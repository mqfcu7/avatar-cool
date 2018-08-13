package com.mqfcu7.jiangmeilan.avatar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class CrawlerFeelSuite extends Object {
    private String mUA;
    private int mLastId;

    public void init(String ua) {
        mUA = ua;
    }

    public List<FeelSuite> getNewestFeelSuites() {
        String urlPage = "http://qianming.appchizi.com/index.php/NewApi38/index/cid/qutu/p/1/markId/0/pt/c360";
        List<FeelSuite> suites = crawlDailyFeel(urlPage, mUA);
        if (!suites.isEmpty()) {
            mLastId = ((LinkedList<FeelSuite>) suites).getLast().id - 1;
        }
        return suites;
    }

    public List<FeelSuite> getLastFeelSuites() {
        String urlPage = "http://qianming.appchizi.com/index.php/NewApi38/index/cid/qutu/lastId/" + mLastId + "/pt/c360";
        List<FeelSuite> suites = crawlLasterDailyFeel(urlPage, mUA);
        if (!suites.isEmpty()) {
            mLastId = ((LinkedList<FeelSuite>) suites).getLast().id - 1;
        }
        return suites;
    }

    private static JSONObject requestApi(String pageUrl, String ua) {
        JSONObject data = null;
        try {
            URL url = new URL(pageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", ua);
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
                data = new JSONObject(buf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private JSONArray requestApiArray(String pageUrl, String ua) {
        JSONArray data = null;
        try {
            URL url = new URL(pageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", ua);
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
                data = new JSONArray(buf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static List<FeelSuite> crawlDailyFeel(String pageUrl, String ua) {
        List<FeelSuite> feelSuites = new LinkedList<>();

        JSONObject json = requestApi(pageUrl, ua);
        if (json != null) {
            try {
                JSONArray array = json.getJSONArray("rows");
                for (int i = 0; i < array.length(); ++ i) {
                    JSONObject object = (JSONObject) array.get(i);
                    FeelSuite feel = new FeelSuite();
                    feel.id = Integer.parseInt(object.getString("id"));
                    feel.title = Utils.getUTF8StringFromGBKString(object.getString("title"));
                    feel.imageUrl = object.getString("pic").replaceAll("\\\\", "");
                    feel.imageWidth = Integer.parseInt(object.getString("pic_w"));
                    feel.imageHeight = Integer.parseInt(object.getString("pic_h"));
                    feel.userName = Utils.getUTF8StringFromGBKString(object.getString("uname"));
                    feel.userUrl = object.getString("upic").replaceAll("\\\\", "");
                    feel.time = Long.parseLong(object.getString("cTime"));
                    feel.timeStr = Utils.getUTF8StringFromGBKString(object.getString("timeStr"));
                    boolean isAdd = true;
                    for (FeelSuite s : feelSuites) {
                        if (s.title.equals(feel.title)) {
                            isAdd = false;
                            break;
                        }
                    }
                    if (isAdd) {
                        feelSuites.add(feel);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(feelSuites, new Comparator<FeelSuite>() {
            @Override
            public int compare(FeelSuite o1, FeelSuite o2) {
                if (o1.time <= o2.time) {
                    return 1;
                }
                return -1;
            }
        });
        return feelSuites;
    }

    private List<FeelSuite> crawlLasterDailyFeel(String pageUrl, String ua) {
        List<FeelSuite> feelSuites = new LinkedList<>();

        JSONArray array = requestApiArray(pageUrl, ua);
        if (array != null) {
            try {
                for (int i = 0; i < array.length(); ++ i) {
                    JSONObject object = (JSONObject) array.get(i);
                    FeelSuite feel = new FeelSuite();
                    feel.id = Integer.parseInt(object.getString("id"));
                    feel.title = Utils.getUTF8StringFromGBKString(object.getString("title"));
                    feel.imageUrl = object.getString("pic").replaceAll("\\\\", "");
                    feel.imageWidth = Integer.parseInt(object.getString("pic_w"));
                    feel.imageHeight = Integer.parseInt(object.getString("pic_h"));
                    feel.userName = Utils.getUTF8StringFromGBKString(object.getString("uname"));
                    feel.userUrl = object.getString("upic").replaceAll("\\\\", "");
                    feel.time = Long.parseLong(object.getString("cTime"));
                    feel.timeStr = Utils.getUTF8StringFromGBKString(object.getString("timeStr"));
                    boolean isAdd = true;
                    for (FeelSuite s : feelSuites) {
                        if (s.title.equals(feel.title)) {
                            isAdd = false;
                            break;
                        }
                    }
                    if (isAdd) {
                        feelSuites.add(feel);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(feelSuites, new Comparator<FeelSuite>() {
            @Override
            public int compare(FeelSuite o1, FeelSuite o2) {
                if (o1.time <= o2.time) {
                    return 1;
                }
                return -1;
            }
        });
        return feelSuites;
    }
}
