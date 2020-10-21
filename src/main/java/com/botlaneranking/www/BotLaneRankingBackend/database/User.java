package com.botlaneranking.www.BotLaneRankingBackend.database;

public class User {
    private final String summonerName;

    public User(String summonerName) {
        this.summonerName = summonerName;
    }

    public String getSummonerName() {
        return summonerName;
    }
}
