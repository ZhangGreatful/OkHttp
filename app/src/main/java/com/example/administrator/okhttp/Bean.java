package com.example.administrator.okhttp;

/**
 * Created by Administrator on 2016/6/3 0003.
 */
public class Bean {
    String title;
    String desc;
    String iconUrl;

    public Bean() {
    }

    public Bean(String title, String desc, String iconUrl) {
        this.title = title;
        this.desc = desc;
        this.iconUrl = iconUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
