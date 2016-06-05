package com.example.administrator.okhttp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 获取网址httpUrl,得到新闻列表的JSON信息
 * <p/>
 * Created by Administrator on 2016/6/3 0003.
 */
public class GetNews {
    private static final String TAG                  = "GetNews";
    public static final  String APPURL               = "http://118.244.212.82:9092/newsClient";
    public static final  int    ver                  = 1;
    private static final int    subId                = 1;
    private static       int    nid                  = 1;
    public static final  int    MODE_NEXT            = 1;
    public static final  int    MODE_PREVIOUS        = 2;
    private static final int    DEFAULT_CONN_TIMEOUT = 3000;
    private static final int    DEFAULT_READ_TIMEOUT = 3000;

    //      获取网址
    public static String getRequest() {
//        调用获取日期的方法,得到日期stamp
        String stamp = GetNews.getDate();
        String url = APPURL + "/news_list?ver=" + ver
                + "&subid=" + subId + "&dir=" + MODE_NEXT + "&nid=" + nid
                + "&stamp=" + stamp + "&cnt=" + 20;
        Log.d(TAG, "URL**************" + url);
        return url;
    }

    //    获取当前日期
    public static String getDate() {
        Date date = new Date(System.currentTimeMillis());
        String strs = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            strs = dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strs;
    }

    //      得到新闻列表的JSON信息
    public static String getNewsList() {
//        调用获取网址的方法,得到网址httpUrl
        String httpUrl = GetNews.getRequest();
        Log.d(TAG, "httpUrl*********" + httpUrl);
        BufferedReader reader = null;
        String newList = null;
        StringBuffer sb = new StringBuffer();

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
            conn.setConnectTimeout(DEFAULT_CONN_TIMEOUT);
            conn.connect();

            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            reader.close();
            newList = sb.toString();
            Log.d(TAG, "getNewsList: *********" + newList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newList;

    }

}
