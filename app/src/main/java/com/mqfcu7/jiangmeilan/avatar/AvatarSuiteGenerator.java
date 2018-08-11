package com.mqfcu7.jiangmeilan.avatar;

import android.content.Context;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AvatarSuiteGenerator {

    private Database mDatabase;

    public AvatarSuiteGenerator(Context context) {
        mDatabase = new Database(context);
    }

    public AvatarSuite randomAvatarSuite() {
        List<AvatarSuite> suites = mDatabase.getBatchAvatarSuites(1, true);
        if (!suites.isEmpty()) {
            return suites.get(0);
        }

        AvatarSuite s = new AvatarSuite();
        s.title = "可爱好看的气球";
        s.images_url = new LinkedList<>();
        s.images_url.add("http://img.woyaogexing.com/touxiang/fengjing/20140210/0a7102a6d6460eb1!200x200.jpg");
        s.images_url.add("http://img.woyaogexing.com/touxiang/fengjing/20140210/fd52adf4380896d9!200x200.jpg");
        s.images_url.add("http://img.woyaogexing.com/touxiang/fengjing/20140210/5b3c53c748ff7123!200x200.jpg");
        s.images_url.add("http://img.woyaogexing.com/touxiang/fengjing/20140210/ab6687ddb2b5a1d9!200x200.jpg");
        s.images_url.add("http://img.woyaogexing.com/touxiang/fengjing/20140210/5720409b2caef724!200x200.jpg");
        return s;
    }

    public List<AvatarSuite> getInitAvatarSuites(int n) {
        return mDatabase.getBatchAvatarSuites(n, true);
    }

    public List<AvatarSuite> getUpdateAvatarSuites(int n) {
        return mDatabase.getBatchAvatarSuites(n, false);
    }
}
