package com.botlaneranking.www.BotLaneRankingBackend.api;

import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.matchlist.MatchListResponse;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.singleMatch.DetailedMatch;
import com.botlaneranking.www.BotLaneRankingBackend.database.Summoner;
import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
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

    private final RateLimiter rateLimiter = RateLimiter.create(0.82);

    @Autowired
    public RiotApiClient(Gson gson) {
        this.gson = gson;
    }

    public Summoner getSummonerBySummonerName(String summonerName) {
        HttpGet request = new HttpGet(apiUrl + format("/lol/summoner/v4/summoners/by-name/%s", summonerName));
        request.setHeader("X-Riot-Token", apiKey);
        request.addHeader("content-type", "application/json");

        try {
            CloseableHttpResponse response = executeRequest(request);
            Summoner summoner = gson.fromJson(EntityUtils.toString(executeRequest(request).getEntity()), Summoner.class);
            response.close();
            return summoner;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MatchListResponse getMatchListFor(String encryptedAccountId, Integer startIndex, Integer endIndex) {
        HttpGet request = new HttpGet(apiUrl + format("/lol/match/v4/matchlists/by-account/%s?queue=420&endIndex=%s&beginIndex=%s&api_key=%s", encryptedAccountId, endIndex, startIndex, apiKey));
        request.setHeader("X-Riot-Token", apiKey);
        request.addHeader("content-type", "application/json");

        try {
            CloseableHttpResponse response = executeRequest(request);
            MatchListResponse matchListResponse = gson.fromJson(EntityUtils.toString(response.getEntity()), MatchListResponse.class);
            response.close();
            return matchListResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DetailedMatch getIndividualMatch(String matchId) {
        HttpGet request = new HttpGet(apiUrl + format("/lol/match/v4/matches/%s?api_key=%s", matchId, apiKey));
        request.setHeader("X-Riot-Token", apiKey);
        request.addHeader("content-type", "application/json");

        try {
            CloseableHttpResponse response = executeRequest(request);
            DetailedMatch detailedMatch = gson.fromJson(EntityUtils.toString(executeRequest(request).getEntity()), DetailedMatch.class);
            response.close();
            return detailedMatch;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CloseableHttpResponse executeRequest(HttpGet request) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        int retryAmount = 10;
        try {
            rateLimiter.acquire();
            CloseableHttpResponse response = httpclient.execute(request);
            while (response.getStatusLine().getStatusCode() != 200 && retryAmount > 0) {
                Thread.sleep(5000);
                rateLimiter.acquire();
                response = httpclient.execute(request);
                retryAmount = retryAmount - 1;
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
