package com.botlaneranking.www.BotLaneRankingBackend.database;

public class Summoner {
    private final String summonerName;
    private final String id;
    private final String accountId;
    private final Integer summonerLevel;
    private final String puuid;
    private final Integer profileIconId;
    private final String revisionDate;

    public Summoner(String summonerName, String accountId, String id, String puuid, Integer summonerLevel, Integer profileIconId, String revisionDate) {
        this.summonerName = summonerName;
        this.accountId = accountId;
        this.id = id;
        this.puuid = puuid;
        this.summonerLevel = summonerLevel;
        this.profileIconId = profileIconId;
        this.revisionDate = revisionDate;
    }

    public String getSummonerName() {
        return summonerName;
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

    public Integer getSummonerLevel() {
        return summonerLevel;
    }

    public Integer getProfileIconId() {
        return profileIconId;
    }

    public String getRevisionDate() {
        return revisionDate;
    }
}
