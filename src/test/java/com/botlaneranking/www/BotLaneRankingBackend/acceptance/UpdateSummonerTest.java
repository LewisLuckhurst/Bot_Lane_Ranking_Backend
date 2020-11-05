package com.botlaneranking.www.BotLaneRankingBackend.acceptance;

import com.botlaneranking.www.BotLaneRankingBackend.controllers.SummonerController;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.pojo.matches.matchlist.Match;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.responses.SummonerResponse;
import com.botlaneranking.www.BotLaneRankingBackend.support.RequestWithSummonerName;
import com.botlaneranking.www.BotLaneRankingBackend.support.TestSupport;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.botlaneranking.www.BotLaneRankingBackend.support.SummonerBuilder.aDefaultSummoner;
import static com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.GetSummonerByNameResponse.aDefaultGetSummonerByNameResponse;
import static com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.MatchBuilder.aDefaultMatch;
import static com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.MatchListResponseBuilder.aDefaultMatchListResponse;
import static com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.detailedmatch.DetailedMatchBuilder.aDefaultDetailedMatch;
import static com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.detailedmatch.ParticipantBuilder.aDefaultParticipant;
import static com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.detailedmatch.StatsBuilder.aDefaultStatsBuilder;
import static com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.detailedmatch.TimeLineBuilder.aDefaultTimeLine;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SummonerController.class)
public class UpdateSummonerTest extends TestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void onlyUpdateMatchesWhereTheSummonerPlayedAdc() throws Exception {
        givenTheDatabaseContains(aDefaultSummoner()
                .withSummonerName(SUMMONER_NAME)
                .withAccountId(ENCRYPTED_ACCOUNT_ID)
                .build());

        stubFor(get(urlEqualTo(format("/lol/match/v4/matchlists/by-account/%s?queue=420&endIndex=100&beginIndex=0&api_key=%s", ENCRYPTED_ACCOUNT_ID, API_KEY)))
                .withHeader("X-Riot-Token", matching(API_KEY)).willReturn(
                        aResponse().withStatus(200)
                                .withBody(gson.toJson(aDefaultMatchListResponse()
                                        .withMatches(asList(
                                                aDefaultMatch()
                                                        .withGameId("1")
                                                        .withRole("SOLO")
                                                        .withChampion("20")
                                                        .withLane("TOP")
                                                        .build(),
                                                aDefaultMatch()
                                                        .withGameId("2")
                                                        .withRole("DUO_CARRY")
                                                        .withChampion("50")
                                                        .withLane("BOTTOM")
                                                        .build()))
                                        .build()
                                ))
                ));

        stubFor(get(urlEqualTo(format("/lol/match/v4/matches/%s?api_key=%s", "2", API_KEY)))
                .withHeader("X-Riot-Token", matching(API_KEY)).willReturn(
                        aResponse().withStatus(200)
                                .withBody(gson.toJson(aDefaultDetailedMatch()
                                        .withGameId("2")
                                        .withParticipantList(
                                                asList(aDefaultParticipant()
                                                                .withChampionId("50")
                                                                .withTeamId("200")
                                                                .withTimeLine(aDefaultTimeLine()
                                                                        .withRole("DUO_CARRY")
                                                                        .withLane("BOTTOM")
                                                                        .build())
                                                                .withStats(aDefaultStatsBuilder()
                                                                        .withWin("true")
                                                                        .build())
                                                                .build(),
                                                        aDefaultParticipant()
                                                                .withChampionId("40")
                                                                .withTeamId("200")
                                                                .withTimeLine(aDefaultTimeLine()
                                                                        .withRole("DUO_SUPPORT")
                                                                        .withLane("NONE")
                                                                        .build())
                                                                .withStats(aDefaultStatsBuilder()
                                                                        .withWin("true")
                                                                        .build())
                                                                .build(),
                                                        aDefaultParticipant()
                                                                .withChampionId("30")
                                                                .withTeamId("100")
                                                                .withTimeLine(aDefaultTimeLine()
                                                                        .withRole("DUO_SUPPORT")
                                                                        .withLane("NONE")
                                                                        .build())
                                                                .withStats(aDefaultStatsBuilder()
                                                                        .withWin("false")
                                                                        .build())
                                                                .build()))
                                        .build()))));

