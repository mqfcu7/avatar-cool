package com.mqfcu7.jiangmeilan.avatar;

import com.bumptech.glide.Glide;
import com.mqfcu7.jiangmeilan.avatar.databinding.ActivityMainBinding;
import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int MAX_HOT_AVATAR_PAGE_NUM = 10;
    ActivityMainBinding mBinding;

    private AvatarSuiteAdapter mHotAvatarAdapter;
    private RecyclerView mHotAvatarRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;
    private NestedScrollView mNestedScrollView;

    private CrawlerThread mCrawlerThread;
    private AvatarSuiteGenerator mAvatarSuiteGenerator;

    private int mHotPageNum;

    private class AvatarSuiteHolder extends RecyclerView.ViewHolder {
        public AvatarSuiteLayout mAvatarSuiteLayout;

        public AvatarSuiteHolder(View itemView) {
            super(itemView);

            mAvatarSuiteLayout = itemView.findViewById(R.id.list_item_avatar_suite_layout);
        }

        public void bindAvatarSuite(AvatarSuite avatarSuite) {
            mAvatarSuiteLayout.setAvatarSuite(avatarSuite);
        }
    }

    private class AvatarSuiteAdapter extends RecyclerView.Adapter<AvatarSuiteHolder> {
        private List<AvatarSuite> mAvatarSuites;

        public AvatarSuiteAdapter(List<AvatarSuite> avatarSuites) {
            mAvatarSuites = new LinkedList<>();
            mAvatarSuites.addAll(avatarSuites);
        }

        @NonNull
        @Override
        public AvatarSuiteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View v = layoutInflater.inflate(R.layout.list_item_hot_avatar, parent,false);
            return new AvatarSuiteHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull AvatarSuiteHolder holder, int position) {
            AvatarSuite avatarSuite = mAvatarSuites.get(position);
            holder.bindAvatarSuite(avatarSuite);
        }

        @Override
        public void onViewRecycled(@NonNull AvatarSuiteHolder holder) {
            super.onViewRecycled(holder);
            holder.mAvatarSuiteLayout.onReset();
        }

        @Override
        public int getItemCount() {
            return mAvatarSuites.size();
        }

        public void pushItems(List<AvatarSuite> avatarSuites) {
            mAvatarSuites.addAll(0, avatarSuites);
        }

        public void appendItems(List<AvatarSuite> avatarSuites) {
            mAvatarSuites.addAll(avatarSuites);
        }
    }

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mCrawlerThread = CrawlerThread.getInstance();
        if (!mCrawlerThread.isAlive()) {
            mCrawlerThread.setDatabase(new Database(getApplicationContext()));
            mCrawlerThread.start();
        }

        mAvatarSuiteGenerator = new AvatarSuiteGenerator(getApplicationContext());

        initDailyAvatar();
        initHotAvatar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCrawlerThread.interrupt();
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSwipeLayout.setRefreshing(false);

                                mHotAvatarAdapter.pushItems(mAvatarSuiteGenerator.getUpdateAvatarSuites(5));
                                mHotAvatarAdapter.notifyItemRangeChanged(0, 5);
                            }
                        });
                    }
                }).start();
            }
        });

        mHotAvatarRecyclerView = mBinding.hotAvatarInclude.mainHotRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setSmoothScrollbarEnabled(false);
        mHotAvatarRecyclerView.setLayoutManager(linearLayoutManager);
        if (mHotAvatarAdapter == null) {
            mHotAvatarAdapter = new AvatarSuiteAdapter(mAvatarSuiteGenerator.getInitAvatarSuites(5));
            mHotAvatarRecyclerView.setAdapter(mHotAvatarAdapter);
        }
        mHotAvatarRecyclerView.setNestedScrollingEnabled(false);
        mHotAvatarRecyclerView.setItemViewCacheSize(20);
        mHotAvatarRecyclerView.setDrawingCacheEnabled(true);
        mHotAvatarRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //ViewCompat.setNestedScrollingEnabled(mHotAvatarRecyclerView, false);

        mNestedScrollView = mBinding.mainNestedScrollView;
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    /*
                    if (mHotPageNum > MAX_HOT_AVATAR_PAGE_NUM) {
                        return;
                    }
                    */
                    int pos = mHotAvatarAdapter.getItemCount();
                    mHotAvatarAdapter.appendItems(mAvatarSuiteGenerator.getUpdateAvatarSuites(5));
                    mHotAvatarAdapter.notifyItemRangeChanged(pos, 5);
                    mHotPageNum ++;
                    Glide.get(getApplicationContext()).clearMemory();
                }
            }
        });
    }
}
