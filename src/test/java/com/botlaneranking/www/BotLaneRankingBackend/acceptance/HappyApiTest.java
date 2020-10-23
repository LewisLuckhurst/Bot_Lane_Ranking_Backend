package com.botlaneranking.www.BotLaneRankingBackend.acceptance;

import com.botlaneranking.www.BotLaneRankingBackend.api.RiotApiClient;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.responses.BotLaneStatisticsResponse;
import com.botlaneranking.www.BotLaneRankingBackend.database.DynamoDbDao;
import com.botlaneranking.www.BotLaneRankingBackend.support.TestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.botlaneranking.www.BotLaneRankingBackend.support.SummonerBuilder.aDefaultSummoner;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class HappyApiTest extends TestSupport {

    @MockBean
    public DynamoDbDao dao;

    @MockBean
    public RiotApiClient riotApiClient;

    @Test
    void doNotMakeRequestToRiotWhenUsernameIsInDatabase() throws Exception {
        when(dao.containSummonerName(SUMMONER_NAME))
                .thenReturn(true);
        when(dao.getUserBySummonerName(SUMMONER_NAME))
                .thenReturn(aDefaultSummoner()
                        .withSummonerName(SUMMONER_NAME)
                        .build());

        BotLaneStatisticsResponse response = botLaneStatisticsRequestForSummoner(SUMMONER_NAME);
        verify(dao, times(1)).getUserBySummonerName(SUMMONER_NAME);
        verify(dao, times(1)).containSummonerName(SUMMONER_NAME);
        verify(riotApiClient, never()).getSummonerBySummonerName(SUMMONER_NAME);

        assertThat(response.getSummonerName(), is(SUMMONER_NAME));
    }
}
