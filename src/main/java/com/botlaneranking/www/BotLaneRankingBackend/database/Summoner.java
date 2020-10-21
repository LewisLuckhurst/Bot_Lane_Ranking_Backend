package com.botlaneranking.www.BotLaneRankingBackend.database;

public class Summoner {
    private final String summonerName;

    public Summoner(String summonerName) {
        this.summonerName = summonerName;
    }

    public String getSummonerName() {
        return summonerName;
    }
}
