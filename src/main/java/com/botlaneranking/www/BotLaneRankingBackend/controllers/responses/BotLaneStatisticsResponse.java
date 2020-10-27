package com.botlaneranking.www.BotLaneRankingBackend.controllers.responses;

public class BotLaneStatisticsResponse {
    private final String summonerName;
    private final String summonerLevel;
    private final String profileIcon;

    public BotLaneStatisticsResponse(String summonerName, String summonerLevel, String profileIcon) {
        this.summonerName = summonerName;
        this.summonerLevel = summonerLevel;
        this.profileIcon = profileIcon;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public String getSummonerLevel() {
        return summonerLevel;
    }

    public String getProfileIcon() {
        return profileIcon;
    }
}
