package com.botlaneranking.www.BotLaneRankingBackend.database;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DynamoDbDao {
    private final Table table;
    private final Gson gson;

    @Autowired
    public DynamoDbDao(@Value("${database.url}") String databaseUrl, Gson gson) {
        this.gson = gson;
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(databaseUrl, "eu-west-2"))
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);

        this.table = dynamoDB.getTable("Summoners");
    }

    public Summoner getUserBySummonerName(String summonerName) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("name", summonerName);

        try {
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);
            return gson.fromJson(outcome.toJSON(), Summoner.class);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    public boolean containsSummonerName(String summonerName) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("name", summonerName);
        Item outcome = table.getItem(spec);
        return outcome != null;
    }

    public void createNewSummoner(Summoner summoner) {
        table.putItem(
                new Item().withPrimaryKey("name", summoner.getName())
                        .withJSON("id", summoner.getId())
                        .withJSON("accountId", summoner.getAccountId())
                        .withJSON("summonerLevel", summoner.getSummonerLevel())
                        .withJSON("puuid", summoner.getPuuid())
                        .withJSON("profileIconId", summoner.getProfileIconId())
                        .withJSON("revisionDate", summoner.getRevisionDate())
        );
    }
}
