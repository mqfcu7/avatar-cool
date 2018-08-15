package com.mqfcu7.jiangmeilan.avatar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.mqfcu7.jiangmeilan.avatar.databinding.ActivityMainBinding;
import com.umeng.analytics.MobclickAgent;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MAX_HOT_AVATAR_PAGE_NUM = 5;
    ActivityMainBinding mBinding;

    private AvatarSuiteLayout mAvatarSuiteLayout;

    private AvatarSuiteAdapter mHotAvatarAdapter;
    private RecyclerView mHotAvatarRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;
    private NestedScrollView mNestedScrollView;

    private AvatarSuiteGenerator mAvatarSuiteGenerator;

    private int mHotPageNum;
    private String mUA;
    private CrawlerFeelSuite mCrawlerFeelSuite = new CrawlerFeelSuite();
    private List<FeelSuite> mFeelSuites;
    private Database mDatabase;

    private ViewGroup bannerContainer;
    BannerView bv;
    String posId;

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
        Utils.setStatusBarLightMode(this, getWindow(), true);
        mDatabase = new Database(getApplicationContext());
        mUA = Utils.getUserAgent(getApplicationContext());
        CrawlerThread.setUA(mUA);

        Glide.get(getApplicationContext()).clearMemory();
        mAvatarSuiteGenerator = new AvatarSuiteGenerator(getApplicationContext());

        initFloatingButton();
        initDailyFeel();
        initCategoryNavigateLayout();
        initDailyAvatar();
        initHotAvatar();

        initAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initFloatingButton() {
        mBinding.fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(MainActivity.this)
                        .openGallery(PictureMimeType.ofImage())
                        .maxSelectNum(1)
                        .selectionMode(PictureConfig.SINGLE)
                        .isCamera(true)
                        .imageSpanCount(3)
                        .compress(true)
                        .forResult(PictureConfig.CHOOSE_REQUEST);

            }
        });
    }

    private void initCategoryNavigateLayout() {
        mBinding.cateoryListInclude.mainCategoryGirlLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AvatarListActivity.newIntent(getApplicationContext(), Database.AvatarType.GIRL, "小姐姐");
                startActivity(intent);
            }
        });
        mBinding.cateoryListInclude.mainCategoryBoyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AvatarListActivity.newIntent(getApplicationContext(), Database.AvatarType.BOY, "小哥哥");
                startActivity(intent);
            }
        });
        mBinding.cateoryListInclude.mainCategoryLovesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AvatarListActivity.newIntent(getApplicationContext(), Database.AvatarType.LOVES, "情侣");
                startActivity(intent);
            }
        });
        mBinding.cateoryListInclude.mainCategoryFriendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AvatarListActivity.newIntent(getApplicationContext(), Database.AvatarType.FRIEND, "闺蜜");
                startActivity(intent);
            }
        });
        mBinding.cateoryListInclude.mainCategoryPetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AvatarListActivity.newIntent(getApplicationContext(), Database.AvatarType.PET, "宠物");
                startActivity(intent);
            }
        });
        mBinding.cateoryListInclude.mainCategoryComicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AvatarListActivity.newIntent(getApplicationContext(), Database.AvatarType.COMIC, "动漫");
                startActivity(intent);
            }
        });
        mBinding.cateoryListInclude.mainCategoryGameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AvatarListActivity.newIntent(getApplicationContext(), Database.AvatarType.GAME, "游戏");
                startActivity(intent);
            }
        });
        mBinding.cateoryListInclude.mainCategorySceneryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AvatarListActivity.newIntent(getApplicationContext(), Database.AvatarType.SCENERY, "风景");
                startActivity(intent);
            }
        });
    }

    private void initDailyFeel() {
        final FeelSuite feel = mDatabase.getFeelSuite();
        Glide.with(getApplicationContext())
                .load(feel.userUrl)
                .apply(new RequestOptions().circleCrop())
                .into(mBinding.dailyFeelInclude.mainDailyFeelImageView);
        mBinding.dailyFeelInclude.mainDailyFeelNameText.setText(feel.userName);
        mBinding.dailyFeelInclude.mainDailyFeelTitleText.setText(feel.title);
        mBinding.dailyFeelInclude.mainDailyFeelTimeText.setText(feel.timeStr);
        mBinding.dailyFeelInclude.mainDailyFeelCopyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(ClipData.newPlainText("Label", feel.title));
                    Toast.makeText(getApplicationContext(), "复制成功", Toast.LENGTH_SHORT).show();
                }
        });

        mBinding.dailyFeelInclude.mainDailyFeelTitleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FeelActivity.class);
                startActivity(intent);
            }
        });
        mBinding.dailyFeelInclude.mainDailyFeelMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FeelActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initDailyAvatar() {
        mAvatarSuiteLayout = mBinding.dailyAvatarInclude.mainAvatarSuiteLayout;
        mAvatarSuiteLayout.setAvatarSuite(mAvatarSuiteGenerator.randomAvatarSuite());
        mAvatarSuiteLayout.setPaddingVertical(5);
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

                                initDailyFeel();
                                mHotAvatarAdapter.pushItems(mAvatarSuiteGenerator.getUpdateAvatarSuites(5));
                                mHotAvatarAdapter.notifyItemRangeChanged(0, 5);
                                getBanner().loadAD();
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

        mNestedScrollView = mBinding.mainNestedScrollView;
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (mHotPageNum > MAX_HOT_AVATAR_PAGE_NUM) {
                        return;
                    }
                    int pos = mHotAvatarAdapter.getItemCount();
                    mHotAvatarAdapter.appendItems(mAvatarSuiteGenerator.getUpdateAvatarSuites(5));
                    mHotAvatarAdapter.notifyItemRangeChanged(pos, 5);
                    mHotPageNum ++;
                }
            }
        });
    }

    private void initAd() {
        bannerContainer = mBinding.mainBannerContainer;
        getBanner().loadAD();
        bannerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBanner().loadAD();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if (!selectList.isEmpty()) {
                        Intent intent = AvatarDetailActivity.newIntent(
                                getApplicationContext(), selectList.get(0).getCompressPath());
                        startActivity(intent);
                    }
                    break;
            }
        }
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

    private BannerView getBanner() {
        posId = Constants.BannerPosID;
        if(bv != null){
            bannerContainer.removeView(bv);
            bv.destroy();
        }
        bv = new BannerView(this, ADSize.BANNER, Constants.APPID, posId);
        // 注意：如果开发者的banner不是始终展示在屏幕中的话，请关闭自动刷新，否则将导致曝光率过低。
        // 并且应该自行处理：当banner广告区域出现在屏幕后，再手动loadAD。
        bv.setRefresh(30);
        bv.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(AdError error) {
                Log.d(
                        "TAG",
                        String.format("Banner onNoAD，eCode = %d, eMsg = %s", error.getErrorCode(),
                                error.getErrorMsg()));
            }

            @Override
            public void onADReceiv() {
            }
        });
        bannerContainer.addView(bv);
        return bv;
    }
}
