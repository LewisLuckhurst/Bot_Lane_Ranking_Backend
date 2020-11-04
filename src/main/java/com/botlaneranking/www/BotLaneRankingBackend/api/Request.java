package com.botlaneranking.www.BotLaneRankingBackend.api;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class Request implements Comparable<Request> {
    private final int priority;
    private String summonerName;
    private SseEmitter sseEmitter;
    private int startIndex;
    private int endIndex;

    public Request(int priority, String summonerName, SseEmitter sseEmitter, int startIndex, int endIndex) {
        this.priority = priority;
        this.summonerName = summonerName;
        this.sseEmitter = sseEmitter;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getPriority() {
        return priority;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public SseEmitter getSseEmitter() {
        return sseEmitter;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    @Override
    public int compareTo(Request request){
        return Integer.compare(priority, request.getPriority());
    }
}
