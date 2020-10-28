package com.botlaneranking.www.BotLaneRankingBackend.config.pojo;

public class Champion {
    private final String key;
    private final String name;

    public Champion(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
