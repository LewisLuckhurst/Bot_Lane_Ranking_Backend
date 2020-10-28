package com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch;

import java.util.List;

public class DetailedMatch {
    private final String gameId;
    private final List<Participant> participants;

    public DetailedMatch(String gameId, List<Participant> participants) {
        this.gameId = gameId;
        this.participants = participants;
    }

    public String getGameId() {
        return gameId;
    }

    public List<Participant> getParticipants() {
        return participants;
    }
}