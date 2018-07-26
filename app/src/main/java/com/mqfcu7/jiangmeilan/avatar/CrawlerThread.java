package com.mqfcu7.jiangmeilan.avatar;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CrawlerThread extends Thread {
    private static CrawlerThread sCrawlerThread = new CrawlerThread();

    private static final String DOMAIN_TOUXIANG = "https://www.woyaogexing.com";

    private Database mDatabase;
    private int mTouXiangPageNum = 1;

    private CrawlerThread() { }

    public static CrawlerThread getInstance() {
        return sCrawlerThread;
    }

    public void setDatabase(Database db) {
        mDatabase = db;
    }

    @Override
    public void run() {
        while (true) {
            crawlAvatarSuites();

            try {
                sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void crawlAvatarSuites() {
        Log.d("TAG", "num: " + mDatabase.getNewAvatarSuiteNum());
        if (mDatabase.getNewAvatarSuiteNum() > 100) {
            return;
        }

        List<AvatarSuite> collect = new ArrayList<>();
        Queue<AvatarSuite> queue = new LinkedList<>();

        try {
            Document rootDoc = Jsoup.connect(DOMAIN_TOUXIANG + getTouXiangePageURI()).get();
            Elements rootElements = rootDoc.select("div.pMain").select("div.txList");
            for (int i = 0; i < rootElements.size(); ++ i) {
                String url = DOMAIN_TOUXIANG + rootElements.get(i).select("a").attr("href").toString();
                Document doc = Jsoup.connect(url).get();
                Elements elements = doc.select("div.contLeftA");
                AvatarSuite as = new AvatarSuite();
                as.title = elements.select("h1").text();
                as.images_url = new ArrayList<>();
                elements = elements.select("li.tx-img");
                for (int j = 0; j < elements.size(); ++ j) {
                    as.images_url.add("http:" + elements.get(j).select("img").attr("src").toString());
                }
                queue.add(as);
            }
            mTouXiangPageNum += 1;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // split images
        while (!queue.isEmpty()) {
            AvatarSuite a = queue.poll();
            if (a.images_url.size() < 4) continue;
            if (a.images_url.size() <= 10) {
                a.calcHash();
                collect.add(a);
                continue;
            }
            AvatarSuite b = new AvatarSuite();
            b.title = a.title;
            b.images_url = new ArrayList<>(a.images_url.size() / 2);
            b.images_url.addAll(a.images_url.subList(0, a.images_url.size() / 2));
            queue.offer(b);

            AvatarSuite c = new AvatarSuite();
            c.title = a.title;
            c.images_url = new ArrayList<>(a.images_url.size() - b.images_url.size());
            c.images_url.addAll(a.images_url.subList(b.images_url.size(), a.images_url.size()));
            queue.offer(c);
        }

        Collections.shuffle(collect);
        for (AvatarSuite a : collect) {
            mDatabase.addAvatarSuite(a);
        }
    }

    private String getTouXiangePageURI() {
        if (mTouXiangPageNum == 1) {
            return "/touxiang/";
        }

        return "/touxiang/index_" + mTouXiangPageNum + ".html";
    }
}
