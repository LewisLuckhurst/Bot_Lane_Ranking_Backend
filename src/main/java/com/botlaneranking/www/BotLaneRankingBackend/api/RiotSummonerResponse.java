package com.botlaneranking.www.BotLaneRankingBackend.api;

import com.botlaneranking.www.BotLaneRankingBackend.database.Summoner;

public class RiotSummonerResponse {
    private final String responseCode;
    private final Summoner summoner;

    public RiotSummonerResponse(String responseCode, Summoner summoner) {
        this.responseCode = responseCode;
        this.summoner = summoner;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public Summoner getSummoner() {
        return summoner;
    }
}
