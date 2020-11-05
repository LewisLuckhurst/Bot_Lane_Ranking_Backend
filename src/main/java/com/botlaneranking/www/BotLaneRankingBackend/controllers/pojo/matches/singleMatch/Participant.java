package com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch;

public class Participant {
    private final String participantId;
    private final String teamId;
    private final String championId;
    private final Stats stats;
    private final TimeLine timeline;

    public Participant(String participantId, String teamId, String championId, Stats stats, TimeLine timeline) {
        this.participantId = participantId;
        this.teamId = teamId;
        this.championId = championId;
        this.stats = stats;
        this.timeline = timeline;
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

    public TimeLine getTimeline() {
        return timeline;
    }
}
