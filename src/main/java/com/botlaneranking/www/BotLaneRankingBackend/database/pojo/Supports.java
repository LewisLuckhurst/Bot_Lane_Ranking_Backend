package com.botlaneranking.www.BotLaneRankingBackend.database.pojo;

import java.util.HashMap;

public class Supports {
    private final HashMap<String, WinLoss> supports;

    public Supports(HashMap<String, WinLoss> supports) {
        this.supports = supports;
    }

    public HashMap<String, WinLoss> getSupports() {
        return supports;
    }
}
