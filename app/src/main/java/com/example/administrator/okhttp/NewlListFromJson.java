package com.example.administrator.okhttp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析新闻列表的JSON信息,
 * 将解析得到的JSON格式数据转化为我们所封装的Entity
 * Created by Administrator on 2016/6/3 0003.
 */
public class NewlListFromJson {
    private static final String TAG          = "NewlListFromJson";
    private              String NewsListJson = null;
    private Entity entity;
    public List<Entity> list = new ArrayList<>();

    public List<Entity> getJsonData() {
//        调用新闻列表的NewListJson信息
        NewsListJson = GetNews.getNewsList();
//        解析NewsListJson信息
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(NewsListJson);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject newsList = jsonArray.getJSONObject(i);
                entity = new Entity();
                entity.title = newsList.getString("title");
                Log.d(TAG, "entity.title*******" + entity.title);
                entity.summary = newsList.getString("summary");
                entity.icon = newsList.getString("icon");
                entity.stamp = newsList.getString("stamp");
                entity.link = newsList.getString("link");
                entity.type = newsList.getInt("type");
                entity.nid = newsList.getInt("nid");
                list.add(entity);
                Log.d(TAG, "listSize******" + list.size());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;

    }

}
