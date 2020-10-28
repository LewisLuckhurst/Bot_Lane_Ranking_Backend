package com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4;

import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.matchlist.Match;

public class MatchBuilder {
    private String platformId;
    private String gameId;
    private String champion;
    private Integer queue;
    private Integer season;
    private String timeStamp;
    private String role;
    private String lane;

    public static MatchBuilder aDefaultMatch(){
        MatchBuilder matchBuilder = new MatchBuilder();
        matchBuilder.withPlatformId("EUW1");
        matchBuilder.withGameId("500");
        matchBuilder.withChampion("51");
        matchBuilder.withQueue(420);
        matchBuilder.withSeason(13);
        matchBuilder.withTimeStamp("1603786864341");
        matchBuilder.withRole("DUO_CARRY");
        matchBuilder.withLane("BOTTOM");
        return matchBuilder;
    }

    public MatchBuilder withPlatformId(String platformId) {
        this.platformId = platformId;
        return this;
    }

    public MatchBuilder withGameId(String gameId) {
        this.gameId = gameId;
        return this;
    }

    public MatchBuilder withChampion(String champion) {
        this.champion = champion;
        return this;
    }

    public MatchBuilder withQueue(Integer queue) {
        this.queue = queue;
        return this;
    }

    public MatchBuilder withSeason(Integer season) {
        this.season = season;
        return this;
    }

    public MatchBuilder withTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public MatchBuilder withRole(String role) {
        this.role = role;
        return this;
    }

    public MatchBuilder withLane(String lane) {
        this.lane = lane;
        return this;
    }

    public Match build(){
        return new Match(gameId, champion, queue, season, role, lane);
    }
}
