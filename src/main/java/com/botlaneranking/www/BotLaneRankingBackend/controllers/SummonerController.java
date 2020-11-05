package com.botlaneranking.www.BotLaneRankingBackend.controllers;

import com.botlaneranking.www.BotLaneRankingBackend.api.Request;
import com.botlaneranking.www.BotLaneRankingBackend.api.RequestExecutor;
import com.botlaneranking.www.BotLaneRankingBackend.api.RiotApiClient;
import com.botlaneranking.www.BotLaneRankingBackend.config.pojo.RateLimiterComponent;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.responses.SummonerResponse;
import com.botlaneranking.www.BotLaneRankingBackend.database.DynamoDbDao;
import com.botlaneranking.www.BotLaneRankingBackend.database.Summoner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class SummonerController {
    private final DynamoDbDao dao;
    private final RiotApiClient riotApiClient;
    private final RequestExecutor requestExecutor;
    private final RateLimiterComponent rateLimiterComponent;

    @Autowired
    public SummonerController(DynamoDbDao dao, RiotApiClient riotApiClient, RequestExecutor requestExecutor, RateLimiterComponent rateLimiterComponent) {
        this.dao = dao;
        this.riotApiClient = riotApiClient;
        this.requestExecutor = requestExecutor;
        this.rateLimiterComponent = rateLimiterComponent;
    }

    @RequestMapping(value = "/getBotLaneStatistics", method = POST)
    public SummonerResponse getBotLaneStatistics(@RequestBody Map<String, Object> payload) {
        String summonerName = payload.get("summonerName").toString();

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
                summoner.getChampions());
    }

    @RequestMapping(value = "/update", method = POST)
    public SseEmitter update(@RequestBody Map<String, Object> payload) {
        String summonerName = payload.get("summonerName").toString();
        SseEmitter emitter = new SseEmitter();
        requestExecutor.addRequest(new Request(1, summonerName, emitter, 0, 100));
        return emitter;
    }
}
