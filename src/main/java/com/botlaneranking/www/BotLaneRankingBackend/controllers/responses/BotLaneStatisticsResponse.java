package com.botlaneranking.www.BotLaneRankingBackend.controllers.responses;

public class BotLaneStatisticsResponse {
    private final String summonerName;

    public BotLaneStatisticsResponse(String summonerName) {
        this.summonerName = summonerName;
    }

    public String getSummonerName() {
        return summonerName;
    }
}
