package com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4;

import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.matchlist.Match;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.matchlist.MatchListResponse;

import java.util.List;

public class MatchListResponseBuilder {
    private List<Match> matches;
    private Integer startIndex;
    private Integer endIndex;

    public static MatchListResponseBuilder aDefaultMatchListResponse(){
        MatchListResponseBuilder matchList = new MatchListResponseBuilder();
        matchList.withEndIndex(100);
        matchList.withStartIndex(0);
        return matchList;
    }

    public MatchListResponseBuilder withStartIndex(Integer startIndex){
        this.startIndex = startIndex;
        return this;
    }

    public MatchListResponseBuilder withEndIndex(Integer endIndex){
        this.endIndex = endIndex;
        return this;
    }

    public MatchListResponseBuilder withMatches(List<Match> matches){
        this.matches = matches;
        return this;
    }

    public MatchListResponse build(){
        return new MatchListResponse(matches, startIndex, endIndex);
    }
}
