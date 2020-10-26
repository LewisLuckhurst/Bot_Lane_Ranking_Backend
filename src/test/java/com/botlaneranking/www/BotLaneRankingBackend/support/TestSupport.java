package com.botlaneranking.www.BotLaneRankingBackend.support;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class TestSupport {
    protected Gson gson = new Gson();

    protected static WireMockServer wireMockServer = new WireMockServer();

    @BeforeAll
    public static void setUp(){
        wireMockServer.start();
    }

    @AfterAll
    public static void tearDown(){
        wireMockServer.stop();
    }
}
