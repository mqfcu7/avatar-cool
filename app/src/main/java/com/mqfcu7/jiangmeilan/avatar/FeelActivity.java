package com.mqfcu7.jiangmeilan.avatar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.mqfcu7.jiangmeilan.avatar.databinding.ActivityFeelBinding;
import com.umeng.analytics.MobclickAgent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class FeelActivity extends AppCompatActivity {
    private ActivityFeelBinding mBinding;
    private RecyclerView mRecyclerView;
    private RecyclerAdapterWithHF mAdapter;
    private PtrClassicFrameLayout mFrameLayout;
    private CrawlerFeelSuite mCrawlerFeelSuite = new CrawlerFeelSuite();

    private List<FeelSuite> mFeelList = new LinkedList<>();
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_feel);
        Utils.setStatusBarLightMode(this, getWindow(), true);
        mCrawlerFeelSuite.init(Utils.getUserAgent(getApplicationContext()));

        initBackBanner();
        createFeelList();
        createFrameLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initBackBanner() {
        mBinding.feelBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createFeelList() {
        mRecyclerView = mBinding.feelRecycleView;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FeelAdapter adapter = new FeelAdapter(mFeelList);
        mAdapter = new RecyclerAdapterWithHF(adapter);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void createFrameLayout() {
        mFrameLayout = mBinding.feelFrameLayout;
        mFrameLayout.setLoadMoreEnable(true);
        mFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFrameLayout.autoRefresh(true);
            }
        }, 100);
        mFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateNewestFeelList();
                    }
                }).start();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mFrameLayout.refreshComplete();
                    }
                }, 1000);
            }
        });
        mFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateLasterFeelList();
                    }
                }).start();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mFrameLayout.loadMoreComplete(true);
                    }
                }, 1000);
            }
        });
    }

    private int updateNewestFeelList() {
        Set<Integer> idSet = getFeelIdSet();
        List<FeelSuite> suites = mCrawlerFeelSuite.getNewestFeelSuites();
        Collections.reverse(suites);
        int cnt = 0;
        for (FeelSuite feel : suites) {
            if (idSet.contains(feel.id)) {
                continue;
            }
            if (feel.id == 100000001) {
                continue;
            }
            ((LinkedList<FeelSuite>) mFeelList).addFirst(feel);
            cnt ++;
        }
        return cnt;
    }

    private int updateLasterFeelList() {
        Set<Integer> idSet = getFeelIdSet();
        List<FeelSuite> suites = mCrawlerFeelSuite.getLastFeelSuites();
        int cnt = 0;
        for (FeelSuite feel : suites) {
            if (idSet.contains(feel.id)) {
                continue;
            }
            if (feel.id == 100000001) {
                continue;
            }
            ((LinkedList<FeelSuite>) mFeelList).addLast(feel);
            cnt ++;
        }
        return cnt;
    }

    private Set<Integer> getFeelIdSet() {
        Set<Integer> idSet = new TreeSet<>();
        for (FeelSuite feel : mFeelList) {
            idSet.add(feel.id);
        }
        return idSet;
    }

    private class FeelHolder extends RecyclerView.ViewHolder {
        private View mView;
        private ImageView mUserImage;
        private TextView mUserName;
        private TextView mTitle;
        private FeelImageLayout mImage;
        private TextView mTime;
        private LinearLayout mCopyLayout;

        public FeelHolder(View v) {
            super(v);

            mView = v;
            mUserImage = (ImageView)v.findViewById(R.id.item_feel_user_image);
            mUserName = (TextView)v.findViewById(R.id.item_feel_user_name_text);
            mTitle = (TextView)v.findViewById(R.id.item_feel_title_text);
            mImage = (FeelImageLayout) v.findViewById(R.id.item_feel_image);
            mTime = (TextView)v.findViewById(R.id.item_feel_time_text);
            mCopyLayout = (LinearLayout)v.findViewById(R.id.item_feel_copy_layout);
        }

        public void bindFeel(final FeelSuite feel) {
            mUserName.setText(feel.userName);
            Glide.with(mView.getContext())
                    .load(feel.userUrl)
                    .apply(new RequestOptions().override(mUserImage.getWidth(), mUserImage.getHeight()))
                    .apply(new RequestOptions().circleCrop())
                    .into(mUserImage);
            mTitle.setText(feel.title);

            mImage.setImage(feel.imageUrl, feel.imageWidth, feel.imageHeight);

            mTime.setText(feel.timeStr);
            mCopyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(ClipData.newPlainText("Label", feel.title));
                    Toast.makeText(getApplicationContext(), "复制成功", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class FeelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<FeelSuite> mFeelList;

        public FeelAdapter(List<FeelSuite> feelSuites) {
            mFeelList = feelSuites;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(FeelActivity.this);
            View v = inflater.inflate(R.layout.list_item_feel, parent, false);
            return new FeelHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            FeelSuite feel = mFeelList.get(position);
            ((FeelHolder)holder).bindFeel(feel);
        }

        @Override
        public int getItemCount() {
            return mFeelList.size();
        }


    }
}
