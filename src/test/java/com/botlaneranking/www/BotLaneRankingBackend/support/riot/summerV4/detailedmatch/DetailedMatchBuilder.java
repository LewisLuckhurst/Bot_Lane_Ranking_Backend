package com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.detailedmatch;

import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch.DetailedMatch;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch.Participant;

import java.util.List;

import static com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.detailedmatch.ParticipantBuilder.aDefaultParticipant;
import static java.util.Collections.singletonList;

public class DetailedMatchBuilder {
    private String gameId;
    private List<Participant> participantList;

    public static DetailedMatchBuilder aDefaultDetailedMatch(){
        DetailedMatchBuilder detailedMatchBuilder = new DetailedMatchBuilder();
        detailedMatchBuilder.withGameId("500");
        detailedMatchBuilder.withParticipantList(singletonList(aDefaultParticipant().build()));
        return detailedMatchBuilder;
    }

    public DetailedMatchBuilder withGameId(String gameId) {
        this.gameId = gameId;
        return this;
    }

    public DetailedMatchBuilder withParticipantList(List<Participant> participantList) {
        this.participantList = participantList;
        return this;
    }

    public DetailedMatch build(){
        return new DetailedMatch(gameId, participantList);
    }
}
