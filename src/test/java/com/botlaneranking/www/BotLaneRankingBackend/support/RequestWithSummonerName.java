package com.botlaneranking.www.BotLaneRankingBackend.support;

public class RequestWithSummonerName {
    private final String summonerName;

    public RequestWithSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    public String getSummonerName() {
        return summonerName;
    }
}
