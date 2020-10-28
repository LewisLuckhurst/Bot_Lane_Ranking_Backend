package com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.detailedmatch;

import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch.Stats;

public class StatsBuilder {
    private String participantId;
    private String win;

    public static StatsBuilder aDefaultStatsBuilder(){
        StatsBuilder statsBuilder = new StatsBuilder();
        statsBuilder.withParticipantId("1");
        statsBuilder.withWin("false");
        return statsBuilder;
    }

    public StatsBuilder withParticipantId(String participantId) {
        this.participantId = participantId;
        return this;
    }

    public StatsBuilder withWin(String win) {
        this.win = win;
        return this;
    }

    public Stats build(){
        return new Stats(participantId, win);
    }
}
