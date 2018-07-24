package com.mqfcu7.jiangmeilan.avatar;

import com.mqfcu7.jiangmeilan.avatar.databinding.ActivityMainBinding;
import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mBinding;

    private Map<Integer, String> mHandlerFunction = new HashMap<>();
    private MainHandler mHandler;

    private class MainHandler extends Handler {
        WeakReference<Activity> mActivity;
        public MainHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            for (Map.Entry<Integer, String> entry : mHandlerFunction.entrySet()) {
                if (entry.getKey().equals(msg.what)) {
                    try {
                        Utils.invokeMethod(mActivity.get(), entry.getValue(), new Object[]{msg});
                    } catch (Exception e) {
                        Log.w("TAG", e.toString());
                    }
                }
            }
        }
    }

    public MainActivity() {
        mHandlerFunction.put(Utils.MSG_TYPE_DAILY_AVATAR, "onDailyAvatar");

        mHandler = new MainHandler(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.dailyAvatarInclude.mainAvatarSuiteViewGroup.setAvatarSuite(null);

        HtmlParser parser = new HtmlParser();
        parser.asynRandomAvatarSuite(mHandler);
    }

    private void onDailyAvatar(Message msg) {
        if (msg.obj == null) return;

        AvatarSuite avatarSuite = (AvatarSuite) msg.obj;
    }
}
