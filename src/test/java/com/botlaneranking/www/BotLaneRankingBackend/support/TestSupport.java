package com.botlaneranking.www.BotLaneRankingBackend.support;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.services.dynamodbv2.model.*;
import com.botlaneranking.www.BotLaneRankingBackend.api.RiotApiClient;
import com.botlaneranking.www.BotLaneRankingBackend.config.pojo.ChampionInfo;
import com.botlaneranking.www.BotLaneRankingBackend.controllers.responses.SummonerResponse;
import com.botlaneranking.www.BotLaneRankingBackend.database.DynamoDbDao;
import com.botlaneranking.www.BotLaneRankingBackend.database.Summoner;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@ActiveProfiles("test")
public class TestSupport {
    @SpyBean
    protected DynamoDbDao dao;

    @SpyBean
    protected RiotApiClient riotApiClient;

    @SpyBean
    protected ChampionInfo championInfo;

    protected static String SUMMONER_NAME;
    protected static final String BOT_LANE_STATISTICS = "/getBotLaneStatistics";
    protected static String GAME_ID;
    protected static final String API_KEY = "riotapikey";
    protected static final String ENCRYPTED_ACCOUNT_ID = "123";
    protected static final String UPDATE = "/update";


    protected Gson gson = new Gson();
    private static DynamoDBProxyServer server;
    protected static WireMockServer wireMockServer = new WireMockServer();
    protected static DynamoDB dbClient;
    private static Table table;

    @BeforeEach
    public void beforeEach(){
        wireMockServer.start();
        SUMMONER_NAME = UUID.randomUUID().toString();
        GAME_ID = UUID.randomUUID().toString();
//        Mockito.when(rateLimiterComponent.getRateLimiter()).thenReturn(RateLimiter.create(1000));
    }

    @AfterEach
    public void afterAll(){
        wireMockServer.stop();
    }

    @BeforeAll
    public static void setUp() throws Exception {
        System.setProperty("sqlite4java.library.path", "native-libs");
        String port = "8000";
        server = ServerRunner.createServerFromCommandLineArgs(
                new String[]{"-inMemory", "-port", port});
        server.start();
        createClient();
        createTable();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        server.stop();
    }

    private static void createClient(){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "eu-west-2"))
                .build();
        dbClient = new DynamoDB(client);
    }

    private static void createTable(){
        String tableName = "Summoners_EUW";

        try {
            System.out.println("Attempting to create table; please wait...");
            table = dbClient.createTable(tableName,
                    singletonList(new KeySchemaElement("name", KeyType.HASH)),
                    singletonList(new AttributeDefinition("name", ScalarAttributeType.S)),
                    new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());
        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
    }

    protected List<SummonerResponse> getResponseList(MvcResult result, int expectedSize) throws UnsupportedEncodingException {
        List<SummonerResponse> resultList = emptyList();
        while (resultList.size() < expectedSize) {
            List<SummonerResponse> tempList = new ArrayList<>();
            List<String> split = List.of(result.getResponse().getContentAsString().replaceAll("data:", "").split("\n\n"));
            for (String s : split) {
                SummonerResponse summonerResponse = null;
                try {
                    summonerResponse = gson.fromJson(s, SummonerResponse.class);
                } catch (Exception ignored) {
                }
                if (summonerResponse != null) {
                    tempList.add(summonerResponse);
                }
            }
            resultList = tempList;
        }
        return resultList;
    }

    protected void waitForDbToUpdate() throws InterruptedException {
        Thread.sleep(500);
    }

    public void givenTheDatabaseContains(Summoner summoner){
        dao.createNewSummoner(summoner);
    }
}
