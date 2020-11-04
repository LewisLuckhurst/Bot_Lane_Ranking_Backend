package com.botlaneranking.www.BotLaneRankingBackend.api;

import com.botlaneranking.www.BotLaneRankingBackend.config.pojo.RateLimiterComponent;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.matchlist.MatchListResponse;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch.DetailedMatch;
import com.botlaneranking.www.BotLaneRankingBackend.database.Summoner;
import com.google.gson.Gson;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class RiotApiClient {
    @Value("${riot.api.url}")
    private String apiUrl;

    @Value("${riot.api.key}")
    private String apiKey;

    public Gson gson;

    private RateLimiterComponent rateLimiterComponent;

    @Autowired
    public RiotApiClient(Gson gson, RateLimiterComponent rateLimiterComponent1){
        this.gson = gson;
        this.rateLimiterComponent = rateLimiterComponent1;
    }

    public Summoner getSummonerBySummonerName(String summonerName){
        CloseableHttpClient httpclient = HttpClients.createDefault();

        rateLimiterComponent.getRateLimiter().acquire();
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

    public MatchListResponse getMatchListFor(String encryptedAccountId, Integer startIndex, Integer endIndex){
        CloseableHttpClient httpclient = HttpClients.createDefault();

        rateLimiterComponent.getRateLimiter().acquire();
        HttpGet request = new HttpGet(apiUrl + format("/lol/match/v4/matchlists/by-account/%s?queue=420&endIndex=%s&beginIndex=%s&api_key=%s", encryptedAccountId, endIndex, startIndex, apiKey));
        request.setHeader("X-Riot-Token", apiKey);
        request.addHeader("content-type", "application/json");

        try {
            MatchListResponse matchListResponse = gson.fromJson(EntityUtils.toString(httpclient.execute(request).getEntity()), MatchListResponse.class);
            httpclient.close();
            return matchListResponse;

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public DetailedMatch getIndividualMatch(String matchId){
        CloseableHttpClient httpclient = HttpClients.createDefault();

        rateLimiterComponent.getRateLimiter().acquire();
        HttpGet request = new HttpGet(apiUrl + format("/lol/match/v4/matches/%s?api_key=%s", matchId, apiKey));
        request.setHeader("X-Riot-Token", apiKey);
        request.addHeader("content-type", "application/json");

        try {
            DetailedMatch detailedMatch = gson.fromJson(EntityUtils.toString(httpclient.execute(request).getEntity()), DetailedMatch.class);
            httpclient.close();
            return detailedMatch;

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
