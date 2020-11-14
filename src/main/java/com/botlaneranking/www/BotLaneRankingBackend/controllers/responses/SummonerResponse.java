package com.botlaneranking.www.BotLaneRankingBackend.controllers.responses;


import com.botlaneranking.www.BotLaneRankingBackend.database.pojo.Supports;
import com.botlaneranking.www.BotLaneRankingBackend.database.pojo.WinLoss;

import java.util.HashMap;

public class SummonerResponse {
    private final String summonerName;
    private final String summonerLevel;
    private final String profileIcon;
    private final String updateInProgress;
    private final HashMap<String, Supports> champions;
    private final HashMap<String, WinLoss> supports;

    public SummonerResponse(String summonerName, String summonerLevel, String profileIcon, String updateInProgress, HashMap<String, Supports> champions, HashMap<String, WinLoss> supports) {
        this.summonerName = summonerName;
        this.summonerLevel = summonerLevel;
        this.profileIcon = profileIcon;
        this.updateInProgress = updateInProgress;
        this.champions = champions;
        this.supports = supports;
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

    public HashMap<String, WinLoss> getSupports() {
        return supports;
    }

    public String getUpdateInProgress() {
        return updateInProgress;
    }
}
