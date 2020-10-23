package com.botlaneranking.www.BotLaneRankingBackend.support;

import com.botlaneranking.www.BotLaneRankingBackend.database.Summoner;

public class SummonerBuilder {
    private String summonerName;
    private String accountId;
    private String encryptedId;
    private String puuid;
    private String summonerLevel;

    public static SummonerBuilder aDefaultSummoner(){
        SummonerBuilder summonerBuilder = new SummonerBuilder();
        summonerBuilder.withSummonerName("RandomSummoner");
        summonerBuilder.withAccountId("123123");
        summonerBuilder.withEncryptedId("323232");
        summonerBuilder.withPuuid("1000");
        summonerBuilder.withSummonerLevel("10");
        return summonerBuilder;
    }

    public SummonerBuilder withSummonerName(String summonerName){
        this.summonerName = summonerName;
        return this;
    }

    public void withAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void withEncryptedId(String encryptedId) {
        this.encryptedId = encryptedId;
    }

    public void withPuuid(String puuid) {
        this.puuid = puuid;
    }

    public void withSummonerLevel(String summonerLevel) {
        this.summonerLevel = summonerLevel;
    }

    public Summoner build(){
        return new Summoner(summonerName, accountId, encryptedId, puuid, summonerLevel);
    }
}
