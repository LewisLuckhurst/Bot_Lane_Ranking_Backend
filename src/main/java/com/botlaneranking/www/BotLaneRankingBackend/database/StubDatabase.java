package com.botlaneranking.www.BotLaneRankingBackend.database;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class StubDatabase {
    private HashMap<String, Summoner> summonerHashMap = new HashMap<>();

    public Summoner getUserBySummonerName(String summonerName) {
        return summonerHashMap.get(summonerName);
    }

    public boolean containsSummonerName(String summonerName) {
        return summonerHashMap.containsKey(summonerName);
    }

    public void createNewSummoner(Summoner summoner) {
        summonerHashMap.put(summoner.getName().toLowerCase(), new Summoner(
                summoner.getName().toLowerCase(),
                summoner.getAccountId(),
                summoner.getId(),
                summoner.getPuuid(),
                summoner.getSummonerLevel(),
                summoner.getProfileIconId(),
                summoner.getRevisionDate(),
                new HashMap<>(),
                "none",
                new HashMap<>()
        ));
    }

    public void updateChampions(Summoner summoner) {
        summonerHashMap.put(summoner.getName().toLowerCase(), summoner);
    }
}
