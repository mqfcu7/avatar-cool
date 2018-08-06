package com.mqfcu7.jiangmeilan.avatar;

import android.app.Application;

public class App extends Application {

    private CrawlerThread mCrawlerThread;

    @Override
    public void onCreate() {
        super.onCreate();

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
