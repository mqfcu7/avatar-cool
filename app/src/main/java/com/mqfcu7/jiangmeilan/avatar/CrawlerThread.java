package com.mqfcu7.jiangmeilan.avatar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class CrawlerThread extends Thread {
    private static CrawlerThread sCrawlerThread = new CrawlerThread();

    private static final String DOMAIN_TOUXIANG = "https://www.woyaogexing.com";
    private static final int MAX_VATAR_NUM = 200;

    public static String sUA = "";
    private Random mRandom = new Random();
    private Database mDatabase;
    private int mTouXiangPageNum = 1;

    private boolean mIsStopGirlCrawler = false;
    private int mGirlPageNum = 1;
    private boolean mIsStopBoyCrawler = false;
    private int mBoyPageNum = 1;
    private boolean mIsStopLovesCrawler = false;
    private int mLovesPageNum = 1;
    private boolean mIsStopFriendCrawler = false;
    private int mFriendPageNum = 1;
    private boolean mIsStopPetCrawler = false;
    private int mPetPageNum = 1;
    private String mPetDomain = "http://api.51touxiang.com/api/face/cate_face_v2?cid=70044&page=";
    private int mPetDomainIdx = 1;
    private boolean mIsStopComicCrawler = false;
    private int mComicPageNum = 1;
    private boolean mIsStopGameCrawler = false;
    private int mGamePageNum = 1;
    private String mGameDomain = "http://api.51touxiang.com/api/face/cate_face_v2?cid=80029&page=";
    private int mGameDomainIdx = 1;
    private boolean mIsStopSceneryCrawler = false;
    private int mSceneryPageNum = 1;

    private CrawlerThread() {
    }

    public static CrawlerThread getInstance() {
        return sCrawlerThread;
    }

    public void setDatabase(Database db) {
        mDatabase = db;
    }
    public static void setUA(String ua) { sUA = ua; }

    @Override
    public void run() {
        while (true) {
            crawlAvatarSuites();
            crawlFeelSuite();

            crawlGirlAvatars();
            crawlBoyAvatars();
            crawlLovesAvatars();
            crawlFriendAvatars();
            crawlPetAvatars();
            crawlComicAvatars();
            crawlGameAvatars();
            crawlSceneryAvatars();

            try {
                sleep(2000);
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
        mTouXiangPageNum ++;

        // split images
        while (!queue.isEmpty()) {
            AvatarSuite a = queue.poll();
            if (a.images_url.size() < 4) continue;
            //if (a.images_url.size() < mRandom.nextInt(4) + 7) {
            if (a.images_url.size() < 10) {
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
        String[] t = title.split(" |：|:");
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
        mGirlPageNum ++;
    }

    private void crawlBoyAvatars() {
        if (mIsStopBoyCrawler) {
            return;
        }

        boolean isRepeat = false;
        final String pageUrl = DOMAIN_TOUXIANG + "/touxiang/nan/" + getPageURI(mBoyPageNum);
        List<AvatarSuite> suites = getPageAvatarSuite(pageUrl);
        List<Avatar> collect = avatarSuitesToAvatars(suites, Database.AvatarType.BOY);
        Collections.shuffle(collect);
        for (Avatar a : collect) {
            isRepeat |= mDatabase.addAvatar(a);
        }
        mIsStopBoyCrawler = isRepeat && mDatabase.getAvatarNum(Database.AvatarType.BOY) > MAX_VATAR_NUM;
        mBoyPageNum ++;
    }

    private void crawlLovesAvatars() {
        if (mIsStopLovesCrawler) {
            return;
        }

        boolean isRepeat = false;
        final String pageUrl = DOMAIN_TOUXIANG + "/touxiang/qinglv/" + getPageURI(mLovesPageNum);
        List<AvatarSuite> suites = getPageAvatarSuite(pageUrl);
        List<Avatar> collect = avatarSuitesToAvatars(suites, Database.AvatarType.LOVES);
        Collections.shuffle(collect);
        for (Avatar a : collect) {
            isRepeat |= mDatabase.addAvatar(a);
        }
        mIsStopLovesCrawler = isRepeat && mDatabase.getAvatarNum(Database.AvatarType.LOVES) > MAX_VATAR_NUM;
        mLovesPageNum ++;
    }

    private void crawlFriendAvatars() {
        if (mIsStopFriendCrawler) {
            return;
        }
        boolean isRepeat = false;
        final String pageUrl = "http://api.51touxiang.com/api/face/cate_face_v2?cid=20007&page=" + mFriendPageNum;
        JSONObject json = requestApi(pageUrl);
        List<String> urls = parseJson(json);
        if (urls == null || urls.isEmpty()) {
            mIsStopFriendCrawler = true;
            return;
        }
        for (String url : urls) {
            Avatar a = new Avatar();
            a.url = url;
            a.type = Database.AvatarType.FRIEND;
            a.calcHash();
            isRepeat |= mDatabase.addAvatar(a);
        }
        mIsStopFriendCrawler = isRepeat && mDatabase.getAvatarNum(Database.AvatarType.FRIEND) > MAX_VATAR_NUM;
        mFriendPageNum ++;
    }

    private void crawlPetAvatars() {
        if (mIsStopPetCrawler) {
            return;
        }
        boolean isRepeat = false;
        final String pageUrl = mPetDomain + mPetPageNum;
        JSONObject json = requestApi(pageUrl);
        List<String> urls = parseJson(json);
        if (urls == null || urls.isEmpty()) {
            if (mPetDomainIdx == 1) {
                mPetDomain = "http://api.51touxiang.com/api/face/cate_face_v2?cid=100001&page=";
                mPetDomainIdx ++;
                mPetPageNum = 2;
            } else if (mPetDomainIdx == 2) {
                mPetDomain = "http://api.51touxiang.com/api/face/cate_face_v2?cid=100002&page=";
                mPetDomainIdx ++;
                mPetPageNum = 1;
            } else {
                mIsStopPetCrawler = true;
            }
            return;
        }
        for (String url : urls) {
            Avatar a = new Avatar();
            a.url = url;
            a.type = Database.AvatarType.PET;
            a.calcHash();
            isRepeat |= mDatabase.addAvatar(a);
        }
        mIsStopPetCrawler = isRepeat && mDatabase.getAvatarNum(Database.AvatarType.PET) > MAX_VATAR_NUM;
        mPetPageNum ++;
    }

    private void crawlComicAvatars() {
        if (mIsStopComicCrawler) {
            return;
        }

        boolean isRepeat = false;
        final String pageUrl = DOMAIN_TOUXIANG + "/touxiang/katong/" + getPageURI(mComicPageNum);
        List<AvatarSuite> suites = getPageAvatarSuite(pageUrl);
        List<Avatar> collect = avatarSuitesToAvatars(suites, Database.AvatarType.COMIC);
        Collections.shuffle(collect);
        for (Avatar a : collect) {
            isRepeat |= mDatabase.addAvatar(a);
        }
        mIsStopComicCrawler = isRepeat && mDatabase.getAvatarNum(Database.AvatarType.COMIC) > MAX_VATAR_NUM;
        mComicPageNum ++;
    }

    private void crawlGameAvatars() {
        if (mIsStopGameCrawler) {
            return;
        }

        boolean isRepeat = false;
        final String pageUrl = mGameDomain + mGamePageNum;
        JSONObject json = requestApi(pageUrl);
        List<String> urls = parseJson(json);
        if (urls == null || urls.isEmpty()) {
            if (mGameDomainIdx == 1) {
                mGameDomain = "http://api.51touxiang.com/api/face/cate_face_v2?cid=80028&page=";
                mGameDomainIdx ++;
                mGamePageNum = 1;
            } else if (mGameDomainIdx == 2) {
                mGameDomain = "http://api.51touxiang.com/api/face/cate_face_v2?cid=80002&page=";
                mGameDomainIdx ++;
                mGamePageNum = 1;
            } else if (mGameDomainIdx == 3) {
                mGameDomain = "http://api.51touxiang.com/api/face/cate_face_v2?cid=80004&page=";
                mGameDomainIdx ++;
                mGamePageNum = 1;
            } else if (mGameDomainIdx == 4) {
                mGameDomain = "http://api.51touxiang.com/api/face/cate_face_v2?cid=80010&page=";
                mGameDomainIdx ++;
                mGamePageNum = 1;
            } else {
                mIsStopGameCrawler = true;
            }
            return;
        }
        for (String url : urls) {
            Avatar a = new Avatar();
            a.url = url;
            a.type = Database.AvatarType.GAME;
            a.calcHash();
            isRepeat |= mDatabase.addAvatar(a);
        }
        mIsStopGameCrawler = isRepeat && mDatabase.getAvatarNum(Database.AvatarType.GAME) > MAX_VATAR_NUM;
        mGamePageNum ++;
    }

    private void crawlSceneryAvatars() {
        if (mIsStopSceneryCrawler) {
            return;
        }

        boolean isRepeat = false;
        final String pageUrl = DOMAIN_TOUXIANG + "/touxiang/fengjing/" + getPageURI(mSceneryPageNum);
        List<AvatarSuite> suites = getPageAvatarSuite(pageUrl);
        List<Avatar> collect = avatarSuitesToAvatars(suites, Database.AvatarType.SCENERY);
        Collections.shuffle(collect);
        for (Avatar a : collect) {
            isRepeat |= mDatabase.addAvatar(a);
        }
        mIsStopSceneryCrawler = isRepeat && mDatabase.getAvatarNum(Database.AvatarType.SCENERY) > MAX_VATAR_NUM;
        mSceneryPageNum ++;
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

    private JSONObject requestApi(String pageUrl) {
        JSONObject data = null;
        try {
            URL url = new URL(pageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1000);
            if (200 == conn.getResponseCode()) {
                InputStreamReader reader = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader in = new BufferedReader(reader);
                String line;
                StringBuffer content = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    content.append(line);
                }
                String buf = content.toString().replaceAll("\\n", "");
                if (!StringUtil.isBlank(buf)) {
                    data = new JSONObject(buf);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private List<String> parseJson(JSONObject data) {
        if (null == data) {
            return null;
        }

        List<String> result = new ArrayList<>();
        try {
            JSONArray faces = data.getJSONArray("faces");
            for (int i = 0; i < faces.length(); ++ i) {
                String url = ((JSONObject)faces.get(i)).getString("url");
                result.add(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void crawlFeelSuite() {
        if (sUA.isEmpty()) {
            return;
        }

        List<FeelSuite> suites = CrawlerFeelSuite.crawlDailyFeel("http://qianming.appchizi.com/index.php/NewApi38/index/cid/qutu/p/1/markId/0/pt/c360", sUA);
        if (suites.isEmpty()) {
            return;
        }

        if (mDatabase != null) {
            mDatabase.updateFeelSuite(suites);
            sUA = "";
        }
    }
}
