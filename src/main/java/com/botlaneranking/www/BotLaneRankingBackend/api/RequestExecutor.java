package com.botlaneranking.www.BotLaneRankingBackend.api;

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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

@Component
public class RequestExecutor {
    private PriorityBlockingQueue<Request> requests = new PriorityBlockingQueue<>();
    private final DynamoDbDao dao;
    private final RiotApiClient riotApiClient;
    private final ChampionInfo championInfo;

    @Autowired
    public RequestExecutor(DynamoDbDao dao, RiotApiClient riotApiClient, ChampionInfo championInfo) {
        this.dao = dao;
        this.riotApiClient = riotApiClient;
        this.championInfo = championInfo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void executeRequest() {
        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
        sseMvcExecutor.execute(() -> {
            try {
                while (true) {
                    Request request = requests.take();
                    updateSummoner(request.getSummonerName(), request.getSseEmitter(), request.getStartIndex(), request.getEndIndex());
                }
            } catch (Exception ignored) {
            }
        });
    }

    public void addRequest(Request request) {
        requests.add(request);
    }

    private void updateSummoner(String summonerName, SseEmitter emitter, int startIndex, int endIndex) throws IOException {
        if (!dao.containsSummonerName(summonerName)) {
            throw new RuntimeException("Summoner not in database");
        }

        Summoner summoner = dao.getUserBySummonerName(summonerName);

        MatchListResponse matchList = riotApiClient.getMatchListFor(summoner.getAccountId(), startIndex, endIndex);

        List<Match> adcMatches = matchList.getMatches().stream().filter(match -> match.getLane().equals("BOTTOM")
                && match.getRole().equals("DUO_CARRY")).collect(Collectors.toList());

        for (Match adcMatch : adcMatches) {
            if (adcMatch.getGameId().equals(summoner.getMostRecentMatchId())) {
                summoner.setMostRecentMatchId(adcMatches.get(0).getGameId());
                dao.updateChampions(summoner);
                emitter.complete();
                return;
            }

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

            emitter.send(new SummonerResponse(
                    summonerName,
                    summoner.getSummonerLevel(),
                    summoner.getProfileIconId(),
                    summoner.getChampions()));
        }

        if (startIndex == 0 && !adcMatches.isEmpty()) {
            summoner.setMostRecentMatchId(adcMatches.get(0).getGameId());
        }

        if (matchList.getMatches().size() < 100) {
            emitter.complete();
            dao.updateChampions(summoner);
            return;
        }

        dao.updateChampions(summoner);
        int priority = ((startIndex + 100) / 100) + 1;
        addRequest(new Request(priority, summonerName, emitter, startIndex + 100, endIndex + 100));
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
