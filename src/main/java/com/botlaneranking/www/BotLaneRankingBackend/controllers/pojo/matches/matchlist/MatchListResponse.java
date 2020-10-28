package com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.matchlist;

import java.util.List;

public class MatchListResponse {
    private final List<Match> matches;
    private final Integer startIndex;
    private final Integer endIndex;

    public MatchListResponse(List<Match> matches, Integer startIndex, Integer endIndex) {
        this.matches = matches;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public Integer getEndIndex() {
        return endIndex;
    }
}