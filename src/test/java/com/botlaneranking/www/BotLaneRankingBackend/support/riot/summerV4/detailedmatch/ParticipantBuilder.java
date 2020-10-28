package com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.detailedmatch;

import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch.Participant;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch.Stats;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch.TimeLine;

import static com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.detailedmatch.StatsBuilder.aDefaultStatsBuilder;
import static com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.detailedmatch.TimeLineBuilder.aDefaultTimeLine;

public class ParticipantBuilder {
    private String participantId;
    private String teamId;
    private String championId;
    private Stats stats;
    private TimeLine timeLine;

    public static ParticipantBuilder aDefaultParticipant(){
        ParticipantBuilder participantBuilder = new ParticipantBuilder();
        participantBuilder.withParticipantId("1");
        participantBuilder.withTeamId("100");
        participantBuilder.withChampionId("20");
        participantBuilder.withStats(aDefaultStatsBuilder().build());
        participantBuilder.withTimeLine(aDefaultTimeLine().build());
        return participantBuilder;
    }

    public ParticipantBuilder withParticipantId(String participantId) {
        this.participantId = participantId;
        return this;
    }

    public ParticipantBuilder withTeamId(String teamId) {
        this.teamId = teamId;
        return this;
    }

    public ParticipantBuilder withChampionId(String championId) {
        this.championId = championId;
        return this;
    }

    public ParticipantBuilder withStats(Stats stats) {
        this.stats = stats;
        return this;
    }

    public ParticipantBuilder withTimeLine(TimeLine timeLine) {
        this.timeLine = timeLine;
        return this;
    }

    public Participant build(){
        return new Participant(participantId, teamId, championId, stats, timeLine);
    }
}
