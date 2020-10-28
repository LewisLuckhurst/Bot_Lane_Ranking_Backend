package com.botlaneranking.www.BotLaneRankingBackend.config.pojo;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ChampionInfo {
    private final HashMap<String, Champion> data;

    @Autowired
    public ChampionInfo(Gson gson) {
        HashMap<String, Champion> data = new HashMap<>();

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet request = new HttpGet("http://ddragon.leagueoflegends.com/cdn/10.22.1/data/en_US/champion.json");

        try {
            JsonObject jsonObject = gson.fromJson(EntityUtils.toString(httpclient.execute(request).getEntity()), JsonObject.class);
            for(Map.Entry<String, JsonElement> entrySet: jsonObject.get("data").getAsJsonObject().entrySet()){
                JsonObject obj = entrySet.getValue().getAsJsonObject();
                Champion champion = new Champion(obj.get("key").getAsString(), obj.get("name").getAsString());
                data.put(champion.getKey(), champion);
            }

            httpclient.close();
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        this.data = data;
    }

    public HashMap<String, Champion> getChampions() {
        return data;
    }
}
