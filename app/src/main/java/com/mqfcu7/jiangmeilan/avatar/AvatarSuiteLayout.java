package com.mqfcu7.jiangmeilan.avatar;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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
    private static final int TITLE_HEIGHT = 120;

    private AvatarSuite mAvatarSuite;
    private Random mRandom = new Random();

    private int mWidth;
    private int mHeight;

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

        if (mImageViews.isEmpty()) {
            int imageNum = Math.min(mRandom.nextInt(7) + 4, mAvatarSuite.images_url.size());
            Log.d("TAG", "title: " + mAvatarSuite.title);
            try {
                Utils.invokeMethod(this, "calcMeasure" + imageNum, null);
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

    public void onReset() {
        Log.d("TAG", "onReset");

        removeAllViews();
        mImageViews.clear();
    }

    private void setTitleView() {
        if (mTitleView != null) return;
        mTitleView = new TextView(getContext());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        mTitleView.setText(mAvatarSuite.title);
        mTitleView.setGravity(Gravity.CENTER);

        mTitleView.layout(0, 40, mWidth, TITLE_HEIGHT);
        addView(mTitleView, layoutParams);
    }

    private void calcMeasure4() {
        int width = (mWidth - PADDING_IMAGE * 5) / 4;
        mHeight = width + TITLE_HEIGHT;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int offx = PADDING_IMAGE;
        int offy = TITLE_HEIGHT;
        for (int i = 0; i < 4; ++i) {
            final String url = mAvatarSuite.images_url.get(i);
            ImageView v = new ImageView(getContext());
            v.setPadding(PADDING_IMAGE / 2, 0, PADDING_IMAGE / 2, 0);
            Glide.with(getContext())
                    .load(url)
                    .apply(new RequestOptions().override(width, width))
                    .into(v);
            v.layout(offx, offy, offx + width, offy + width);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = AvatarDetailActivity.newIntent(getContext(), url);
                    getContext().startActivity(intent);
                }
            });
            addView(v, layoutParams);
            mImageViews.add(v);
            offx += width + PADDING_IMAGE;
        }
    }

    private void calcMeasure5() {
        int width = (mWidth - PADDING_IMAGE * 5) / 4;
        mHeight = width * 2 + TITLE_HEIGHT + PADDING_IMAGE;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        {
            final String url = mAvatarSuite.images_url.get(0);
            ImageView v = new ImageView(getContext());
            v.setPadding(PADDING_IMAGE / 2, 0, PADDING_IMAGE / 2, 0);
            Glide.with(getContext())
                    .load(url)
                    .apply(new RequestOptions().override(width * 2 + PADDING_IMAGE, width * 2 + PADDING_IMAGE))
                    .into(v);
            v.layout(PADDING_IMAGE, TITLE_HEIGHT, width * 2 + PADDING_IMAGE * 2, TITLE_HEIGHT + width * 2 + PADDING_IMAGE);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = AvatarDetailActivity.newIntent(getContext(), url);
                    getContext().startActivity(intent);
                }
            });
            addView(v, layoutParams);
            mImageViews.add(v);
        }
        int offx = width * 2 + PADDING_IMAGE * 3;
        int offy = TITLE_HEIGHT;
        for (int i = 0; i < 4; ++ i) {
            final String url = mAvatarSuite.images_url.get(i + 1);
            ImageView v = new ImageView(getContext());
            v.setPadding(PADDING_IMAGE / 2, 0, PADDING_IMAGE / 2, 0);
            Glide.with(getContext())
                    .load(url)
                    .apply(new RequestOptions().override(width, width))
                    .into(v);
            v.layout(offx, offy, offx + width, offy + width);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = AvatarDetailActivity.newIntent(getContext(), url);
                    getContext().startActivity(intent);
                }
            });
            addView(v, layoutParams);
            if (i == 1) {
                offx = width * 2 + PADDING_IMAGE * 3;
                offy += width + PADDING_IMAGE;
            } else {
                offx += width + PADDING_IMAGE;
            }
            mImageViews.add(v);
        }
    }

    private void calcMeasure6() {
        int width = (mWidth - PADDING_IMAGE * 4) / 3;
        mHeight = width * 2 + PADDING_IMAGE + TITLE_HEIGHT;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int offx = PADDING_IMAGE;
        int offy = TITLE_HEIGHT;
        for (int i = 0; i < 6; ++ i) {
            final String url = mAvatarSuite.images_url.get(i);
            ImageView v = new ImageView(getContext());
            v.setPadding(PADDING_IMAGE / 2, 0, PADDING_IMAGE / 2, 0);
            Glide.with(getContext())
                    .load(url)
                    .apply(new RequestOptions().override(width, width))
                    .into(v);
            v.layout(offx, offy, offx + width, offy + width);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = AvatarDetailActivity.newIntent(getContext(), url);
                    getContext().startActivity(intent);
                }
            });
            addView(v, layoutParams);
            if (i == 2) {
                offx = PADDING_IMAGE;
                offy += width + PADDING_IMAGE;
            } else {
                offx += width + PADDING_IMAGE;
            }
            mImageViews.add(v);
        }
    }

    private void calcMeasure7() {
        int width = (mWidth - PADDING_IMAGE * 6) / 5;
        mHeight = width * 2 + PADDING_IMAGE + TITLE_HEIGHT;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        {
            final String url = mAvatarSuite.images_url.get(0);
            ImageView v = new ImageView(getContext());
            v.setPadding(PADDING_IMAGE / 2, 0, PADDING_IMAGE / 2, 0);
            Glide.with(getContext())
                    .load(url)
                    .apply(new RequestOptions().override(width * 2 + PADDING_IMAGE, width * 2 + PADDING_IMAGE))
                    .into(v);
            v.layout(PADDING_IMAGE, TITLE_HEIGHT, width * 2 + PADDING_IMAGE * 2, TITLE_HEIGHT + width * 2 + PADDING_IMAGE);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = AvatarDetailActivity.newIntent(getContext(), url);
                    getContext().startActivity(intent);
                }
            });
            addView(v, layoutParams);
            mImageViews.add(v);
        }
        int offx = width * 2 + PADDING_IMAGE * 3;
        int offy = TITLE_HEIGHT;
        for (int i = 0; i < 6; ++ i) {
            final String url = mAvatarSuite.images_url.get(i + 1);
            ImageView v = new ImageView(getContext());
            v.setPadding(PADDING_IMAGE / 2, 0, PADDING_IMAGE / 2, 0);
            Glide.with(getContext())
                    .load(url)
                    .apply(new RequestOptions().override(width, width))
                    .into(v);
            v.layout(offx, offy, offx + width, offy + width);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = AvatarDetailActivity.newIntent(getContext(), url);
                    getContext().startActivity(intent);
                }
            });
            addView(v, layoutParams);
            if (i == 2) {
                offx = width * 2 + PADDING_IMAGE * 3;
                offy += width + PADDING_IMAGE;
            } else {
                offx += width + PADDING_IMAGE;
            }
            mImageViews.add(v);
        }
    }

    private void calcMeasure8() {
        int width = (mWidth - PADDING_IMAGE * 5) / 4;
        mHeight = width * 2 + PADDING_IMAGE + TITLE_HEIGHT;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int offx = PADDING_IMAGE;
        int offy = TITLE_HEIGHT;
        for (int i = 0; i < 8; ++ i) {
            final String url = mAvatarSuite.images_url.get(i);
            ImageView v = new ImageView(getContext());
            v.setPadding(PADDING_IMAGE / 2, 0, PADDING_IMAGE / 2, 0);
            Glide.with(getContext())
                    .load(url)
                    .apply(new RequestOptions().override(width, width))
                    .into(v);
            v.layout(offx, offy, offx + width, offy + width);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = AvatarDetailActivity.newIntent(getContext(), url);
                    getContext().startActivity(intent);
                }
            });
            addView(v, layoutParams);
            if (i == 3) {
                offx = PADDING_IMAGE;
                offy += width + PADDING_IMAGE;
            } else {
                offx += width + PADDING_IMAGE;
            }
            mImageViews.add(v);
        }
    }

    private void calcMeasure9() {
        int width = (mWidth - PADDING_IMAGE * 5) / 4;
        mHeight = width * 3 + PADDING_IMAGE * 2 + TITLE_HEIGHT;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        {
            final String url = mAvatarSuite.images_url.get(0);
            ImageView v = new ImageView(getContext());
            v.setPadding(PADDING_IMAGE / 2, 0, PADDING_IMAGE / 2, 0);
            Glide.with(getContext())
                    .load(url)
                    .apply(new RequestOptions().override(width * 2 + PADDING_IMAGE, width * 2 + PADDING_IMAGE))
                    .into(v);
            v.layout(PADDING_IMAGE, TITLE_HEIGHT, width * 2 + PADDING_IMAGE * 2, TITLE_HEIGHT + width * 2 + PADDING_IMAGE);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = AvatarDetailActivity.newIntent(getContext(), url);
                    getContext().startActivity(intent);
                }
            });
            addView(v, layoutParams);
            mImageViews.add(v);
        }
        {
            int offx = width * 2 + PADDING_IMAGE * 3;
            int offy = TITLE_HEIGHT;
            for (int i = 0; i < 4; ++ i) {
                final String url = mAvatarSuite.images_url.get(i + 1);
                ImageView v = new ImageView(getContext());
                v.setPadding(PADDING_IMAGE / 2, 0, PADDING_IMAGE / 2, 0);
                Glide.with(getContext())
                        .load(url)
                        .apply(new RequestOptions().override(width, width))
                        .into(v);
                v.layout(offx, offy, offx + width, offy + width);
                v.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = AvatarDetailActivity.newIntent(getContext(), url);
                        getContext().startActivity(intent);
                    }
                });
                addView(v, layoutParams);
                if (i == 1) {
                    offx = width * 2 + PADDING_IMAGE * 3;
                    offy += width + PADDING_IMAGE;
                } else {
                    offx += width + PADDING_IMAGE;
                }
                mImageViews.add(v);
            }
        }
        int offx = PADDING_IMAGE;
        int offy = width * 2 + PADDING_IMAGE + TITLE_HEIGHT;
        for (int i = 0; i < 4; ++ i) {
            final String url = mAvatarSuite.images_url.get(i + 5);
            ImageView v = new ImageView(getContext());
            v.setPadding(PADDING_IMAGE / 2, 0, PADDING_IMAGE / 2, 0);
            Glide.with(getContext())
                    .load(url)
                    .apply(new RequestOptions().override(width, width))
                    .into(v);
            v.layout(offx, offy, offx + width, offy + width);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = AvatarDetailActivity.newIntent(getContext(), url);
                    getContext().startActivity(intent);
                }
            });
            addView(v, layoutParams);
            offx += width + PADDING_IMAGE;
            mImageViews.add(v);
        }
    }
}
