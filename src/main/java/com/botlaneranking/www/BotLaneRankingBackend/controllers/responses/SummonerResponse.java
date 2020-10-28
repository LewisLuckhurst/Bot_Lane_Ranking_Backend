package com.botlaneranking.www.BotLaneRankingBackend.controllers.responses;


import com.botlaneranking.www.BotLaneRankingBackend.database.pojo.Supports;

import java.util.HashMap;

public class SummonerResponse {
    private final String summonerName;
    private final String summonerLevel;
    private final String profileIcon;
    private final HashMap<String, Supports> champions;

    public SummonerResponse(String summonerName, String summonerLevel, String profileIcon, HashMap<String, Supports> champions) {
        this.summonerName = summonerName;
        this.summonerLevel = summonerLevel;
        this.profileIcon = profileIcon;
        this.champions = champions;
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

    public HashMap<String, Supports> getChampions() {
        return champions;
    }
}
