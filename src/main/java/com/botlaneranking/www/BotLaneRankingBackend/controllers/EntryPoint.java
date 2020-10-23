package com.botlaneranking.www.BotLaneRankingBackend.controllers;

import com.botlaneranking.www.BotLaneRankingBackend.api.RiotApiClient;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.responses.BotLaneStatisticsResponse;
import com.botlaneranking.www.BotLaneRankingBackend.database.DynamoDbDao;
import com.botlaneranking.www.BotLaneRankingBackend.database.Summoner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class EntryPoint {
    public final DynamoDbDao dao;
    public final RiotApiClient riotApiClient;

    @Autowired
    public EntryPoint(DynamoDbDao dao, RiotApiClient riotApiClient) {
        this.dao = dao;
        this.riotApiClient = riotApiClient;
    }

    @RequestMapping(value = "/getBotLaneStatistics", method = POST)
    public BotLaneStatisticsResponse getBotLaneStatistics(@RequestBody Map<String, Object> payload){
        String summonerName = payload.get("summonerName").toString();
        Summoner summoner;
        if(dao.containsSummonerName(summonerName)) {
            summoner = dao.getUserBySummonerName(summonerName);
        } else {
            summoner = riotApiClient.getSummonerBySummonerName(summonerName);
            dao.createNewSummoner(summonerName, summoner.getId(), summoner.getAccountId(),
                    summoner.getSummonerLevel(), summoner.getPuuid(), summoner.getProfileIconId(), summoner.getRevisionDate());
        }

        return new BotLaneStatisticsResponse(
                summonerName,
                summoner.getSummonerLevel(),
                summoner.getProfileIconId()
        );
    }
}
