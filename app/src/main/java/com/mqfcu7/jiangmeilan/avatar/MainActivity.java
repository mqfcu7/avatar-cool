package com.mqfcu7.jiangmeilan.avatar;

import com.mqfcu7.jiangmeilan.avatar.databinding.ActivityMainBinding;
import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mBinding;

    private Map<Integer, String> mHandlerFunction = new HashMap<>();
    private MainHandler mHandler;

    private AvatarSuiteAdapter mHotAvatarAdapter;
    private RecyclerView mHotAvatarRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;

    private AvatarSuiteGenerator mAvatarSuiteGenerator = new AvatarSuiteGenerator();

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

    private class AvatarSuiteHolder extends RecyclerView.ViewHolder {
        private AvatarSuiteLayout mAvatarSuiteLayout;

        public AvatarSuiteHolder(View itemView) {
            super(itemView);

            mAvatarSuiteLayout = itemView.findViewById(R.id.list_item_avatar_suite_layout);
        }

        public void bindAvatarSuite(AvatarSuite avatarSuite) {
            mAvatarSuiteLayout.setAvatarSuite(avatarSuite);
            Log.d("TAG", avatarSuite.title);
        }
    }

    private class AvatarSuiteAdapter extends RecyclerView.Adapter<AvatarSuiteHolder> {
        private List<AvatarSuite> mAvatarSuites;

        public AvatarSuiteAdapter(List<AvatarSuite> avatarSuites) {
            mAvatarSuites = avatarSuites;
            Log.d("TAG", "size: " + mAvatarSuites.size());
        }

        @NonNull
        @Override
        public AvatarSuiteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d("TAG", "onCreateViewHolder");
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View v = layoutInflater.inflate(R.layout.list_item_hot_avatar, parent,false);
            return new AvatarSuiteHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull AvatarSuiteHolder holder, int position) {
            Log.d("TAG", "onBindViewHolder: " + position);
            AvatarSuite avatarSuite = mAvatarSuites.get(position);
            holder.bindAvatarSuite(avatarSuite);
        }

        @Override
        public int getItemCount() {
            return mAvatarSuites.size();
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

        HtmlParser parser = new HtmlParser();
        parser.asynRandomAvatarSuite(mHandler);

        initDailyAvatar();
        initHotAvatar();
    }

    private void initDailyAvatar() {
        mBinding.dailyAvatarInclude.mainAvatarSuiteLayout.setAvatarSuite(mAvatarSuiteGenerator.randomAvatarSuite());
    }

    private void initHotAvatar() {
        mSwipeLayout = mBinding.mainSwipeRefreshLayout;
        mSwipeLayout.setDistanceToTriggerSync(300);
        mSwipeLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED);
        mSwipeLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        mSwipeLayout.setSize(SwipeRefreshLayout.LARGE);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mHotAvatarAdapter.notifyDataSetChanged();
            }
        });

        mHotAvatarRecyclerView = mBinding.hotAvatarInclude.mainHotRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        mHotAvatarRecyclerView.setLayoutManager(linearLayoutManager);
        if (mHotAvatarAdapter == null) {
            mHotAvatarAdapter = new AvatarSuiteAdapter(mAvatarSuiteGenerator.getBatchAvatarSuites(3));
            mHotAvatarRecyclerView.setAdapter(mHotAvatarAdapter);
        }
        mHotAvatarRecyclerView.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(mHotAvatarRecyclerView, false);
        mHotAvatarRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void onDailyAvatar(Message msg) {
        if (msg.obj == null) return;

        AvatarSuite avatarSuite = (AvatarSuite) msg.obj;
    }
}
