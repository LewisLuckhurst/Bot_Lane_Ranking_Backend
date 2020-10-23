package com.botlaneranking.www.BotLaneRankingBackend.controllers.responses;

public class BotLaneStatisticsResponse {
    private final String summonerName;
    private final Integer summonerLevel;
    private final Integer profileIcon;

    public BotLaneStatisticsResponse(String summonerName, Integer summonerLevel, Integer profileIcon) {
        this.summonerName = summonerName;
        this.summonerLevel = summonerLevel;
        this.profileIcon = profileIcon;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public Integer getSummonerLevel() {
        return summonerLevel;
    }

    public Integer getProfileIcon() {
        return profileIcon;
    }
}
