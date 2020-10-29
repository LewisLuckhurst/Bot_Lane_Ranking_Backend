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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class SummonerController {
    private final DynamoDbDao dao;
    private final RiotApiClient riotApiClient;
    private final ChampionInfo championInfo;

    @Autowired
    public SummonerController(DynamoDbDao dao, RiotApiClient riotApiClient, ChampionInfo championInfo) {
        this.dao = dao;
        this.riotApiClient = riotApiClient;
        this.championInfo = championInfo;
    }

    @RequestMapping(value = "/getBotLaneStatistics", method = POST)
    public SummonerResponse getBotLaneStatistics(@RequestBody Map<String, Object> payload) {
        String summonerName = payload.get("summonerName").toString();

        boolean databaseContainsSummoner = dao.containsSummonerName(summonerName);

        Summoner summoner = databaseContainsSummoner ? dao.getUserBySummonerName(summonerName) :
                riotApiClient.getSummonerBySummonerName(summonerName);

        if (!databaseContainsSummoner) {
            dao.createNewSummoner(summoner);
        }

        return new SummonerResponse(
                summonerName,
                summoner.getSummonerLevel(),
                summoner.getProfileIconId(),
                summoner.getChampions());
    }

    @RequestMapping(value = "/update", method = POST)
    public SummonerResponse updateRecord(@RequestBody Map<String, Object> payload) {
        String summonerName = payload.get("summonerName").toString();

        if (!dao.containsSummonerName(summonerName)) {
            throw new RuntimeException("Summoner not in database");
        }

        Summoner summoner = dao.getUserBySummonerName(summonerName);
        int startIndex = 0;
        int endIndex = 100;

        while (true) {
            MatchListResponse matchList = riotApiClient.getMatchListFor(summoner.getAccountId(), startIndex, endIndex);

            List<Match> adcMatches = matchList.getMatches().stream().filter(match -> match.getLane().equals("BOTTOM")
                    && match.getRole().equals("DUO_CARRY")).collect(Collectors.toList());

            for (Match adcMatch : adcMatches) {
                DetailedMatch individualMatch = riotApiClient.getIndividualMatch(adcMatch.getGameId());

                Participant adc = individualMatch.getParticipants().stream().filter(participant ->
                        participant.getChampionId().equals(adcMatch.getChampion()))
                        .findFirst().orElseThrow();

                Participant support = individualMatch.getParticipants().stream().filter(participant ->
                        participant.getTeamId().equals(adc.getTeamId()) &&
                                participant.getTimeLine().getLane().equals("NONE") &&
                                participant.getTimeLine().getRole().equals("DUO_SUPPORT"))
                        .findFirst().orElseThrow();

                updateSummonerAdcStatistics(summoner, adc, support);
            }
            if(matchList.getMatches().size() < 100){
                break;
            }
            startIndex = startIndex + 100;
            endIndex = endIndex + 100;
        }

        dao.updateChampions(summoner);

        return new SummonerResponse(
                summonerName,
                summoner.getSummonerLevel(),
                summoner.getProfileIconId(),
                summoner.getChampions());
    }

    private void updateSummonerAdcStatistics(Summoner summoner, Participant adc, Participant support) {
        boolean gameWon = adc.getStats().getWin().equalsIgnoreCase("true");

        String adcName = championInfo.getChampions().get(adc.getChampionId()).getName();
        String supportName = championInfo.getChampions().get(support.getChampionId()).getName();

        if (summoner.getChampions().containsKey(adcName)) {
            if (summoner.getChampions().get(adcName).getSupports().containsKey(supportName)) {
                WinLoss winLoss = summoner.getChampions().get(adcName).getSupports().get(supportName);

                if (gameWon) {
                    winLoss.incrementWins();
                    return;
                }
                winLoss.incrementLosses();
                return;
            }

            if (gameWon) {
                summoner.getChampions().get(adcName).getSupports()
                        .put(supportName, new WinLoss("1", "0"));
                return;
            }
            summoner.getChampions().get(adcName).getSupports()
                    .put(supportName, new WinLoss("0", "1"));
            return;
        }

        HashMap<String, WinLoss> winLossHashMap = new HashMap<>();

        if (gameWon) {
            winLossHashMap.put(supportName, new WinLoss("1", "0"));
        } else {
            winLossHashMap.put(supportName, new WinLoss("0", "1"));
        }

        summoner.getChampions().put(adcName, new Supports(winLossHashMap));
    }
}
