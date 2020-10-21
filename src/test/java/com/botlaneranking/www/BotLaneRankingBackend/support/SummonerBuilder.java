package com.botlaneranking.www.BotLaneRankingBackend.support;

import com.botlaneranking.www.BotLaneRankingBackend.database.Summoner;

public class SummonerBuilder {
    private String summonerName;

    public static SummonerBuilder aDefaultSummoner(){
        SummonerBuilder summonerBuilder = new SummonerBuilder();
        summonerBuilder.withSummonerName("RandomSummoner");
        return summonerBuilder;
    }

    public SummonerBuilder withSummonerName(String summonerName){
        this.summonerName = summonerName;
        return this;
    }

    public Summoner build(){
        return new Summoner(summonerName);
    }
}
