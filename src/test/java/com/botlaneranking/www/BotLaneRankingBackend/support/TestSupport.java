package com.botlaneranking.www.BotLaneRankingBackend.support;

import com.botlaneranking.www.BotLaneRankingBackend.controllers.responses.BotLaneStatisticsResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.gson.Gson;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties()
public class TestSupport {
    @LocalServerPort
    private int port;

    @Autowired
    public Gson gson;

    protected static WireMockServer wireMockServer = new WireMockServer();

    @BeforeAll
    public static void setUp(){
        wireMockServer.start();
    }

    @AfterAll
    public static void tearDown(){
        wireMockServer.stop();
    }

    protected static final String SUMMONER_NAME = "lucky";
    protected static final String API_KEY = "riotapikey";

    protected BotLaneStatisticsResponse botLaneStatisticsRequestForSummoner(String summoner){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost request = new HttpPost(String.format("http://localhost:%s/getBotLaneStatistics", port));
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("summonerName", summoner);
            StringEntity params = new StringEntity(requestBody.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            BotLaneStatisticsResponse response = gson.fromJson(EntityUtils.toString(httpclient.execute(request).getEntity()), BotLaneStatisticsResponse.class);
            httpclient.close();
            return response;

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
