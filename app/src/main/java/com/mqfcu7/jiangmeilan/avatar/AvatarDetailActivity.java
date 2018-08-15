package com.mqfcu7.jiangmeilan.avatar;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.mqfcu7.jiangmeilan.avatar.databinding.ActivityAvatarDetailBinding;
import com.umeng.analytics.MobclickAgent;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;

public class AvatarDetailActivity extends AppCompatActivity {
    private static final String EXTRA_AVATAR_URL =
            "com.mqfcu7.jiangmeilan.avatar.avatar_url";

    private ActivityAvatarDetailBinding mBinding;
    private String mImageUrl;

    private ViewGroup bannerContainer;
    BannerView bv;
    String posId;

    public static Intent newIntent(Context context, String imageUrl) {
        Intent intent = new Intent(context, AvatarDetailActivity.class);
        intent.putExtra(EXTRA_AVATAR_URL, imageUrl);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_avatar_detail);
        Utils.setStatusBarLightMode(this, getWindow(), true);

        Glide.get(getApplicationContext()).clearMemory();

        mImageUrl = (String) getIntent().getSerializableExtra(EXTRA_AVATAR_URL);

        initBackBanner();
        initforwardBanner();
        initImageViews();

        initAd();
    }

    private void initBackBanner() {
        mBinding.avatarDetailBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initforwardBanner() {
        mBinding.avatarDetailEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = BuildAvatarActivity.newIntent(AvatarDetailActivity.this, mImageUrl);
                startActivity(intent);
            }
        });
    }

    private void initImageViews() {
        mBinding.avatarDetailBigImageView.post(new Runnable() {
            @Override
            public void run() {
                ImageView v = mBinding.avatarDetailBigImageView;
                Glide.with(AvatarDetailActivity.this)
                        .load(mImageUrl)
                        .apply(new RequestOptions().override(v.getWidth(), v.getWidth()))
                        .into(v);
            }
        });
        mBinding.avatarDetailBigImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = BuildAvatarActivity.newIntent(AvatarDetailActivity.this, mImageUrl);
                startActivity(intent);
            }
        });

        mBinding.avatarDetailRoundImageView.post(new Runnable() {
            @Override
            public void run() {
                ImageView v = mBinding.avatarDetailRoundImageView;
                Glide.with(AvatarDetailActivity.this)
                        .load(mImageUrl)
                        .apply(new RequestOptions().override(v.getWidth()))
                        .apply(new RequestOptions().bitmapTransform(new RoundedCorners(v.getWidth() / 5)))
                        .into(v);
            }
        });
        mBinding.avatarDetailRoundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = BuildAvatarActivity.newIntent(AvatarDetailActivity.this, mImageUrl);
                startActivity(intent);
            }
        });

        mBinding.avatarDetailCircleImageView.post(new Runnable() {
            @Override
            public void run() {
                ImageView v = mBinding.avatarDetailCircleImageView;
                Glide.with(AvatarDetailActivity.this)
                        .load(mImageUrl)
                        .apply(new RequestOptions().override(v.getWidth()))
                        .apply(new RequestOptions().circleCrop())
                        .into(v);
            }
        });
        mBinding.avatarDetailCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = BuildAvatarActivity.newIntent(AvatarDetailActivity.this, mImageUrl);
                startActivity(intent);
            }
        });
    }

    private void initAd() {
        bannerContainer = mBinding.avatarDetailBannerContainer;
        getBanner().loadAD();
        bannerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBanner().loadAD();
            }
        });
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
        posId = Constants.DetailBannerPosID;
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
