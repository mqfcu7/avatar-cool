package com.mqfcu7.jiangmeilan.avatar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class CrawlerThread extends Thread {
    private static CrawlerThread sCrawlerThread = new CrawlerThread();

    private static final String DOMAIN_TOUXIANG = "https://www.woyaogexing.com";
    private static final int MAX_VATAR_NUM = 1000;

    private Random mRandom = new Random();
    private Database mDatabase;
    private int mTouXiangPageNum = 1;

    private boolean mIsStopGirlCrawler = false;
    private int mGirlPageNum = 1;

    private CrawlerThread() {
    }

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
            crawlGirlAvatars();

            try {
                sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void crawlAvatarSuites() {
        if (mDatabase.getNewAvatarSuiteNum() > 100) {
            return;
        }

        List<AvatarSuite> collect = new ArrayList<>();
        Queue<AvatarSuite> queue = new LinkedList<>();

        final String pageUrl = DOMAIN_TOUXIANG + "/touxiang/" + getPageURI(mTouXiangPageNum);
        queue.addAll(getPageAvatarSuite(pageUrl));

        // split images
        while (!queue.isEmpty()) {
            AvatarSuite a = queue.poll();
            if (a.images_url.size() < 4) continue;
            if (a.images_url.size() < mRandom.nextInt(4) + 7) {
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

    private String getPageURI(int page) {
        if (page == 1) {
            return "";
        }

        return "index_" + page + ".html";
    }

    private String parseTitle(String title) {
        String[] t = title.split(" ");
        if (t.length > 1) {
            return t[t.length - 1];
        }
        return title;
    }

    private void crawlGirlAvatars() {
        if (mIsStopGirlCrawler) {
            return;
        }

        boolean isRepeat = false;
        final String pageUrl = DOMAIN_TOUXIANG + "/touxiang/nv/" + getPageURI(mGirlPageNum);
        List<AvatarSuite> suites = getPageAvatarSuite(pageUrl);
        List<Avatar> collect = avatarSuitesToAvatars(suites, Database.AvatarType.GIRL);
        Collections.shuffle(collect);
        for (Avatar a : collect) {
            isRepeat |= mDatabase.addAvatar(a);
        }
        mIsStopGirlCrawler = isRepeat && mDatabase.getAvatarNum(Database.AvatarType.GIRL) > MAX_VATAR_NUM;
    }

    private List<AvatarSuite> getPageAvatarSuite(String pageUrl) {
        List<AvatarSuite> suites = new ArrayList<>();
        try {
            Document rootDoc = Jsoup.connect(pageUrl).get();
            Elements rootElements = rootDoc.select("div.pMain").select("div.txList");
            for (int i = 0; i < rootElements.size(); ++ i) {
                String url = DOMAIN_TOUXIANG + rootElements.get(i).select("a").attr("href").toString();
                Document doc = Jsoup.connect(url).get();
                Elements elements = doc.select("div.contLeftA");
                AvatarSuite as = new AvatarSuite();
                as.title = parseTitle(elements.select("h1").text());
                as.images_url = new ArrayList<>();
                elements = elements.select("li.tx-img");
                for (int j = 0; j < elements.size(); ++ j) {
                    as.images_url.add("http:" + elements.get(j).select("img").attr("src").toString());
                }
                suites.add(as);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suites;
    }

    private List<Avatar> avatarSuitesToAvatars(final List<AvatarSuite> suites, int type) {
        List<Avatar> result = new LinkedList<>();
        for (AvatarSuite suite : suites) {
            for (int i = 0; i < suite.images_url.size(); ++ i) {
                Avatar a = new Avatar();
                a.type = type;
                a.url = suite.images_url.get(i);
                a.calcHash();
                result.add(a);
            }
        }
        return result;
    }
}
