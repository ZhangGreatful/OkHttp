package com.example.administrator.okhttp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * 添加header,实现上拉刷新
 * Created by Administrator on 2016/6/4 0004.
 */
public class ReFlashListView extends ListView implements AbsListView.OnScrollListener {
    private static final String TAG = "ReFlashListView";
    View    header;//顶部布局文件
    int     headerHeight;//顶部布局文件的高度
    //    判断可见界面是否在最顶端
    int     firstVisibleItem;
    //    标记当前在listView最顶端按下
    boolean isRemark;
    //    按下时的Y值
    int     startY;
    int     state;//当前的状态
    int     scrollState;//listView当前滚动状态
    final int NONE       = 0;//正常状态
    final int PULL       = 1;//提示下拉状态
    final int RELESE     = 2;//释放状态
    final int REFLASHING = 3;//正在刷新状态
    IReflashListener iReflashListener;//刷新数据的接口

    public ReFlashListView(Context context) {
        super(context);
        initView(context);
    }

    public ReFlashListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ReFlashListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        初始化布局
        initView(context);
    }

    /**
     * 初始化布局,添加顶部布局文件到listView内
     */
    private void initView(Context context) {
//        获取顶部布局文件
        LayoutInflater inflater = LayoutInflater.from(context);
        header = inflater.inflate(R.layout.header_layout, null);
        measureView(header);
        headerHeight = header.getMeasuredHeight();
        Log.d(TAG, "headerHeiht=***********" + headerHeight);
        topPadding(-headerHeight);
        this.addHeaderView(header);
//
        this.setOnScrollListener(this);

    }

    /**
     * 通知父布局,占用的宽和高
     *
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
//            宽度为Match_Parent,高度为Wrap_Content
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
//        获取子布局的宽度,第一个变量是header左右的边距,第二个是内边距,第三个是子布局宽度
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    /**
     * 设置header布局的上边距
     *
     * @param topPadding
     */
    private void topPadding(int topPadding) {
        header.setPadding(header.getPaddingLeft(), topPadding,
                header.getPaddingRight(), header.getPaddingBottom());
        header.invalidate();
    }

    //取到当前的滚动状态
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
    }

    //    监听onTouch事件
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (firstVisibleItem == 0) {
                    isRemark = true;
                    startY = (int) ev.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                if (state == RELESE) {
                    state = REFLASHING;
                    reflashViewByState();
//                    加载最新数据
//                    调用刷新的onFlash方法
                    iReflashListener.onReflash();
                } else if (state == PULL) {
                    state = NONE;
                    isRemark = false;
                    reflashViewByState();
                }
                break;

        }
        return super.onTouchEvent(ev);
    }

    /**
     * 根据当前状态改变界面显示
     */
    private void reflashViewByState() {
        TextView tip = (TextView) header.findViewById(R.id.tip);
        ImageView arrow = (ImageView) header.findViewById(R.id.arrow);
        ProgressBar progress = (ProgressBar) header.findViewById(R.id.progress);
        RotateAnimation anim=new RotateAnimation(0,180,
                RotateAnimation.RELATIVE_TO_SELF,0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);
//        设置时间间隔
        anim.setDuration(500);
        anim.setFillAfter(true);
        RotateAnimation anim1=new RotateAnimation(180,0,
                RotateAnimation.RELATIVE_TO_SELF,0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);
        anim.setDuration(500);
        anim.setFillAfter(true);
        switch (state) {
            case NONE:
//                取消动画
                arrow.clearAnimation();
                topPadding(-headerHeight);
                break;
            case PULL:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("下拉可以刷新");
                arrow.clearAnimation();
                arrow.setAnimation(anim1);
                break;
            case RELESE:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("松开可以刷新");
//                从0变成180
                arrow.clearAnimation();
                arrow.setAnimation(anim);
                break;
            case REFLASHING:
//                固定的高度
                topPadding(50);
                arrow.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                tip.setText("正在刷新");
                break;
        }
    }

    /**
     * 判断移动过程中的操作
     *
     * @param ev
     */
    private void onMove(MotionEvent ev) {
        if (isRemark) {
            return;
        }
//        当前的位置tempY,移动的距离space
        int tempY = (int) ev.getY();
        int space = tempY = startY;
        int topPadding = space - headerHeight;
        switch (state) {
            case NONE:
                if (space > 0) {
                    state = PULL;
                    reflashViewByState();
                }
                break;
            case PULL:
                if (space > headerHeight + 30 &&
                        scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    state = RELESE;
                    reflashViewByState();
                }
                break;
            case RELESE:
                topPadding(topPadding);
                if (space < headerHeight + 30) {
                    state = PULL;
                    reflashViewByState();
                } else if (space <= 0) {
                    state = NONE;
                    reflashViewByState();
                    isRemark = false;
                }
                break;
            case REFLASHING:
                break;
        }
    }

    /**
     * 获取完数据
     */
    public void reflashComplete(){
        state=NONE;
        isRemark=false;
        reflashViewByState();
        TextView lastupdateTime= (TextView) header.findViewById(R.id.lastupdate_time);
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日hh:mm:ss");
        Date date=new Date(System.currentTimeMillis());
        String time=format.format(date);
        lastupdateTime.setText(time);
    }
    public  void setInterface(IReflashListener iReflashListener){
        this.iReflashListener =iReflashListener;
    }
    /**
     * 刷新数据的接口
     */
    public interface IReflashListener{

        void onReflash();
    }
}
