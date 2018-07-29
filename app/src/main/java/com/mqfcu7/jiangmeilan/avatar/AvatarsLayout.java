package com.mqfcu7.jiangmeilan.avatar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class AvatarsLayout extends LinearLayout {
    public static final int IMAGE_NUM = 3;
    private static final int IMAGE_PADDING = 5;

    private int mWidth;
    private int mHeight;
    private String[] mUrls = new String[IMAGE_NUM];
    private List<ImageView> mImageViews = new ArrayList<>();

    public AvatarsLayout(Context context) {this(context, null);}

    public AvatarsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAvatars(String[] urls) {
        for (int i = 0; i < IMAGE_NUM; ++ i) {
            mUrls[i] = urls[i];
        }
        if (!mImageViews.isEmpty()) {
            for (int i = 0; i < IMAGE_NUM; ++ i) {
                int width = (mWidth - IMAGE_PADDING * 5) / IMAGE_NUM;
                Glide.with(getContext())
                        .load(mUrls[i])
                        .apply(new RequestOptions().override(width, width))
                        .into(mImageViews.get(i));
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (mImageViews.isEmpty()) {
            calcMeasure();
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mImageViews.isEmpty()) {
            calcMeasure();
        }
    }

    private void calcMeasure() {
        int width = (mWidth - IMAGE_PADDING * 5) / IMAGE_NUM;
        mHeight = width;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int offx = IMAGE_PADDING;
        for (int i = 0; i < IMAGE_NUM; ++ i) {
            ImageView v = new ImageView(getContext());
            v.setPadding(IMAGE_PADDING / 2, 0, IMAGE_PADDING / 2, 0);
            Glide.with(getContext())
                    .load(mUrls[i])
                    .apply(new RequestOptions().override(width, width))
                    .into(v);
            v.layout(offx, 0, offx + width, width);
            addView(v, layoutParams);
            mImageViews.add(v);
            offx += width + IMAGE_PADDING;
        }
    }
}
