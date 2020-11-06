package com.botlaneranking.www.BotLaneRankingBackend.controllers;

import com.botlaneranking.www.BotLaneRankingBackend.api.RiotApiClient;
import com.botlaneranking.www.BotLaneRankingBackend.config.pojo.ChampionInfo;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.matchlist.Match;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.matchlist.MatchListResponse;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch.DetailedMatch;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch.Participant;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.responses.SummonerResponse;
import com.botlaneranking.www.BotLaneRankingBackend.database.DynamoDbDao;
import com.botlaneranking.www.BotLaneRankingBackend.database.Summoner;
import com.botlaneranking.www.BotLaneRankingBackend.database.pojo.Supports;
import com.botlaneranking.www.BotLaneRankingBackend.database.pojo.WinLoss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class SummonerController {
    private final DynamoDbDao dao;
    private final RiotApiClient riotApiClient;
    private final ChampionInfo championInfo;
    private final ConcurrentHashMap<String, Summoner> summonersBeingUpdated = new ConcurrentHashMap<>();

    @Autowired
    public SummonerController(DynamoDbDao dao, RiotApiClient riotApiClient, ChampionInfo championInfo) {
        this.dao = dao;
        this.riotApiClient = riotApiClient;
        this.championInfo = championInfo;
    }

    @RequestMapping(value = "/getBotLaneStatistics", method = POST)
    public SummonerResponse getBotLaneStatistics(@RequestBody Map<String, Object> payload) {
        String summonerName = payload.get("summonerName").toString();

        if(summonersBeingUpdated.containsKey(summonerName)){
            Summoner summoner = summonersBeingUpdated.get(summonerName);

            return new SummonerResponse(
                    summonerName,
                    summoner.getSummonerLevel(),
                    summoner.getProfileIconId(),
                    String.valueOf(true),
                    summoner.getChampions(),
                    summoner.getSupports());
        }

        boolean databaseContainsSummoner = dao.containsSummonerName(summonerName);

        Summoner summoner = databaseContainsSummoner ? dao.getUserBySummonerName(summonerName) :
                riotApiClient.getSummonerBySummonerName(summonerName);
        summoner.setMostRecentMatchId("no-most-recent-match");

        if (!databaseContainsSummoner) {
            dao.createNewSummoner(summoner);
        }

        return new SummonerResponse(
                summonerName,
                summoner.getSummonerLevel(),
                summoner.getProfileIconId(),
                String.valueOf(false),
                summoner.getChampions(),
                summoner.getSupports());
    }

    @RequestMapping(value = "/update", method = POST)
    public SseEmitter update(@RequestBody Map<String, Object> payload) {
        String summonerName = payload.get("summonerName").toString();

        SseEmitter emitter = new SseEmitter(-1L);
        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
        sseMvcExecutor.execute(() -> {
            if (!dao.containsSummonerName(summonerName)) {
                try {
                    emitter.send(ResponseEntity
                            .badRequest()
                            .body("Summoner Not in Database"));
                    emitter.complete();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Summoner summoner = dao.getUserBySummonerName(summonerName);

            if(summonersBeingUpdated.containsKey(summonerName)){
                try {
                    emitter.send(ResponseEntity
                            .badRequest()
                            .body("Summoner is already being updated"));
                    emitter.complete();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            summonersBeingUpdated.put(summonerName, summoner);

            try {
                updateSummonerInfo(summoner);
                updateSummonerMatches(summoner, emitter, 0, 100);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return emitter;
    }

    private void updateSummonerMatches(Summoner summoner, SseEmitter emitter, int startIndex, int endIndex) throws IOException {

        MatchListResponse matchList = riotApiClient.getMatchListFor(summoner.getAccountId(), startIndex, endIndex);

        List<Match> adcMatches = matchList.getMatches().stream().filter(match -> match.getLane().equals("BOTTOM")
                && match.getRole().equals("DUO_CARRY")).collect(Collectors.toList());

        for (Match adcMatch : adcMatches) {
            if (adcMatch.getGameId().equals(summoner.getMostRecentMatchId())) {
                summoner.setMostRecentMatchId(adcMatches.get(0).getGameId());
                summonersBeingUpdated.remove(summoner.getName());
                dao.updateChampions(summoner);
                emitter.complete();
                return;
            }

            DetailedMatch individualMatch = riotApiClient.getIndividualMatch(adcMatch.getGameId());

            if (individualMatch.getParticipants() != null) {
                Participant adc = individualMatch.getParticipants().stream().filter(participant ->
                        participant.getChampionId().equals(adcMatch.getChampion()))
                        .findFirst().orElseThrow();

                Participant support = individualMatch.getParticipants().stream().filter(participant ->
                        participant.getTeamId().equals(adc.getTeamId()) &&
                                participant.getTimeline().getRole().equals("DUO_SUPPORT"))
                        .findFirst().orElseThrow();

                updateSummonerAdcStatistics(summoner, adc, support);
                updateSupportStatistics(summoner, adc, support);

                emitter.send(new SummonerResponse(
                        summoner.getName(),
                        summoner.getSummonerLevel(),
                        summoner.getProfileIconId(),
                        String.valueOf(true),
                        summoner.getChampions(),
                        summoner.getSupports()));
            }
        }

        if (startIndex == 0 && !adcMatches.isEmpty()) {
            summoner.setMostRecentMatchId(adcMatches.get(0).getGameId());
        }

        if (matchList.getMatches().size() < 100) {
            emitter.complete();
            summonersBeingUpdated.remove(summoner.getName());
            dao.updateChampions(summoner);
            return;
        }

        dao.updateChampions(summoner);
        updateSummonerMatches(summoner, emitter, startIndex + 100, endIndex + 100);
    }

    private void updateSummonerAdcStatistics(Summoner summoner, Participant adc, Participant support) {
        boolean gameWon = adc.getStats().getWin().equalsIgnoreCase("true");

        String adcName = championInfo.getChampions().get(adc.getChampionId()).getName();
        String supportName = championInfo.getChampions().get(support.getChampionId()).getName();

        if(!summoner.getChampions().containsKey(adcName)){
            summoner.getChampions().put(adcName, new Supports(new HashMap<>()));
        }

        if (!summoner.getChampions().get(adcName).getSupports().containsKey(supportName)) {
            summoner.getChampions().get(adcName).getSupports().put(supportName, new WinLoss("0", "0"));
        }

        if (gameWon) {
            summoner.getChampions().get(adcName).getSupports().get(supportName).incrementWins();
        } else {
            summoner.getChampions().get(adcName).getSupports().get(supportName).incrementLosses();
        }
    }

    private void updateSupportStatistics(Summoner summoner, Participant adc, Participant support) {
        boolean gameWon = adc.getStats().getWin().equalsIgnoreCase("true");

        String supportName = championInfo.getChampions().get(support.getChampionId()).getName();

        if (!summoner.getSupports().containsKey(supportName)) {
            summoner.getSupports().put(supportName, new WinLoss("0", "0"));
        }

        if (gameWon) {
            summoner.getSupports().get(supportName).incrementWins();
        } else {
            summoner.getSupports().get(supportName).incrementLosses();
        }
    }

    private void updateSummonerInfo(Summoner summoner) {
        riotApiClient.getSummonerBySummonerName(summoner.getName());
        dao.updateSummonerInfo(summoner);
        summonersBeingUpdated.put(summoner.getName(), summoner);
    }
}
