package com.botlaneranking.www.BotLaneRankingBackend.acceptance;

import com.botlaneranking.www.BotLaneRankingBackend.api.RiotApiClient;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.responses.BotLaneStatisticsResponse;
import com.botlaneranking.www.BotLaneRankingBackend.database.DynamoDbDao;
import com.botlaneranking.www.BotLaneRankingBackend.support.TestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static com.botlaneranking.www.BotLaneRankingBackend.support.SummonerBuilder.aDefaultSummoner;
import static com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.GetSummonerByNameResponse.aDefaultGetSummonerByNameResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

public class HappyApiTest extends TestSupport {

    @MockBean
    public DynamoDbDao dao;

    @SpyBean
    public RiotApiClient riotApiClient;

    @Test
    void doNotMakeRequestToRiotWhenUsernameIsInDatabase() throws Exception {
        when(dao.containsSummonerName(SUMMONER_NAME))
                .thenReturn(true);
        when(dao.getUserBySummonerName(SUMMONER_NAME))
                .thenReturn(aDefaultSummoner()
                        .withSummonerName(SUMMONER_NAME)
                        .withSummonerLevel(20)
                        .withProfileIcon(30)
                        .build());

        BotLaneStatisticsResponse response = botLaneStatisticsRequestForSummoner(SUMMONER_NAME);
        verify(dao, times(1)).getUserBySummonerName(SUMMONER_NAME);
        verify(dao, times(1)).containsSummonerName(SUMMONER_NAME);
        verify(riotApiClient, never()).getSummonerBySummonerName(SUMMONER_NAME);

        assertThat(response.getSummonerName(), is(SUMMONER_NAME));
        assertThat(response.getSummonerLevel(), is(20));
        assertThat(response.getProfileIcon(), is(30));
    }

    @Test
    void makeRequestToRiotWhenUsernameIsNotInDatabase() {
        when(dao.containsSummonerName(SUMMONER_NAME))
                .thenReturn(false);

        stubFor(get(urlEqualTo(format("/lol/summoner/v4/summoners/by-name/%s", SUMMONER_NAME)))
                .withHeader("X-Riot-Token", matching(API_KEY)).willReturn(
                aResponse().withStatus(200)
                        .withBody(gson.toJson(aDefaultGetSummonerByNameResponse()
                                .withId("500")
                                .withAccountId("123")
                                .withName(SUMMONER_NAME)
                                .withSummonerLevel(45)
                                .withPuuid("600")
                                .withProfileIconId(749)
                                .withRevisionDate("1602798176000")
                        ))
        ));

        BotLaneStatisticsResponse response = botLaneStatisticsRequestForSummoner(SUMMONER_NAME);
        verify(dao, times(1)).containsSummonerName(SUMMONER_NAME);
        verify(dao, never()).getUserBySummonerName(SUMMONER_NAME);
        verify(dao, times(1)).createNewSummoner(SUMMONER_NAME, "500", "123",
                45, "600", 749, "1602798176000");
        verify(riotApiClient, times(1)).getSummonerBySummonerName(SUMMONER_NAME);

        assertThat(response.getSummonerName(), is(SUMMONER_NAME));
        assertThat(response.getSummonerLevel(), is(45));
        assertThat(response.getProfileIcon(), is(749));
    }
}
