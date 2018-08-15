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

import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.pi.AdData;
import com.qq.e.comm.util.AdError;
import com.qq.e.comm.util.GDTLogger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.TreeSet;

public class FeelActivity extends AppCompatActivity implements NativeExpressAD.NativeExpressADListener {
    public static final int AD_COUNT = 5;    // 加载广告的条数，取值范围为[1, 10]
    public static int FIRST_AD_POSITION = 3; // 第一条广告的位置
    public static int ITEMS_PER_AD = 4;     // 每间隔10个条目插入一条广告

    private ActivityFeelBinding mBinding;
    private RecyclerView mRecyclerView;
    private FeelAdapter mFeelAdapter;
    private RecyclerAdapterWithHF mAdapter;
    private PtrClassicFrameLayout mFrameLayout;
    private CrawlerFeelSuite mCrawlerFeelSuite = new CrawlerFeelSuite();
    private NativeExpressAD mADManager;
    private List<NativeExpressADView> mAdViewList;

    private List<Object> mFeelList = new LinkedList<>();
    private Handler mHandler = new Handler();
    private HashMap<NativeExpressADView, Integer> mAdViewPositionMap = new HashMap<NativeExpressADView, Integer>();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAdViewList != null) {
            for (NativeExpressADView view : mAdViewList) {
                view.destroy();
            }
        }
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
        mFeelAdapter = new FeelAdapter(mFeelList);
        mAdapter = new RecyclerAdapterWithHF(mFeelAdapter);
        mRecyclerView.setAdapter(mAdapter);
        initNativeExpressAD();
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
            ((LinkedList<Object>) mFeelList).addFirst(feel);
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
            ((LinkedList<Object>) mFeelList).addLast(feel);
            cnt ++;
        }
        return cnt;
    }

    private Set<Integer> getFeelIdSet() {
        Set<Integer> idSet = new TreeSet<>();
        for (Object feel : mFeelList) {
            if (feel instanceof FeelSuite) {
                idSet.add(((FeelSuite)feel).id);
            }
        }
        return idSet;
    }

    private void initNativeExpressAD() {
        ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT);
        mADManager = new NativeExpressAD(FeelActivity.this, adSize, Constants.APPID, Constants.NativeExpressPosID, this);
        mADManager.loadAD(AD_COUNT);
    }

    @Override
    public void onNoAD(AdError adError) {
        Log.d(
                "TAG",
                String.format("onNoAD, error code: %d, error msg: %s", adError.getErrorCode(),
                        adError.getErrorMsg()));
    }

    @Override
    public void onADLoaded(List<NativeExpressADView> adList) {
        mAdViewList = adList;
        for (int i = 0; i < mAdViewList.size(); i++) {
            int position = FIRST_AD_POSITION + ITEMS_PER_AD * i;
            if (position < mFeelList.size()) {
                NativeExpressADView view = mAdViewList.get(i);
                if (view.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                    view.setMediaListener(mediaListener);
                }
                mAdViewPositionMap.put(view, position); // 把每个广告在列表中位置记录下来
                mFeelAdapter.addADViewToPosition(position, mAdViewList.get(i));
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRenderFail(NativeExpressADView adView) {
    }

    @Override
    public void onRenderSuccess(NativeExpressADView adView) {
    }

    @Override
    public void onADExposure(NativeExpressADView adView) {
    }

    @Override
    public void onADClicked(NativeExpressADView adView) {
    }

    @Override
    public void onADClosed(NativeExpressADView adView) {
        if (mFeelAdapter != null) {
            int removedPosition = mAdViewPositionMap.get(adView);
            mFeelAdapter.removeADView(removedPosition, adView);
        }
    }

    @Override
    public void onADLeftApplication(NativeExpressADView adView) {
    }

    @Override
    public void onADOpenOverlay(NativeExpressADView adView) {
    }

    @Override
    public void onADCloseOverlay(NativeExpressADView adView) {
    }

    private class FeelHolder extends RecyclerView.ViewHolder {
        private View mView;
        private ImageView mUserImage;
        private TextView mUserName;
        private TextView mTitle;
        private FeelImageLayout mImage;
        private TextView mTime;
        private LinearLayout mCopyLayout;
        public ViewGroup container;

        public FeelHolder(View v) {
            super(v);

            mView = v;
            mUserImage = (ImageView)v.findViewById(R.id.item_feel_user_image);
            mUserName = (TextView)v.findViewById(R.id.item_feel_user_name_text);
            mTitle = (TextView)v.findViewById(R.id.item_feel_title_text);
            mImage = (FeelImageLayout) v.findViewById(R.id.item_feel_image);
            mTime = (TextView)v.findViewById(R.id.item_feel_time_text);
            mCopyLayout = (LinearLayout)v.findViewById(R.id.item_feel_copy_layout);
            container = (ViewGroup) v.findViewById(R.id.express_ad_container);
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
        static final int TYPE_DATA = 0;
        static final int TYPE_AD = 1;

        private List<Object> mFeelList;

        public FeelAdapter(List<Object> feelSuites) {
            mFeelList = feelSuites;
        }

        public void addADViewToPosition(int position, NativeExpressADView adView) {
            if (position >= 0 && position < mFeelList.size() && adView != null) {
                mFeelList.add(position, adView);
            }
        }

        public void removeADView(int position, NativeExpressADView adView) {
            mFeelList.remove(position);
            mAdapter.notifyItemRemoved(position); // position为adView在当前列表中的位置
            mAdapter.notifyItemRangeChanged(0, mFeelList.size() - 1);
        }

        @Override
        public int getItemViewType(int position) {
            return mFeelList.get(position) instanceof NativeExpressADView ? TYPE_AD : TYPE_DATA;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layoutId = (viewType == TYPE_AD) ? R.layout.list_item_express_ad : R.layout.list_item_feel;
            LayoutInflater inflater = LayoutInflater.from(FeelActivity.this);
            View v = inflater.inflate(layoutId, parent, false);
            return new FeelHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (TYPE_AD == type) {
                final NativeExpressADView adView = (NativeExpressADView) mFeelList.get(position);
                mAdViewPositionMap.put(adView, position); // 广告在列表中的位置是可以被更新的
                FeelHolder feelHolder = (FeelHolder) holder;
                if (feelHolder.container.getChildCount() > 0
                        && feelHolder.container.getChildAt(0) == adView) {
                    return;
                }

                if (feelHolder.container.getChildCount() > 0) {
                    feelHolder.container.removeAllViews();
                }

                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                feelHolder.container.addView(adView);
                adView.render(); // 调用render方法后sdk才会开始展示广告
            } else {
                FeelSuite feel = (FeelSuite) mFeelList.get(position);
                ((FeelHolder) holder).bindFeel(feel);
            }
        }

        @Override
        public int getItemCount() {
            return mFeelList.size();
        }
    }

    private String getAdInfo(NativeExpressADView nativeExpressADView) {
        AdData adData = nativeExpressADView.getBoundData();
        if (adData != null) {
            StringBuilder infoBuilder = new StringBuilder();
            infoBuilder.append("title:").append(adData.getTitle()).append(",")
                    .append("desc:").append(adData.getDesc()).append(",")
                    .append("patternType:").append(adData.getAdPatternType());
            if (adData.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                infoBuilder.append(", video info: ")
                        .append(getVideoInfo(adData.getProperty(AdData.VideoPlayer.class)));
            }
            return infoBuilder.toString();
        }
        return null;
    }

    private String getVideoInfo(AdData.VideoPlayer videoPlayer) {
        if (videoPlayer != null) {
            StringBuilder videoBuilder = new StringBuilder();
            videoBuilder.append("state:").append(videoPlayer.getVideoState()).append(",")
                    .append("duration:").append(videoPlayer.getDuration()).append(",")
                    .append("position:").append(videoPlayer.getCurrentPosition());
            return videoBuilder.toString();
        }
        return null;
    }

    private NativeExpressMediaListener mediaListener = new NativeExpressMediaListener() {
        @Override
        public void onVideoInit(NativeExpressADView nativeExpressADView) {
        }

        @Override
        public void onVideoLoading(NativeExpressADView nativeExpressADView) {
        }

        @Override
        public void onVideoReady(NativeExpressADView nativeExpressADView, long l) {
        }

        @Override
        public void onVideoStart(NativeExpressADView nativeExpressADView) {
        }

        @Override
        public void onVideoPause(NativeExpressADView nativeExpressADView) {
        }

        @Override
        public void onVideoComplete(NativeExpressADView nativeExpressADView) {
        }

        @Override
        public void onVideoError(NativeExpressADView nativeExpressADView, AdError adError) {
        }

        @Override
        public void onVideoPageOpen(NativeExpressADView nativeExpressADView) {
        }

        @Override
        public void onVideoPageClose(NativeExpressADView nativeExpressADView) {
        }
    };
}
