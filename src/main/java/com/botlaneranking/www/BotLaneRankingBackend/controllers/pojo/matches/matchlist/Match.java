package com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.matchlist;

public class Match {
    private final String gameId;
    private final String champion;
    private final Integer queue;
    private final Integer season;
    private final String role;
    private final String lane;

    public Match(String gameId, String champion, Integer queue, Integer season, String role, String lane) {
        this.gameId = gameId;
        this.champion = champion;
        this.queue = queue;
        this.season = season;
        this.role = role;
        this.lane = lane;
    }

    public String getGameId() {
        return gameId;
    }

    public String getChampion() {
        return champion;
    }

    public Integer getQueue() {
        return queue;
    }

    public Integer getSeason() {
        return season;
    }

    public String getRole() {
        return role;
    }

    public String getLane() {
        return lane;
    }
}