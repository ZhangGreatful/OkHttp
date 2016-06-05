package com.example.administrator.okhttp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * 判断滚动的状态,滚动过程中,停止加载任务.停止滚动后,再加载
 * Created by Administrator on 2016/6/3 0003.
 */
public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener {
    private       List<Entity>   mEntities;
    private       LayoutInflater inflater;//布局
    private       ImageLoader    mImageLoader;
    //    保存当前所有的URL的地址
    public static String[]       URLS;
//    可见项的起始项和结束项
    private       int            mStart, mEnd;
//    判断是否第一次启动
    private boolean mFirstIn;

    //第一个参数为上下文,第二个为数据data
    public NewsAdapter(Context context, List<Entity> data, ListView listView) {
//        在这里初始化mImageLoader,保留只有一个LruCache
        mImageLoader = new ImageLoader(listView);
        mEntities = data;
        inflater = LayoutInflater.from(context);
//        给URLS赋值
        URLS = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            URLS[i] = data.get(i).icon;
        }
        mFirstIn=true;
//        别忘了注册对应的事件
        listView.setOnScrollListener(this);

    }

    @Override
    public int getCount() {
        return mEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return mEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
//            设置布局
            convertView = inflater.inflate(R.layout.item_list_news, null);
//            对元素进行初始化
            viewHolder.ivicon = (ImageView) convertView.findViewById(R.id.imageView1);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.textView1);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.textView2);
//            添加标签
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        设置默认图标ic_launcher
        viewHolder.ivicon.setImageResource(R.drawable.defaultpic);
        viewHolder.tvTitle.setText(mEntities.get(position).title);
        viewHolder.tvContent.setText(mEntities.get(position).summary);
        String url = mEntities.get(position).icon;
//        对imageView设置一个Tag,让imageView和url进行绑定
        viewHolder.ivicon.setTag(url);
//        mImageLoader.showImageByAsncTask(viewHolder.ivicon, url);
//        使用线程加载图片
//        new ImageLoader().showImageByThread(viewHolder.ivicon,mEntities.get(position).icon);
//        使用异步加载的方式加载图片
//        new ImageLoader().showImageByAsncTask(viewHolder.ivicon, mEntities.get(position).icon);
        mImageLoader.showImageByAsncTask(viewHolder.ivicon, mEntities.get(position).icon);
        return convertView;


    }

    /**
     * 当滑动状态改变时才会调用
     *
     * @param view
     * @param scrollState
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        注意在初始化时不会加载.
        if (scrollState == SCROLL_STATE_IDLE) {
//            加载可见项
            mImageLoader.loadImages(mStart,mEnd);
        } else {
//            停止任务
            mImageLoader.cancelAllTask();
        }
    }

    /**
     * 在整个滑动过程中都会调用
     *
     * @param view
     * @param firstVisibleItem 第一个可见item
     * @param visibleItemCount 可见item的长度
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mStart = firstVisibleItem+1;
        mEnd = firstVisibleItem + visibleItemCount+1;
//        当前列表第一次显示,并且可见列表数大于0,执行该方法
        if (mFirstIn==true&&visibleItemCount>0){
            mImageLoader.loadImages(mStart,mEnd);
            mFirstIn=false;
        }
    }

    class ViewHolder {
        //    定义三个元素,title,context,icon
        public ImageView ivicon;
        public TextView  tvTitle, tvContent;
    }
}
