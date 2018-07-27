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
        return mDatabase.getBatchAvatarSuites(5, false);
    }
}
