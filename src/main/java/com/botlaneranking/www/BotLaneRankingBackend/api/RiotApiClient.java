package com.botlaneranking.www.BotLaneRankingBackend.api;

import com.botlaneranking.www.BotLaneRankingBackend.database.Summoner;
import com.google.gson.Gson;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;

import static java.lang.String.format;

public class RiotApiClient {
    @Value("${riot.api.url}")
    private String apiUrl;

    @Value("${riot.api.key}")
    private String apiKey;

    public Gson gson;

    public RiotApiClient(Gson gson){
        this.gson = gson;
    }

    public Summoner getSummonerBySummonerName(String summonerName){
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet request = new HttpGet(apiUrl + format("/lol/summoner/v4/summoners/by-name/%s", summonerName));
        request.setHeader("X-Riot-Token", apiKey);
        request.addHeader("content-type", "application/json");

        try {
            Summoner summoner = gson.fromJson(EntityUtils.toString(httpclient.execute(request).getEntity()), Summoner.class);
            httpclient.close();
            return summoner;

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
