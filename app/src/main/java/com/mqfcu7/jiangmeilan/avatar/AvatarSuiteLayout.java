package com.mqfcu7.jiangmeilan.avatar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AvatarSuiteLayout extends LinearLayout {
    private static final int PADDING_IMAGE = 5;
    private static final int TITLE_HEIGHT = 70;

    private AvatarSuite mAvatarSuite;
    private Random mRandom = new Random();

    private int mWidth;
    private int mHeight;
    private int mImageNum = 0;

    private TextView mTitleView;
    private List<ImageView> mImageViews = new ArrayList<>();

    public AvatarSuiteLayout(Context context) {this(context, null);}

    public AvatarSuiteLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public void setAvatarSuite(final AvatarSuite suite) {
        mAvatarSuite = suite;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);

        if (mImageNum == 0) {
            mImageNum = Math.min(mRandom.nextInt(7) + 4, mAvatarSuite.images_url.size());
            // TODO
            mImageNum = 4;
            try {
                Utils.invokeMethod(this, "calcMeasure" + mImageNum, null);
            } catch (Exception e) {
                Log.w("TAG", e.toString());
            }
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!changed) return;
        setTitleView();
    }

    private void setTitleView() {
        if (mTitleView != null) return;
        mTitleView = new TextView(getContext());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        mTitleView.setText(mAvatarSuite.title);
        mTitleView.setGravity(Gravity.CENTER);

        mTitleView.layout(0, 0, mWidth, TITLE_HEIGHT);
        addView(mTitleView, layoutParams);
    }

    private void calcMeasure4() {
        int width = (mWidth - PADDING_IMAGE * (mImageNum - 1)) / mImageNum;
        mHeight = width + TITLE_HEIGHT;

        if (mImageViews.isEmpty()) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            int offx = 0;
            int offy = TITLE_HEIGHT;
            for (int i = 0; i < mImageNum; ++i) {
                ImageView v = new ImageView(getContext());
                v.setPadding(PADDING_IMAGE / 2, 0, PADDING_IMAGE / 2, 0);
                Glide.with(getContext())
                        .load(mAvatarSuite.images_url.get(i))
                        .apply(new RequestOptions().override(width, width))
                        .into(v);
                v.layout(offx, offy, offx + width, offy + width);
                addView(v, layoutParams);
                mImageViews.add(v);
                offx += width + PADDING_IMAGE;
            }
        }
    }

    private void layoutViews4() {
        int width = (mWidth - PADDING_IMAGE * (mImageNum - 1)) / mImageNum;;

        if (mImageViews.isEmpty()) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            int offx = 0;
            int offy = mTitleView.getHeight();
            for (int i = 0; i < mImageNum; ++i) {
                ImageView v = new ImageView(getContext());
                v.setPadding(PADDING_IMAGE / 2, 0, PADDING_IMAGE / 2, 0);
                Glide.with(getContext())
                        .load(mAvatarSuite.images_url.get(i))
                        .apply(new RequestOptions().override(width, width))
                        .into(v);
                v.layout(offx, offy, offx + width, offy + width);
                addView(v, layoutParams);
                mImageViews.add(v);
                offx += width + PADDING_IMAGE;
            }
        }
    }

    private void calcMeasure5() {

    }
}
