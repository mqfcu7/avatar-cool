package com.mqfcu7.jiangmeilan.avatar;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mqfcu7.jiangmeilan.avatar.databinding.ActivityBuildAvatarBinding;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class BuildAvatarActivity extends AppCompatActivity {
    private static final String EXTRA_AVATAR_URL =
            "com.mqfcu7.jiangmeilan.avatar.avatar_url";
    private static final String TMP_IMAGE_PATH = "TMP_IMAGE_NAME.jpg";

    private ActivityBuildAvatarBinding mBinding;
    private String mImageUrl;

    public static Intent newIntent(Context context, String imageUrl) {
        Intent intent = new Intent(context, BuildAvatarActivity.class);
        intent.putExtra(EXTRA_AVATAR_URL, imageUrl);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_build_avatar);
        Utils.setStatusBarLightMode(this, getWindow(), true);

        Glide.get(getApplicationContext()).clearMemory();

        mImageUrl = (String) getIntent().getSerializableExtra(EXTRA_AVATAR_URL);
        Glide.with(this).asBitmap().load(mImageUrl).into(new SimpleTarget<Bitmap>(400, 400) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                mBinding.stickerView.setBaseImage(resource);
                mBinding.stickerView.postInvalidate();
            }
        });

        initTitleBanner();
        mBinding.stickerItemView.setStickerView(mBinding.stickerView);

        mBinding.stickerView.setActivity(this);
    }

    private void initTitleBanner() {
        mBinding.buildAvatarBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBinding.buildAvatarSaveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.stickerView.onSave();
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
}
