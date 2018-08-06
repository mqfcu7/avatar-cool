package com.mqfcu7.jiangmeilan.avatar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class StickerItemView extends View {
    public static final int HORIZONTAL_IMAGE_NUM = 5;
    public static final int HORIZONTAL_PADDING = 70;
    public static final int VERTICAL_IMAGE_NUM = 3;
    public static final int VERTICAL_PADDING = 20;

    private int mImageWidth;
    private StickerView mStickerView;

    private int[] mStickerItems = new int[] {
            R.drawable.christmass_bell_sound,
            R.drawable.christmass_candy_stick_1,
            R.drawable.christmass_candy_stick_2,
            R.drawable.christmass_globe_ornament_decoration_2,
            R.drawable.christmass_present_bow,
            R.drawable.christmass_present_gift_box8,
            R.drawable.christmass_price_tag_xmas,
            R.drawable.christmass_santa_claus_hat_2,
            R.drawable.christmass_snowbulb,
            R.drawable.christmass_socks,
            R.drawable.christmass_star_1,
            R.drawable.christmass_star_2,
            R.drawable.christmass_star,
            R.drawable.christmass_tree_pine,
            R.drawable.christmass_wreath
    };
    private Rect[] mStickerItemsR = new Rect[mStickerItems.length];

    public StickerItemView(Context context) { this(context, null); }

    public StickerItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setStickerView(StickerView stickerView) {
        mStickerView = stickerView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);

        mImageWidth = (width - (HORIZONTAL_IMAGE_NUM + 1) * HORIZONTAL_PADDING) / HORIZONTAL_IMAGE_NUM;
        int height = mImageWidth * VERTICAL_IMAGE_NUM + (VERTICAL_IMAGE_NUM + 1) * VERTICAL_PADDING;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawStickerItems(canvas);
    }

    private void drawStickerItems(Canvas canvas) {
        int offy = VERTICAL_PADDING;
        for (int i = 0; i < VERTICAL_IMAGE_NUM; ++ i) {
            int offx = HORIZONTAL_PADDING;
            for (int j = 0; j < HORIZONTAL_IMAGE_NUM; ++ j) {
                Bitmap image = BitmapFactory.decodeResource(getResources(), mStickerItems[i*HORIZONTAL_IMAGE_NUM+j]);
                Rect r1 = new Rect(0, 0, image.getWidth(), image.getHeight());
                Rect r2 = new Rect(offx, offy, offx + mImageWidth, offy + mImageWidth);
                canvas.drawBitmap(image, r1, r2, null);
                offx += mImageWidth + HORIZONTAL_PADDING;
                mStickerItemsR[i*HORIZONTAL_IMAGE_NUM+j] = new Rect(r2.left, r2.top, r2.right, r2.bottom);
            }
            offy += mImageWidth + VERTICAL_PADDING;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF point = new PointF(event.getX(), event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                for (int i = 0; i < mStickerItemsR.length; ++ i) {
                    if (mStickerItemsR[i].contains((int)point.x, (int)point.y)) {
                        mStickerView.addStickerItem(BitmapFactory.decodeResource(getResources(), mStickerItems[i]));
                        break;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
