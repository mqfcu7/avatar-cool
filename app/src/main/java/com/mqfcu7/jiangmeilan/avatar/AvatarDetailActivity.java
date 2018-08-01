package com.mqfcu7.jiangmeilan.avatar;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.mqfcu7.jiangmeilan.avatar.databinding.ActivityAvatarDetailBinding;

public class AvatarDetailActivity extends AppCompatActivity {
    private static final String EXTRA_AVATAR_URL =
            "com.mqfcu7.jiangmeilan.avatar.avatar_url";

    private ActivityAvatarDetailBinding mBinding;
    private String mImageUrl;

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
        initImageViews();
    }

    private void initBackBanner() {
        mBinding.avatarDetailBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
    }
}
