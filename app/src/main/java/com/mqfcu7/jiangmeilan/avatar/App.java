package com.mqfcu7.jiangmeilan.avatar;

import android.app.Application;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

public class App extends Application {

    private CrawlerThread mCrawlerThread;

    @Override
    public void onCreate() {
        super.onCreate();

        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);;
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "5b6a8031a40fa31d91000304");

        startCrawlerThread();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mCrawlerThread.interrupt();
    }

    private void startCrawlerThread() {
        mCrawlerThread = CrawlerThread.getInstance();
        if (!mCrawlerThread.isAlive()) {
            mCrawlerThread.setDatabase(new Database(getApplicationContext()));
            mCrawlerThread.start();
        }
    }
}
