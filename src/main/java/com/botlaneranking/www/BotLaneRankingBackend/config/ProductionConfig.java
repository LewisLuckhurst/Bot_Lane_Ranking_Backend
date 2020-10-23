package com.botlaneranking.www.BotLaneRankingBackend.config;

import com.botlaneranking.www.BotLaneRankingBackend.database.DynamoDbDao;
import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductionConfig {

    @Bean
    public DynamoDbDao dynamoDbDao(){
        return new DynamoDbDao();
    }

    @Bean
    public Gson gson(){
        return new Gson();
    }
}
