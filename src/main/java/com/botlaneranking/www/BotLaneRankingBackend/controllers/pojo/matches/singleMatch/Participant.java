package com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch;

public class Participant {
    private final String participantId;
    private final String teamId;
    private final String championId;
    private final Stats stats;
    private final TimeLine timeLine;

    public Participant(String participantId, String teamId, String championId, Stats stats, TimeLine timeLine) {
        this.participantId = participantId;
        this.teamId = teamId;
        this.championId = championId;
        this.stats = stats;
        this.timeLine = timeLine;
    }

    public String getParticipantId() {
        return participantId;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getChampionId() {
        return championId;
    }

    public Stats getStats() {
        return stats;
    }

    public TimeLine getTimeLine() {
        return timeLine;
    }
}