        stubFor(WireMock.get(urlEqualTo(format("/lol/summoner/v4/summoners/by-name/%s", SUMMONER_NAME)))
                .withHeader("X-Riot-Token", WireMock.matching(API_KEY)).willReturn(
                        WireMock.aResponse().withStatus(200)
                                .withBody(gson.toJson(aDefaultGetSummonerByNameResponse()
                                        .withName(SUMMONER_NAME)
                                ))
                ));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post(UPDATE)
                .content(gson.toJson(new RequestWithSummonerName(SUMMONER_NAME)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        List<SummonerResponse> results = getResponseList(result, 1);

        SummonerResponse summonerResponse = results.get(0);
        assertNotNull(summonerResponse.getChampions());
        assertThat(summonerResponse.getChampions().size(), is(1));
        assertThat(summonerResponse.getChampions().get("Swain").getSupports().get("Janna").getWins(), is("1"));
        assertThat(summonerResponse.getChampions().get("Swain").getSupports().get("Janna").getLosses(), is("0"));
        assertThat(summonerResponse.getSupports().get("Janna").getWins(), is("1"));
        assertThat(summonerResponse.getSupports().get("Janna").getLosses(), is("0"));
    }

    @Test
    void dontMakeARequestIfMatchIsAlreadyInTheDatabase() throws Exception {
        givenTheDatabaseContains(aDefaultSummoner()
                .withSummonerName(SUMMONER_NAME)
                .withAccountId(ENCRYPTED_ACCOUNT_ID)
                .withMostRecentMatchId("500")
                .build());

        doReturn(aDefaultMatchListResponse()
                .withMatches(asList(
                        aDefaultMatch()
                                .withGameId("200")
                                .withChampion("50")
                                .build(),
                        aDefaultMatch()
                                .withGameId("500")
                                .withChampion("50")
                                .build()))
                .build())
                .when(riotApiClient).getMatchListFor(ENCRYPTED_ACCOUNT_ID, 0, 100);

        doReturn(
                aDefaultDetailedMatch()
                        .withGameId("200")
                        .withParticipantList(
                                asList(aDefaultParticipant()
                                                .withChampionId("50")
                                                .withTeamId("200")
                                                .withTimeLine(aDefaultTimeLine()
                                                        .withRole("DUO_CARRY")
                                                        .withLane("BOTTOM")
                                                        .build())
                                                .withStats(aDefaultStatsBuilder()
                                                        .withWin("true")
                                                        .build())
                                                .build(),
                                        aDefaultParticipant()
                                                .withChampionId("40")
                                                .withTeamId("200")
                                                .withTimeLine(aDefaultTimeLine()
                                                        .withRole("DUO_SUPPORT")
                                                        .withLane("NONE")
                                                        .build())
                                                .withStats(aDefaultStatsBuilder()
                                                        .withWin("true")
                                                        .build())
                                                .build()))
                        .build())
                .when(riotApiClient).getIndividualMatch(any());

        stubFor(WireMock.get(urlEqualTo(format("/lol/summoner/v4/summoners/by-name/%s", SUMMONER_NAME)))
                .withHeader("X-Riot-Token", WireMock.matching(API_KEY)).willReturn(
                        WireMock.aResponse().withStatus(200)
                                .withBody(gson.toJson(aDefaultGetSummonerByNameResponse()
                                        .withName(SUMMONER_NAME)
                                ))
                ));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post(UPDATE)
                .content(gson.toJson(new RequestWithSummonerName(SUMMONER_NAME)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        List<SummonerResponse> results = getResponseList(result, 1);

        verify(riotApiClient, times(1)).getIndividualMatch("200");
        verify(riotApiClient, never()).getIndividualMatch("500");
        Thread.sleep(100);

        waitForDbToUpdate();
        SummonerResponse summonerResponse = results.get(0);
        assertThat(dao.getUserBySummonerName(SUMMONER_NAME).getMostRecentMatchId(), is("200"));
        assertNotNull(summonerResponse.getChampions());
        assertThat(summonerResponse.getChampions().size(), is(1));
    }

    @Test()
    void increaseIndexBy100WhenMaxEachIndexForCurrentSearchIsReached() throws Exception {
        givenTheDatabaseContains(aDefaultSummoner()
                .withSummonerName(SUMMONER_NAME)
                .withAccountId(ENCRYPTED_ACCOUNT_ID)
                .withProfileIcon("50")
                .withSummonerLevel("200")
                .build());

        List<Match> matchList = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            matchList.add(
                    aDefaultMatch()
                            .withGameId(String.valueOf(i))
                            .withRole("DUO_CARRY")
                            .withChampion("50")
                            .withLane("BOTTOM")
                            .build()
            );
        }

        stubFor(WireMock.get(urlEqualTo(format("/lol/summoner/v4/summoners/by-name/%s", SUMMONER_NAME)))
                .withHeader("X-Riot-Token", WireMock.matching(API_KEY)).willReturn(
                        WireMock.aResponse().withStatus(200)
                                .withBody(gson.toJson(aDefaultGetSummonerByNameResponse()
                                        .withName(SUMMONER_NAME)
                                        .withSummonerLevel(200)
                                        .withProfileIconId(50)
                                ))
                ));

        doReturn(aDefaultMatchListResponse()
                .withMatches(matchList)
                .build())
                .when(riotApiClient)
                .getMatchListFor(ENCRYPTED_ACCOUNT_ID, 0, 100);

        doReturn(aDefaultMatchListResponse()
                .withMatches(singletonList(
                        aDefaultMatch()
                                .withGameId("101")
                                .withRole("DUO_CARRY")
                                .withChampion("50")
                                .withLane("BOTTOM")
                                .build()))
                .withStartIndex(100)
                .withEndIndex(200)
                .build())
                .when(riotApiClient)
                .getMatchListFor(ENCRYPTED_ACCOUNT_ID, 100, 200);

        doReturn(
                aDefaultDetailedMatch()
                        .withGameId("1")
                        .withParticipantList(
                                asList(aDefaultParticipant()
                                                .withChampionId("50")
                                                .withTeamId("200")
                                                .withTimeLine(aDefaultTimeLine()
                                                        .withRole("DUO_CARRY")
                                                        .withLane("BOTTOM")
                                                        .build())
                                                .withStats(aDefaultStatsBuilder()
                                                        .withWin("true")
                                                        .build())
                                                .build(),
                                        aDefaultParticipant()
                                                .withChampionId("40")
                                                .withTeamId("200")
                                                .withTimeLine(aDefaultTimeLine()
                                                        .withRole("DUO_SUPPORT")
                                                        .withLane("NONE")
                                                        .build())
                                                .withStats(aDefaultStatsBuilder()
                                                        .withWin("true")
                                                        .build())
                                                .build()))
                        .build())
                .when(riotApiClient).getIndividualMatch(any());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post(UPDATE)
                .content(gson.toJson(new RequestWithSummonerName(SUMMONER_NAME)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        List<SummonerResponse> results = getResponseList(result, 101);

        Mockito.verify(riotApiClient, times(101)).getIndividualMatch(any());
        Mockito.verify(riotApiClient, times(2)).getMatchListFor(anyString(), anyInt(), anyInt());
        Mockito.verify(riotApiClient, times(1)).getSummonerBySummonerName(any());

        SummonerResponse summonerResponse = results.get(100);
        assertNotNull(summonerResponse.getChampions());
        assertThat(summonerResponse.getChampions().size(), is(1));
        assertThat(summonerResponse.getChampions().get("Swain").getSupports().get("Janna").getWins(), is("101"));
        assertThat(summonerResponse.getChampions().get("Swain").getSupports().get("Janna").getLosses(), is("0"));
        assertThat(summonerResponse.getSupports().get("Janna").getWins(), is("101"));
        assertThat(summonerResponse.getSupports().get("Janna").getLosses(), is("0"));
        assertThat(summonerResponse.getSummonerLevel(), is("200"));
        assertThat(summonerResponse.getProfileIcon(), is("50"));

        waitForDbToUpdate();
        assertThat(dao.getUserBySummonerName(SUMMONER_NAME).getChampions().size(), is(1));
        assertThat(dao.getUserBySummonerName(SUMMONER_NAME).getChampions().get("Swain").getSupports().get("Janna").getWins(), is("101"));
        assertThat(dao.getUserBySummonerName(SUMMONER_NAME).getChampions().get("Swain").getSupports().get("Janna").getLosses(), is("0"));
        assertThat(dao.getUserBySummonerName(SUMMONER_NAME).getSupports().get("Janna").getWins(), is("101"));
        assertThat(dao.getUserBySummonerName(SUMMONER_NAME).getSupports().get("Janna").getLosses(), is("0"));
    }
}
