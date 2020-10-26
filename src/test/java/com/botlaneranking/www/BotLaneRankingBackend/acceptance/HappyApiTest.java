package com.botlaneranking.www.BotLaneRankingBackend.acceptance;

import com.botlaneranking.www.BotLaneRankingBackend.api.RiotApiClient;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.EntryPoint;
import com.botlaneranking.www.BotLaneRankingBackend.database.DynamoDbDao;
import com.botlaneranking.www.BotLaneRankingBackend.database.Summoner;
import com.botlaneranking.www.BotLaneRankingBackend.support.BotLaneStatisticsRequest;
import com.botlaneranking.www.BotLaneRankingBackend.support.TestSupport;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.botlaneranking.www.BotLaneRankingBackend.support.SummonerBuilder.aDefaultSummoner;
import static com.botlaneranking.www.BotLaneRankingBackend.support.riot.summerV4.GetSummonerByNameResponse.aDefaultGetSummonerByNameResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EntryPoint.class)
public class HappyApiTest extends TestSupport {

    private static final String BOT_LANE_STATISTICS = "/getBotLaneStatistics";

    @MockBean
    private DynamoDbDao dao;

    @SpyBean
    private RiotApiClient riotApiClient;

    @Autowired
    private MockMvc mockMvc;

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

        mockMvc.perform(MockMvcRequestBuilders
                .post(BOT_LANE_STATISTICS)
                .content(gson.toJson(new BotLaneStatisticsRequest(SUMMONER_NAME)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("summonerName").value(SUMMONER_NAME))
        .andExpect(jsonPath("summonerLevel").value(20))
        .andExpect(jsonPath("profileIcon").value(30));

        verify(dao, times(1)).getUserBySummonerName(SUMMONER_NAME);
        verify(dao, times(1)).containsSummonerName(SUMMONER_NAME);
        verify(riotApiClient, never()).getSummonerBySummonerName(SUMMONER_NAME);
    }

    @Test
    void makeRequestToRiotWhenUsernameIsNotInDatabase() throws Exception {
        ArgumentCaptor<Summoner> argument = ArgumentCaptor.forClass(Summoner.class);

        when(dao.containsSummonerName(SUMMONER_NAME))
                .thenReturn(false);

        WireMock.stubFor(WireMock.get(urlEqualTo(format("/lol/summoner/v4/summoners/by-name/%s", SUMMONER_NAME)))
                .withHeader("X-Riot-Token", WireMock.matching(API_KEY)).willReturn(
                        WireMock.aResponse().withStatus(200)
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

        mockMvc.perform(MockMvcRequestBuilders
                .post(BOT_LANE_STATISTICS)
                .content(gson.toJson(new BotLaneStatisticsRequest(SUMMONER_NAME)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("summonerName").value(SUMMONER_NAME))
                .andExpect(jsonPath("summonerLevel").value(45))
                .andExpect(jsonPath("profileIcon").value(749));


        verify(dao, times(1)).containsSummonerName(SUMMONER_NAME);
        verify(dao, never()).getUserBySummonerName(SUMMONER_NAME);
        verify(riotApiClient, times(1)).getSummonerBySummonerName(SUMMONER_NAME);

        verify(dao, times(1)).createNewSummoner(argument.capture());
        assertThat(argument.getValue().getSummonerName(), is(SUMMONER_NAME));
        assertThat(argument.getValue().getAccountId(), is("123"));
        assertThat(argument.getValue().getId(), is("500"));
        assertThat(argument.getValue().getPuuid(), is("600"));
        assertThat(argument.getValue().getSummonerLevel(), is(45));
        assertThat(argument.getValue().getProfileIconId(), is(749));
        assertThat(argument.getValue().getRevisionDate(), is("1602798176000"));
    }
}
