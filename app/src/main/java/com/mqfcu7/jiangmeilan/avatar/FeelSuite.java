package com.mqfcu7.jiangmeilan.avatar;

import android.support.annotation.NonNull;

public class FeelSuite extends Object {
    public int id;
    public String title;
    public int imageWidth;
    public int imageHeight;
    public String imageUrl;
    public String userName;
    public String userUrl;
    public Long time;
    public String timeStr;

    @Override
    public String toString() {
        return "id: " + id
                + ", title: " + title
                + ", imageWidht: " + imageWidth
                + ", imageHeight: " + imageHeight
                + ", imageUrl: " + imageUrl
                + ", userName: " + userName
                + ", userUrl: " + userUrl
                + ", time: " + time
                + ", timeStr: " + timeStr;
    }
}
