package com.botlaneranking.www.BotLaneRankingBackend.controllers;

import com.botlaneranking.www.BotLaneRankingBackend.controllers.responses.BotLaneStatisticsResponse;
import com.botlaneranking.www.BotLaneRankingBackend.database.DynamoDbDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class EntryPoint {
    public final DynamoDbDao dao;

    @Autowired
    public EntryPoint(DynamoDbDao dao) {
        this.dao = dao;
    }

    @RequestMapping(value = "/getBotLaneStatistics", method = POST)
    public BotLaneStatisticsResponse getBotLaneStatistics(@RequestBody Map<String, Object> payload){
        String summonerName = payload.get("summonerName").toString();
        if(dao.containSummonerName(summonerName)) {
            return new BotLaneStatisticsResponse(dao.getUserBySummonerName(summonerName).getSummonerName());
        }
        return null;
    }
}
