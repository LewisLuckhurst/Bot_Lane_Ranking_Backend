package com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch;

public class TimeLine {
    private final String role;
    private final String lane;

    public TimeLine(String role, String lane) {
        this.role = role;
        this.lane = lane;
    }

    public String getRole() {
        return role;
    }

    public String getLane() {
        return lane;
    }
}
