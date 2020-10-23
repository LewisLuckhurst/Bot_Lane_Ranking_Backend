package com.botlaneranking.www.BotLaneRankingBackend.database;

public class Summoner {
    private final String summonerName;
    private final String accountId;
    private final String encryptedId;
    private final String puuid;
    private final String summonerLevel;

    public Summoner(String summonerName, String accountId, String encryptedId, String puuid, String summonerLevel) {
        this.summonerName = summonerName;
        this.accountId = accountId;
        this.encryptedId = encryptedId;
        this.puuid = puuid;
        this.summonerLevel = summonerLevel;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getEncryptedId() {
        return encryptedId;
    }

    public String getPuuid() {
        return puuid;
    }

    public String getSummonerLevel() {
        return summonerLevel;
    }
}
