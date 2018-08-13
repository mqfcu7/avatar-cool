package com.mqfcu7.jiangmeilan.avatar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class FeelImageLayout extends LinearLayout {

    private String mUrl;
    private int mWidth;
    private int mHeight;
    private ImageView mImageView;

    private int mW;
    private int mH;

    public FeelImageLayout(Context context) { this(context, null); }

    public FeelImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public void setImage(String url, int width, int height) {
        mUrl = url;
        mWidth = width;
        mHeight = height;

        if (mImageView != null) {
            Glide.with(getContext())
                    .load(mUrl)
                    .apply(new RequestOptions().override(mWidth, mHeight))
                    .into(mImageView);
            mH = mW * mHeight / mWidth;
            setMeasuredDimension(mW, mH);
            mImageView.layout(0, 0, mW, mH);
            mImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = AvatarDetailActivity.newIntent(getContext(), mUrl);
                    getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * mHeight / mWidth;
        setMeasuredDimension(width, height);
        mW = width;
        mH = height;

        if (mImageView == null) {
            mImageView = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            Glide.with(getContext())
                    .load(mUrl)
                    .apply(new RequestOptions().override(mWidth, mHeight))
                    .into(mImageView);
            mImageView.layout(0, 0, width, height);
            addView(mImageView, layoutParams);
            mImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = AvatarDetailActivity.newIntent(getContext(), mUrl);
                    getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }
}
