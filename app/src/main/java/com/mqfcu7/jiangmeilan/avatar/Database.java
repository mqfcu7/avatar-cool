package com.mqfcu7.jiangmeilan.avatar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "com.mqfcu7.jianmeilan.avatar";
    private static final String TABLE_AVATAR_SUITE = "avatar_suite";

    private static final int DATABASE_VERSION = 1;

    private static final int MIN_IMAGE_NUM = 4;
    private static final int MAX_IMAGE_NUM = 10;

    private Context mContext;

    private Random mRandom = new Random();

    public abstract class AvatarSuiteColumns implements BaseColumns {
        public static final String HASH = "hash";
        public static final String TITLE = "title";
        public static final String DESCRIBE = "describe";
        public static final String IMAGE_NUM = "image_num";
        public static final String IMAGES_URL = "images_url";
        public static final String VISITED = "visited";
    }

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createHotAvatarTable(db);
    }

    private void createHotAvatarTable(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_AVATAR_SUITE + " ("
                + AvatarSuiteColumns._ID + " integer primary key,"
                + AvatarSuiteColumns.HASH + " integer,"
                + AvatarSuiteColumns.TITLE + " text,"
                + AvatarSuiteColumns.DESCRIBE + " text,"
                + AvatarSuiteColumns.IMAGE_NUM + " integer,"
                + AvatarSuiteColumns.IMAGES_URL + " text,"
                + AvatarSuiteColumns.VISITED + " integer"
                + ");");

        db.execSQL("insert into " + TABLE_AVATAR_SUITE + " values(0,0,'可爱好看的气球','',4,'http://img.woyaogexing.com/touxiang/fengjing/20140210/0a7102a6d6460eb1!200x200.jpg,http://img.woyaogexing.com/touxiang/fengjing/20140210/fd52adf4380896d9!200x200.jpg,http://img.woyaogexing.com/touxiang/fengjing/20140210/5b3c53c748ff7123!200x200.jpg,http://img.woyaogexing.com/touxiang/fengjing/20140210/ab6687ddb2b5a1d9!200x200.jpg',0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long getNewAvatarSuiteNum() {
        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_AVATAR_SUITE, AvatarSuiteColumns.VISITED + "=0");
    }

    public boolean isExistAvatarSuite(final AvatarSuite avatarSuite) {
        boolean result = false;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_AVATAR_SUITE);
        qb.appendWhere(AvatarSuiteColumns.HASH + "=" + avatarSuite.hash);
        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);
            if (c.moveToFirst()) {
                result = true;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return  result;
    }

    public boolean addAvatarSuite(final AvatarSuite avatarSuite) {
        if (isExistAvatarSuite(avatarSuite)) return false;

        ContentValues values = new ContentValues();
        values.put(AvatarSuiteColumns.HASH, avatarSuite.hash);
        values.put(AvatarSuiteColumns.TITLE, avatarSuite.title);
        values.put(AvatarSuiteColumns.DESCRIBE, avatarSuite.describe);
        values.put(AvatarSuiteColumns.IMAGE_NUM, avatarSuite.images_url.size());
        values.put(AvatarSuiteColumns.IMAGES_URL, serializeImagesUrl(avatarSuite.images_url));
        values.put(AvatarSuiteColumns.VISITED, 0);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_AVATAR_SUITE, AvatarSuiteColumns._ID, values);

        return true;
    }

    public List<AvatarSuite> getBatchAvatarSuites(int num, boolean is_strict) {
        List<AvatarSuite> result = new ArrayList<>();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_AVATAR_SUITE);
        qb.appendWhere(AvatarSuiteColumns.VISITED + "=0");

        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);
            while (c.moveToNext() && num > 0) {
                result.add(buildAvatarSuite(c));
                num --;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        updateVisitedAvatarSuites(result);

        if (is_strict && num > 0) {
            result.addAll(getOldAvatarSuites(num));
        }

        return result;
    }

    private List<AvatarSuite> getOldAvatarSuites(int num) {
        List<AvatarSuite> result = new ArrayList<>();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_AVATAR_SUITE);
        qb.appendWhere(AvatarSuiteColumns.VISITED + "=1");

        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, AvatarSuiteColumns._ID + " desc");
            while (c.moveToNext() && num > 0) {
                result.add(buildAvatarSuite(c));
                num --;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return result;
    }

    private void updateVisitedAvatarSuites(List<AvatarSuite> avatarSuites) {
        SQLiteDatabase db = getWritableDatabase();
        for (AvatarSuite as : avatarSuites) {
            ContentValues values = new ContentValues();
            values.put(AvatarSuiteColumns.VISITED, 1);
            db.update(TABLE_AVATAR_SUITE, values, AvatarSuiteColumns._ID + "=" + as.id, null);
        }
    }

    private AvatarSuite buildAvatarSuite(Cursor c) {
        AvatarSuite as = new AvatarSuite();
        as.id = c.getInt(c.getColumnIndex(AvatarSuiteColumns._ID));
        as.hash = c.getInt(c.getColumnIndex(AvatarSuiteColumns.HASH));
        as.title = c.getString(c.getColumnIndex(AvatarSuiteColumns.TITLE));
        as.describe = c.getString(c.getColumnIndex(AvatarSuiteColumns.DESCRIBE));
        as.images_url = parseImagesUrl(c.getString(c.getColumnIndex(AvatarSuiteColumns.IMAGES_URL)));
        return as;
    }

    private List<String> parseImagesUrl(String images_url) {
        List<String> result = new ArrayList<>();

        String[] items = images_url.split(",");
        for (int i = 0; i < items.length; ++ i) {
            result.add(items[i]);
        }

        return result;
    }

    private String serializeImagesUrl(final List<String> images_url) {
        return  StringUtil.join(images_url, ",");
    }
}
