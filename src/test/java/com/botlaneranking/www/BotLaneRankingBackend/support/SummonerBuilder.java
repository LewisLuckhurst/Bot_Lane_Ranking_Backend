package com.botlaneranking.www.BotLaneRankingBackend.support;

import com.botlaneranking.www.BotLaneRankingBackend.database.Summoner;
import com.botlaneranking.www.BotLaneRankingBackend.database.pojo.Supports;

import java.util.HashMap;

public class SummonerBuilder {
    private String summonerName;
    private String accountId;
    private String encryptedId;
    private String puuid;
    private String summonerLevel;
    private String profileIcon;
    private String revisionDate;
    private HashMap<String, Supports> champions;

    public static SummonerBuilder aDefaultSummoner(){
        SummonerBuilder summonerBuilder = new SummonerBuilder();
        summonerBuilder.withSummonerName("RandomSummoner");
        summonerBuilder.withAccountId("123123");
        summonerBuilder.withEncryptedId("323232");
        summonerBuilder.withPuuid("1000");
        summonerBuilder.withSummonerLevel("10");
        summonerBuilder.withProfileIcon("300");
        summonerBuilder.withRevisionDate("31321");
        return summonerBuilder;
    }

    public SummonerBuilder withSummonerName(String summonerName){
        this.summonerName = summonerName;
        return this;
    }

    public SummonerBuilder withAccountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public SummonerBuilder withEncryptedId(String encryptedId) {
        this.encryptedId = encryptedId;
        return this;
    }

    public SummonerBuilder withPuuid(String puuid) {
        this.puuid = puuid;
        return this;
    }

    public SummonerBuilder withSummonerLevel(String summonerLevel) {
        this.summonerLevel = summonerLevel;
        return this;
    }

    public SummonerBuilder withProfileIcon(String profileIcon) {
        this.profileIcon = profileIcon;
        return this;
    }

    public SummonerBuilder withRevisionDate(String revisionDate) {
        this.revisionDate = revisionDate;
        return this;
    }

    public SummonerBuilder withChampions(HashMap<String, Supports> champions){
        this.champions = champions;
        return this;
    }

    public Summoner build(){
        return new Summoner(summonerName, accountId, encryptedId, puuid, summonerLevel, profileIcon, revisionDate, champions);
    }
}
