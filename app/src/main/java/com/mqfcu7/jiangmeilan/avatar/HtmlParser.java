package com.mqfcu7.jiangmeilan.avatar;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class HtmlParser {

    private static final String DOMAIN = "https://www.woyaogexing.com";

    public HtmlParser() {
    }

    public void asynRandomAvatarSuite(final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = Utils.MSG_TYPE_DAILY_AVATAR;
                try {
                    AvatarSuite result = new AvatarSuite();

                    Document doc = Jsoup.connect(DOMAIN + "/touxiang/").get();
                    Elements elements = doc.select("div.pMain");
                    String url = elements.select("div.txList").select("a").attr("href").toString();
                    result.url = DOMAIN + url;

                    doc = Jsoup.connect(result.url).get();
                    elements = doc.select("div.contLeftA");
                    result.title = elements.select("h1").text();
                    result.images_url = new ArrayList<>();
                    elements = elements.select("li.tx-img");
                    for (int i = 0; i < elements.size(); ++ i) {
                        result.images_url.add("http:" + elements.get(i).select("img").attr("src").toString());
                    }
                    msg.obj = result;

                } catch (Exception e) {

                } finally {
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
}
