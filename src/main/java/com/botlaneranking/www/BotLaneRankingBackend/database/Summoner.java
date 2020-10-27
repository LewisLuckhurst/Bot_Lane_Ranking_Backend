package com.botlaneranking.www.BotLaneRankingBackend.database;

public class Summoner {
    private final String name;
    private final String id;
    private final String accountId;
    private final String summonerLevel;
    private final String puuid;
    private final String profileIconId;
    private final String revisionDate;

    public Summoner(String name, String accountId, String id, String puuid, String summonerLevel, String profileIconId, String revisionDate) {
        this.name = name;
        this.accountId = accountId;
        this.id = id;
        this.puuid = puuid;
        this.summonerLevel = summonerLevel;
        this.profileIconId = profileIconId;
        this.revisionDate = revisionDate;
    }

    public String getName() {
        return name;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getId() {
        return id;
    }

    public String getPuuid() {
        return puuid;
    }

    public String getSummonerLevel() {
        return summonerLevel;
    }

    public String getProfileIconId() {
        return profileIconId;
    }

    public String getRevisionDate() {
        return revisionDate;
    }
}
