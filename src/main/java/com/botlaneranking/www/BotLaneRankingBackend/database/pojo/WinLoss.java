package com.botlaneranking.www.BotLaneRankingBackend.database.pojo;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

public class WinLoss {
    private String wins;
    private String losses;

    public WinLoss(String wins, String losses) {
        this.wins = wins;
        this.losses = losses;
    }

    public String getWins() {
        return wins;
    }

    public String getLosses() {
        return losses;
    }

    public void incrementWins(){
        wins = valueOf(parseInt(wins) + 1);
    }

    public void incrementLosses(){
        losses = valueOf(parseInt(losses) + 1);
    }
}
