package com.mqfcu7.jiangmeilan.avatar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class CategoryNavigateLayout extends LinearLayout {
    private final static int TITLE_SIZE = 50;

    private String mTitle;
    RequestBuilder<Drawable> mImageBuilder;
    private ImageView mImageView;
    private TextView mTextView;

    public CategoryNavigateLayout(Context context) {this(context, null);}

    public CategoryNavigateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CategoryNavigateLayout);
        int imageResourceId = a.getResourceId(R.styleable.CategoryNavigateLayout_image, 0);
        mTitle = a.getString(R.styleable.CategoryNavigateLayout_text);
        mImageBuilder = Glide.with(getContext()).load(getResources().getDrawable(imageResourceId));

        mImageView = new ImageView(getContext());
        mTextView = new TextView(getContext());
        mTextView.setText(mTitle);
        mTextView.setTextColor(0xFF000000);
        mTextView.setTextSize(13);
        mTextView.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        addView(mImageView, layoutParams);
        addView(mTextView, layoutParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width + (int)mTextView.getTextSize() * 2 + 10;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!changed) return;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mImageBuilder.apply(new RequestOptions().override(w, w))
                .apply(new RequestOptions().circleCrop())
                .into(mImageView);
        mImageView.layout(0, 0, w, w);
        mTextView.layout(0, w + 10, w, w + (int)mTextView.getTextSize() * 2 + 10);
    }
}
