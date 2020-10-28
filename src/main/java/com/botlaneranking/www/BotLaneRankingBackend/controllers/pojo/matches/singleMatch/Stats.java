package com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch;

public class Stats {
    private final String participantId;
    private final String win;

    public Stats(String participantId, String win) {
        this.participantId = participantId;
        this.win = win;
    }

    public String getParticipantId() {
        return participantId;
    }

    public String getWin() {
        return win;
    }
}