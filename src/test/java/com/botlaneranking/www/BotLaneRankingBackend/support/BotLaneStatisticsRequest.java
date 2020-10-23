package com.botlaneranking.www.BotLaneRankingBackend.support;

public class BotLaneStatisticsRequest {
    private final String summonerName;

    public BotLaneStatisticsRequest(String summonerName) {
        this.summonerName = summonerName;
    }

    public String getSummonerName() {
        return summonerName;
    }
}
