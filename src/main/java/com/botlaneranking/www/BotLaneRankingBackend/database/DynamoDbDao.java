package com.botlaneranking.www.BotLaneRankingBackend.database;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class DynamoDbDao {
    private final Table table;
    private final Gson gson;

    @Autowired
    public DynamoDbDao(Gson gson,
                       @Value("${aws.accessKeyId}") String accessKey,
                       @Value("${aws.secretKey}") String secretKey,
                       @Value("${database.url}") String databaseUrl) {

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        this.gson = gson;
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(databaseUrl, Regions.EU_WEST_2.getName()))
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        this.table = dynamoDB.getTable("Summoners_EUW");
    }

    public Summoner getUserBySummonerName(String summonerName) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("name", summonerName);
        try {
            Item outcome = table.getItem(spec);
            return gson.fromJson(outcome.toJSON(), Summoner.class);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    public boolean containsSummonerName(String summonerName) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("name", summonerName.toLowerCase());
        Item outcome = table.getItem(spec);
        return outcome != null;
    }

    public void createNewSummoner(Summoner summoner) {
        table.putItem(
                new Item().withPrimaryKey("name", summoner.getName().toLowerCase())
                        .withString("id", summoner.getId())
                        .withString("accountId", summoner.getAccountId())
                        .withString("summonerLevel", summoner.getSummonerLevel())
                        .withString("puuid", summoner.getPuuid())
                        .withString("profileIconId", summoner.getProfileIconId())
                        .withString("revisionDate", summoner.getRevisionDate())
                        .withMap("champions", new HashMap<>())
                        .withMap("supports", new HashMap<>())
                        .withString("mostRecentMatchId", summoner.getMostRecentMatchId())
        );
    }

    public void updateChampions(Summoner summoner) {
        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("name", summoner.getName())
                .withUpdateExpression("set champions = :c, mostRecentMatchId = :m, supports = :s")
                .withValueMap(new ValueMap()
                        .withJSON(":c", gson.toJson(summoner.getChampions()))
                        .withString(":m", summoner.getMostRecentMatchId())
                        .withJSON(":s", gson.toJson(summoner.getSupports())))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        table.updateItem(updateItemSpec);
    }

    public void updateSummonerInfo(Summoner summoner) {
        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("name", summoner.getName())
                .withUpdateExpression("set profileIconId = :i, summonerLevel = :l")
                .withValueMap(new ValueMap()
                        .withString(":l", summoner.getSummonerLevel())
                        .withString(":i", summoner.getProfileIconId()))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        table.updateItem(updateItemSpec);
    }
}
