package com.botlaneranking.www.BotLaneRankingBackend.support;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.services.dynamodbv2.model.*;
import com.botlaneranking.www.BotLaneRankingBackend.database.Summoner;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

import static java.util.Collections.singletonList;

public class TestSupport {

    protected Gson gson = new Gson();
    protected static String SUMMONER_NAME = "lucky";
    protected static final String API_KEY = "riotapikey";
    private static DynamoDBProxyServer server;
    protected static WireMockServer wireMockServer = new WireMockServer();
    protected static DynamoDB dbClient;
    private static Table table;

    @BeforeEach
    public void beforeEach(){
        SUMMONER_NAME = UUID.randomUUID().toString();
    }

    @BeforeAll
    public static void setUp() throws Exception {
        wireMockServer.start();
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
        wireMockServer.stop();
        server.stop();
    }

    private static void createClient(){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "eu-west-2"))
                .build();
        dbClient = new DynamoDB(client);
    }

    private static void createTable(){
        String tableName = "Summoners";

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

    public void givenTheDatabaseContains(Summoner summoner){
        table.putItem(
                new Item().withPrimaryKey("name", summoner.getName())
                .withJSON("summonerLevel", summoner.getSummonerLevel())
                .withJSON("profileIconId", summoner.getProfileIconId())
        );
    }
}
