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
        return mDatabase.getBatchAvatarSuites(1, true).get(0);
    }

    public List<AvatarSuite> getInitAvatarSuites(int n) {
        return mDatabase.getBatchAvatarSuites(n, true);
    }

    public List<AvatarSuite> getUpdateAvatarSuites(int n) {
        return mDatabase.getBatchAvatarSuites(n, false);
    }
}
