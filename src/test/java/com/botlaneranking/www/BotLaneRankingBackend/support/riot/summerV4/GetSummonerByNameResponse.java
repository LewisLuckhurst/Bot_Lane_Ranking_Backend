package com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4;

public class GetSummonerByNameResponse {
    private String id;
    private String accountId;
    private String puuid;
    private String name;
    private Integer profileIconId;
    private String revisionDate;
    private Integer summonerLevel;

    public static GetSummonerByNameResponse aDefaultGetSummonerByNameResponse(){
        GetSummonerByNameResponse getSummonerByNameResponse = new GetSummonerByNameResponse();
        getSummonerByNameResponse.withId("500");
        getSummonerByNameResponse.withAccountId("YJEw123");
        getSummonerByNameResponse.withPuuid("123123");
        getSummonerByNameResponse.withName("lucky");
        getSummonerByNameResponse.withProfileIconId(749);
        getSummonerByNameResponse.withSummonerLevel(45);
        getSummonerByNameResponse.withRevisionDate("1602798176000");
        return getSummonerByNameResponse;
    }


    public GetSummonerByNameResponse withId(String id) {
        this.id = id;
        return this;
    }

    public GetSummonerByNameResponse withAccountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public GetSummonerByNameResponse withPuuid(String puuid) {
        this.puuid = puuid;
        return this;
    }

    public GetSummonerByNameResponse withName(String name) {
        this.name = name;
        return this;
    }

    public GetSummonerByNameResponse withProfileIconId(Integer profileIconId) {
        this.profileIconId = profileIconId;
        return this;
    }

    public GetSummonerByNameResponse withRevisionDate(String revisionDate) {
        this.revisionDate = revisionDate;
        return this;
    }

    public GetSummonerByNameResponse withSummonerLevel(Integer summonerLevel) {
        this.summonerLevel = summonerLevel;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getPuuid() {
        return puuid;
    }

    public String getName() {
        return name;
    }

    public Integer getProfileIconId() {
        return profileIconId;
    }

    public String getRevisionDate() {
        return revisionDate;
    }

    public Integer getSummonerLevel() {
        return summonerLevel;
    }
}
