package com.mqfcu7.jiangmeilan.avatar;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class AvatarSuiteGenerator {

    private Database mDatabase;

    public AvatarSuiteGenerator(Context context) {
        mDatabase = new Database(context);
    }

    public AvatarSuite randomAvatarSuite() {
        AvatarSuite as = new AvatarSuite();
        as.title = "好看的女生头像高清 蜜语季节";
        as.images_url = new ArrayList<>();
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/c0b2577153393133!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/353354b630a9aa5e!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/efb755d6db4be33e!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/7eccd5fb0ed224d4!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/e6ec6c3fc7942303!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/24/c84bf85105cf900e!275x275_big.jpg");

        return as;
    }

    public List<AvatarSuite> getInitAvatarSuites() {
        return mDatabase.getBatchAvatarSuites(5, true);
    }

    public List<AvatarSuite> getUpdateAvatarSuites() {
        List<AvatarSuite> result = new ArrayList<>();

        AvatarSuite as = new AvatarSuite();
        as.title = "戴帽可爱单纯小清新的女生";
        as.images_url = new ArrayList<>();
        as.images_url.add("https://img2.woyaogexing.com/2018/07/25/41bdb7f1c02f6f0e!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/25/521c7badcd36392e!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/25/259196f0bff37fbe!400x400_big.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/25/cacd56155cf5bc8a!400x400_big.jpg");
        result.add(as);

        as = new AvatarSuite();
        as.title = "我想把思念谱成一缕阳光";
        as.images_url = new ArrayList<>();
        as.images_url.add("https://img2.woyaogexing.com/2018/07/25/9f2bfc5dee955cf9!480x480.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/25/d27d5157ed330895!480x480.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/25/9eecd7f661dc43cb!480x480.jpg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/25/965c743b927a83e3!480x480.jpg");
        result.add(as);

        as = new AvatarSuite();
        as.title = "林酒：蓝色系淡雅风动漫女头♡感谢你的出现";
        as.images_url = new ArrayList<>();
        as.images_url.add("https://img2.woyaogexing.com/2018/07/25/01ec308b39b6491f87487fa58ca3ad7f!400x400.jpeg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/25/87ec7bca49cc4cb5a27d935a5cfad6b2!400x400.jpeg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/25/611c001bdfc048b9a0e77b688cebebe8!400x400.jpeg");
        as.images_url.add("https://img2.woyaogexing.com/2018/07/25/61bce76b58dc49de9e425e4f43c3c198!400x400.jpeg");
        result.add(as);

        return result;
    }
}
