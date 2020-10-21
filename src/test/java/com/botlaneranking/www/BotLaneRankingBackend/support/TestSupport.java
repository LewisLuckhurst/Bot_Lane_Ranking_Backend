package com.botlaneranking.www.BotLaneRankingBackend.support;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestSupport {
    @LocalServerPort
    private int port;

    protected static final String SUMMONER_NAME = "lucky";
    protected JSONObject response;

    protected void botLaneStatisticsRequestForSummoner(String summoner){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost request = new HttpPost(String.format("http://localhost:%s/getBotLaneStatistics", port));
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("summonerName", summoner);
            StringEntity params = new StringEntity(requestBody.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            response = new JSONObject(EntityUtils.toString(httpclient.execute(request).getEntity()));
            httpclient.close();

        } catch (Exception ignored){}
    }
}
