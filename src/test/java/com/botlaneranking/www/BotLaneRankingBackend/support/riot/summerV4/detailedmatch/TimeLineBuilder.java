package com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.detailedmatch;

import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch.TimeLine;

public class TimeLineBuilder {
    private String role;
    private String lane;

    public static TimeLineBuilder aDefaultTimeLine(){
        TimeLineBuilder timeLineBuilder = new TimeLineBuilder();
        timeLineBuilder.withLane("TOP");
        timeLineBuilder.withRole("SOLO");
        return timeLineBuilder;
    }

    public TimeLineBuilder withRole(String role) {
        this.role = role;
        return this;
    }

    public TimeLineBuilder withLane(String lane) {
        this.lane = lane;
        return this;
    }

    public TimeLine build(){
        return new TimeLine(role, lane);
    }
}
