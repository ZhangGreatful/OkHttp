package com.example.administrator.okhttp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "MainActivity";
    private NewlListFromJson json;
    private NewsAdapter      adapter;
    private ListView         mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_newslist);
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(adapter);
        String url = GetNews.getRequest();
        json = new NewlListFromJson();
        new MyAsncTasy().execute(url);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String news = json.list.get(position).getLink();
        Log.d(TAG, "链接为******** " + news);
        Intent intent = new Intent(MainActivity.this, BrowseNews.class);
        intent.putExtra("link", news);
        startActivity(intent);
    }


    //    实现网络的异步访问
    private class MyAsncTasy extends AsyncTask<String, Void, List<Entity>> {
        @Override
        protected List<Entity> doInBackground(String... params) {
            return json.getJsonData();

        }

        //      设置UI布局
        @Override
        protected void onPostExecute(List<Entity> entities) {
            super.onPostExecute(entities);
            NewsAdapter adapter = new NewsAdapter(MainActivity.this, entities, mListView);
            mListView.setAdapter(adapter);
        }

    }
}
