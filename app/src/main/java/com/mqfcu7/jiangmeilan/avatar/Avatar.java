package com.mqfcu7.jiangmeilan.avatar;

import org.jsoup.helper.StringUtil;

public class Avatar extends Object{
    public int id;
    public int hash;
    public int type;
    public String url;

    @Override
    public String toString() {
        return url;
    }

    public void calcHash() {
        hash = url.hashCode();
    }
}
