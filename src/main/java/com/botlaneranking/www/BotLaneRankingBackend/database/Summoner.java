package com.botlaneranking.www.BotLaneRankingBackend.database;

import com.botlaneranking.www.BotLaneRankingBackend.database.pojo.Supports;

import java.util.HashMap;

public class Summoner {
    private final String name;
    private final String id;
    private final String accountId;
    private final String summonerLevel;
    private final String puuid;
    private final String profileIconId;
    private final String revisionDate;
    private final HashMap<String, Supports> champions;

    //    champions: [
//            “50”: {
//		        “supports”:[
//			        “40”:{
//				        “wins”:20,
//				        “losses”:10
//                      }
//		        ]
//           ]
//]

    public Summoner(String name, String accountId, String id, String puuid, String summonerLevel, String profileIconId, String revisionDate, HashMap<String, Supports> champions) {
        this.name = name;
        this.accountId = accountId;
        this.id = id;
        this.puuid = puuid;
        this.summonerLevel = summonerLevel;
        this.profileIconId = profileIconId;
        this.revisionDate = revisionDate;
        this.champions = champions;
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

    public HashMap<String, Supports> getChampions() {
        return champions;
    }
}
