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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mBinding;

    private Map<Integer, String> mHandlerFunction = new HashMap<>();
    private MainHandler mHandler;

    private AvatarSuiteAdapter mHotAvatarAdapter;
    private RecyclerView mHotAvatarRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;

    CrawlerThread mCrawlerThread;
    private AvatarSuiteGenerator mAvatarSuiteGenerator;

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
        mHandlerFunction.put(Utils.MSG_TYPE_DAILY_AVATAR, "onDailyAvatar");

        mHandler = new MainHandler(this);
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

                                mHotAvatarAdapter.pushItems(mAvatarSuiteGenerator.getUpdateAvatarSuites());
                                mHotAvatarAdapter.notifyItemRangeChanged(0, 5);
                            }
                        });
                    }
                }).start();
            }
        });

        mHotAvatarRecyclerView = mBinding.hotAvatarInclude.mainHotRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(false);
        mHotAvatarRecyclerView.setLayoutManager(linearLayoutManager);
        if (mHotAvatarAdapter == null) {
            mHotAvatarAdapter = new AvatarSuiteAdapter(mAvatarSuiteGenerator.getInitAvatarSuites());
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
                LinearLayoutManager l = (LinearLayoutManager) recyclerView.getLayoutManager();
                int curPos = l.findFirstVisibleItemPosition();
                int total = l.getItemCount();
                Log.d("TAG", "pos: " + curPos + ", total: " + total);
            }
        });
    }

    private void onDailyAvatar(Message msg) {
        if (msg.obj == null) return;

        AvatarSuite avatarSuite = (AvatarSuite) msg.obj;
    }
}
