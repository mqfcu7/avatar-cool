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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "com.mqfcu7.jianmeilan.avatar";
    private static final String TABLE_AVATAR_SUITE = "avatar_suite";
    private static final String TABLE_AVATARS = "avatars";
    private static final String TABLE_FEEL_SUITE = "feel_suite";

    private static final int DATABASE_VERSION = 1;

    private static final int MIN_IMAGE_NUM = 4;
    private static final int MAX_IMAGE_NUM = 10;

    private Context mContext;

    private Random mRandom = new Random();

    public static class AvatarType {
        public static final int GIRL = 1;
        public static final int BOY = 2;
        public static final int LOVES = 3;
        public static final int FRIEND = 4;
        public static final int PET = 5;
        public static final int COMIC = 6;
        public static final int GAME = 7;
        public static final int SCENERY = 8;
    }

    public abstract class AvatarSuiteColumns implements BaseColumns {
        public static final String HASH = "hash";
        public static final String TITLE = "title";
        public static final String DESCRIBE = "describe";
        public static final String IMAGE_NUM = "image_num";
        public static final String IMAGES_URL = "images_url";
        public static final String VISITED = "visited";
    }

    public abstract class AvatarsColumns implements BaseColumns {
        public static final String HASH = "hash";
        public static final String TYPE = "type";
        public static final String IMAGE_URL = "image_url";
    }

    public abstract class FeelSuiteColumns implements BaseColumns {
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String IMAGE_WIDTH = "image_width";
        public static final String IMAGE_HEIGHT = "image_height";
        public static final String IMAGE_URL = "image_url";
        public static final String USER_NAME = "user_name";
        public static final String USER_AVATAR = "user_avatar";
        public static final String TIME = "time";
        public static final String TIME_STR = "time_str";
    }

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createHotAvatarTable(db);
        createAvatarsTable(db);
        createDailyFeelTable(db);
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

        db.execSQL("insert into " + TABLE_AVATAR_SUITE + " values(0,0,'可爱好看的气球','',5,'http://img.woyaogexing.com/touxiang/fengjing/20140210/0a7102a6d6460eb1!200x200.jpg,http://img.woyaogexing.com/touxiang/fengjing/20140210/fd52adf4380896d9!200x200.jpg,http://img.woyaogexing.com/touxiang/fengjing/20140210/5b3c53c748ff7123!200x200.jpg,http://img.woyaogexing.com/touxiang/fengjing/20140210/ab6687ddb2b5a1d9!200x200.jpg,http://img.woyaogexing.com/touxiang/fengjing/20140210/5720409b2caef724!200x200.jpg',0);");
        db.execSQL("insert into " + TABLE_AVATAR_SUITE + " values(1,-21141116,'我以微笑淡流年','',6,'http://img2.woyaogexing.com/2018/08/07/48b7c4dead001777!480x480.jpg,http://img2.woyaogexing.com/2018/08/07/767f2a847e8fe75c!480x480.jpg,http://img2.woyaogexing.com/2018/08/07/ef9f6e435c5ec0d1!480x480.jpg,http://img2.woyaogexing.com/2018/08/07/26e177fb69583be9!480x480.jpg,http://img2.woyaogexing.com/2018/08/07/0260ab4686440d5c!480x480.jpg,http://img2.woyaogexing.com/2018/08/07/513f71835d22ce91!480x480.jpg',0);");
        db.execSQL("insert into " + TABLE_AVATAR_SUITE + " values(2,0,'可爱好看的气球','',4,'http://img.woyaogexing.com/touxiang/fengjing/20140210/0698b9aa4317bad2!200x200.jpg,http://img.woyaogexing.com/touxiang/fengjing/20140210/b8dea471a0fb4a27!200x200.jpg,http://img.woyaogexing.com/touxiang/fengjing/20140210/fcfb3b808ee3519c!200x200.jpg,http://img.woyaogexing.com/touxiang/fengjing/20140210/3345c94f1b4e3458!200x200.jpg',0);");
        db.execSQL("insert into " + TABLE_AVATAR_SUITE + " values(3,578267812,'一对两张情头','',8,'http://img2.woyaogexing.com/2018/08/07/f95ac91fac22455490fb1f494f4bcfb3!400x400.jpeg,http://img2.woyaogexing.com/2018/08/07/1cc78f9ea5e54025873d33c901bf96db!400x400.jpeg,http://img2.woyaogexing.com/2018/08/07/837cdecbc42f4b148db11fa363c6bb2a!400x400.jpeg,http://img2.woyaogexing.com/2018/08/07/d8254541209b43d0ad378ae8ddbb900b!400x400.jpeg,http://img2.woyaogexing.com/2018/08/07/61bbe40d1f564dc3b6837f5978ca4145!400x400.jpeg,http://img2.woyaogexing.com/2018/08/07/60b56845dd514dabbd76eaddb40e8fd6!400x400.jpeg,http://img2.woyaogexing.com/2018/08/07/8b62d2f07c014c53841ca3f4c8d97800!400x400.jpeg,http://img2.woyaogexing.com/2018/08/07/f3e90c0642d746848c124a94b100eed2!400x400.jpeg',0);");
        db.execSQL("insert into " + TABLE_AVATAR_SUITE + " values(4,0,'可爱好看的气球','',4,'http://img.woyaogexing.com/touxiang/fengjing/20140210/eb71e13fd5a6ecd4!200x200.jpg,http://img.woyaogexing.com/touxiang/fengjing/20140210/faa78040e72f2f28!200x200.jpg,http://img.woyaogexing.com/touxiang/fengjing/20140210/c64b7a33c1ef42ff!200x200.jpg,http://img.woyaogexing.com/touxiang/fengjing/20140210/31e59cac2bc07f02!200x200.jpg',0);");
        db.execSQL("insert into " + TABLE_AVATAR_SUITE + " values(5,-334616750,'春风十里不如你','',9,'http://img2.woyaogexing.com/2018/08/06/be0337087bf841ae832e0608216d921a!400x400.jpeg,http://img2.woyaogexing.com/2018/08/06/af0a4df70f084443a83f206facab8a57!400x400.jpeg,http://img2.woyaogexing.com/2018/08/06/b384a316adee4245a5f0af6d89a44352!400x400.jpeg,http://img2.woyaogexing.com/2018/08/06/f26eaf477fc4471d91be79ac946ccbe1!400x400.jpeg,http://img2.woyaogexing.com/2018/08/06/f7a0f83042384278be34edb3c107778b!400x400.jpeg,http://img2.woyaogexing.com/2018/08/06/87405feb0b024302b5010dcb5a50380f!400x400.jpeg,http://img2.woyaogexing.com/2018/08/06/ea29deddb8c64e73ae0b45a96e3656c6!400x400.jpeg,http://img2.woyaogexing.com/2018/08/06/0bf2d1eeb31043b8906e6a8f2af7ad5b!400x400.jpeg,http://img2.woyaogexing.com/2018/08/06/e11a19723d60407390863d5a063b3477!400x400.jpeg',0);");
        db.execSQL("insert into " + TABLE_AVATAR_SUITE + " values(6,1466955915,'超酷的小哥哥，来来回却只有我这个陌生人。','',5,'http://img2.woyaogexing.com/2018/08/07/c3acaaa597144d639fc6fc73083601cb!400x400.jpeg,http://img2.woyaogexing.com/2018/08/07/4856a6f97b5c4410a676d12a11d159a2!400x400.jpeg,http://img2.woyaogexing.com/2018/08/07/5a83e435175d41c4988e42f9d8d5eb73!400x400.jpeg,http://img2.woyaogexing.com/2018/08/07/6bc2840f9404488db329712b3e7c7e56!400x400.jpeg,http://img2.woyaogexing.com/2018/08/07/1c140ff500844a0483b31f45b83845d6!400x400.jpeg',0);");
    }

    private void createAvatarsTable(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_AVATARS + " ("
                + AvatarsColumns._ID + " integer primary key,"
                + AvatarsColumns.HASH + " integer,"
                + AvatarsColumns.TYPE + " integer,"
                + AvatarsColumns.IMAGE_URL + " text"
                + ");");
    }

    private void createDailyFeelTable(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_FEEL_SUITE + " ("
                + FeelSuiteColumns._ID + " integer primary key,"
                + FeelSuiteColumns.ID + " integer,"
                + FeelSuiteColumns.TITLE + " text,"
                + FeelSuiteColumns.IMAGE_WIDTH + " integer,"
                + FeelSuiteColumns.IMAGE_HEIGHT + " integer,"
                + FeelSuiteColumns.IMAGE_URL + " text,"
                + FeelSuiteColumns.USER_NAME + " text,"
                + FeelSuiteColumns.USER_AVATAR + " text,"
                + FeelSuiteColumns.TIME + " long,"
                + FeelSuiteColumns.TIME_STR + " text"
                + ");");
        db.execSQL("insert into " + TABLE_FEEL_SUITE + " values(0,78615,'人生永远没有最晚的开始，真正晚的是你从未开始。',440,550,'http://wx3.sinaimg.cn/bmiddle/9cf33a33ly1fu4h3auhl6j20u011i783.jpg','时光切片','http://tva1.sinaimg.cn/crop.0.0.180.180.180/90ac16aejw1e8qgp5bmzyj2050050aa8.jpg',1533875336,'8月10日 12:28');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long getNewAvatarSuiteNum() {
        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_AVATAR_SUITE, AvatarSuiteColumns.VISITED + "=0");
    }

    public long getAvatarNum(int type) {
        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_AVATARS, AvatarsColumns.TYPE + "=" + type);
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

    public boolean addAvatar(final Avatar avatar) {
        if (isExistAvatar(avatar)) return false;

        ContentValues values = new ContentValues();
        values.put(AvatarsColumns.HASH, avatar.hash);
        values.put(AvatarsColumns.TYPE, avatar.type);
        values.put(AvatarsColumns.IMAGE_URL, avatar.url);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_AVATARS, AvatarsColumns._ID, values);

        return true;
    }

    private boolean isExistAvatar(final Avatar avatar) {
        boolean result = false;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_AVATARS);
        qb.appendWhere(AvatarsColumns.HASH + "=" + avatar.hash);
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

    public List<Avatar> getBatchAvatars(int start_id, int type, int max_num) {
        List<Avatar> avatars = new LinkedList<>();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_AVATARS);
        qb.appendWhere(AvatarsColumns._ID + ">" + start_id
                + " and " + AvatarsColumns.TYPE + "=" + type);

        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);
            while (c.moveToNext() && max_num > 0) {
                Avatar a = new Avatar();
                a.id = c.getInt(c.getColumnIndex(AvatarsColumns._ID));
                a.hash = c.getInt(c.getColumnIndex(AvatarsColumns.HASH));
                a.type = c.getInt(c.getColumnIndex(AvatarsColumns.TYPE));
                a.url = c.getString(c.getColumnIndex(AvatarsColumns.IMAGE_URL));
                avatars.add(a);
                max_num --;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return avatars;
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

    public FeelSuite getFeelSuite() {
        List<FeelSuite> list = getBatchFeelSuites();
        if (list.isEmpty()) {
            FeelSuite feel = new FeelSuite();
            feel.id = 78615;
            feel.title = "人生永远没有最晚的开始，真正晚的是你从未开始";
            feel.userUrl = "http://tva1.sinaimg.cn/crop.0.0.180.180.180/90ac16aejw1e8qgp5bmzyj2050050aa8.jpg";
            feel.userName = "时光切片";
            feel.timeStr = "8月10日 12:28";
        }

        return list.get(mRandom.nextInt(list.size()));
    }

    public List<FeelSuite> getBatchFeelSuites() {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_FEEL_SUITE);

        List<FeelSuite> list = new LinkedList<>();
        Cursor c = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            c = qb.query(db, null, null, null, null, null, null);
            while (c.moveToNext()) {
                FeelSuite feel = parseFeelSuite(c);
                list.add(feel);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return list;
    }

    public void updateFeelSuite(List<FeelSuite> suites) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + TABLE_FEEL_SUITE);
        db.execSQL("VACUUM");
        for (FeelSuite feel : suites) {
            ContentValues values = new ContentValues();
            values.put(FeelSuiteColumns.ID, feel.id);
            values.put(FeelSuiteColumns.TITLE, feel.title);
            values.put(FeelSuiteColumns.IMAGE_WIDTH, feel.imageWidth);
            values.put(FeelSuiteColumns.IMAGE_HEIGHT, feel.imageHeight);
            values.put(FeelSuiteColumns.IMAGE_URL, feel.imageUrl);
            values.put(FeelSuiteColumns.USER_NAME, feel.userName);
            values.put(FeelSuiteColumns.USER_AVATAR, feel.userUrl);
            values.put(FeelSuiteColumns.TIME, feel.time);
            values.put(FeelSuiteColumns.TIME_STR, feel.timeStr);

            db.insert(TABLE_FEEL_SUITE, FeelSuiteColumns._ID, values);
        }
    }

    private FeelSuite parseFeelSuite(Cursor c) {
        FeelSuite feel = new FeelSuite();
        feel.id = c.getInt(c.getColumnIndex(FeelSuiteColumns.ID));
        feel.title = c.getString(c.getColumnIndex(FeelSuiteColumns.TITLE));
        feel.imageWidth = c.getInt(c.getColumnIndex(FeelSuiteColumns.IMAGE_WIDTH));
        feel.imageHeight = c.getInt(c.getColumnIndex(FeelSuiteColumns.IMAGE_HEIGHT));
        feel.imageUrl = c.getString(c.getColumnIndex(FeelSuiteColumns.IMAGE_URL));
        feel.userName = c.getString(c.getColumnIndex(FeelSuiteColumns.USER_NAME));
        feel.userUrl = c.getString(c.getColumnIndex(FeelSuiteColumns.USER_AVATAR));
        feel.time = c.getLong(c.getColumnIndex(FeelSuiteColumns.TIME));
        feel.timeStr = c.getString(c.getColumnIndex(FeelSuiteColumns.TIME_STR));
        return feel;
    }
}
