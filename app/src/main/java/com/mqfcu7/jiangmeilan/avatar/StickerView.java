package com.mqfcu7.jiangmeilan.avatar;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class StickerView extends View {
    private final static int STATUS_IDLE = 0;
    private final static int STATUS_MOVE = 1;
    private final static int STATUS_DELETE = 2;
    private final static int STATUS_ROTATE = 3;

    private String mImageUrl;
    private Bitmap mBaseImage;
    private Rect mImageR = new Rect();

    private int mCurStatus;
    private List<StickerItem> mStickerItems = new LinkedList<>();
    private StickerItem mCurSticker;
    private float mOldX;
    private float mOldY;
    private Point mPoint = new Point(0, 0);

    private StickerTask mTask;
    private Activity mActivity;

    public StickerView(Context context) { this(context, null); }

    public StickerView(Context context, AttributeSet attr) {
        super(context, attr);

        mCurStatus = STATUS_IDLE;
    }

    public void setImage(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public void setBaseImage(Bitmap baseImage) {
        mBaseImage = baseImage;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        initImageRect();
    }

    private void initImageRect() {
        int width = getWidth();
        int height = getHeight();
        if (width > height) {
            mImageR.left = (width - height) / 2;
            mImageR.top = 0;
            mImageR.right = mImageR.left + height;
            mImageR.bottom = height;
        } else {
            mImageR.left = 0;
            mImageR.top = (height - width) / 2;
            mImageR.right = width;
            mImageR.bottom = mImageR.top + width;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBaseImage(canvas);
        drawStickerItems(canvas);
    }

    private void drawBaseImage(Canvas canvas) {
        if (mBaseImage == null) {
            return;
        }

        canvas.drawBitmap(mBaseImage, new Rect(0, 0, mImageR.width(), mImageR.height()), mImageR, null);
    }

    private void drawStickerItems(Canvas canvas) {
        for (StickerItem sticker : mStickerItems) {
            sticker.draw(canvas);
        }
    }

    public void addStickerItem(Bitmap bitmap) {
        StickerItem sticker = new StickerItem(getContext());
        sticker.init(bitmap, this);
        if (mCurSticker != null) {
            mCurSticker.isDrawHelpTool = false;
        }
        mCurSticker = sticker;
        mStickerItems.add(sticker);

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                onActionMove(x, y);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mCurStatus = STATUS_IDLE;
                break;
        }
        return super.onTouchEvent(event);
    }

    private void onActionDown(float x, float y) {
        boolean ret = false;
        for (StickerItem sticker : mStickerItems) {
            if (sticker.detectDeleteRect.contains(x, y)) {
                mStickerItems.remove(sticker);
                mCurStatus = STATUS_IDLE;
                invalidate();
                return;
            } else if (sticker.detectRotateRect.contains(x, y)) {
                if (mCurSticker != null) {
                    mCurSticker.isDrawHelpTool = false;
                }
                mCurSticker = sticker;
                mCurSticker.isDrawHelpTool = true;
                mCurStatus = STATUS_ROTATE;
                mOldX = x;
                mOldY = y;
                ret = true;
            } else if (detectInItemContent(sticker, x, y)) {
                if (mCurSticker != null) {
                    mCurSticker.isDrawHelpTool = false;
                }
                mCurSticker = sticker;
                mCurSticker.isDrawHelpTool = true;
                mCurStatus = STATUS_MOVE;
                mOldX = x;
                mOldY = y;
                ret = true;
            }
        }

        if (!ret && mCurSticker != null && mCurStatus == STATUS_IDLE) {
            mCurSticker.isDrawHelpTool = false;
            mCurSticker = null;
        }
        invalidate();
    }

    private void onActionMove(float x, float y) {
        if (mCurStatus == STATUS_MOVE) {
            float dx = x - mOldX;
            float dy = y - mOldY;
            if (mCurSticker != null) {
                mCurSticker.updatePos(dx, dy);
                invalidate();
            }
            mOldX = x;
            mOldY = y;
        } else if (mCurStatus == STATUS_ROTATE) {
            float dx = x - mOldX;
            float dy = y - mOldY;
            if (mCurSticker != null) {
                mCurSticker.updateRotateAndScale(mOldX, mOldY, dx, dy);
                invalidate();
            }
            mOldX = x;
            mOldY = y;
        }
    }

    private boolean detectInItemContent(StickerItem sticker, float x, float y) {
        mPoint.set((int)x, (int)y);
        RectUtil.rotatePoint(mPoint, sticker.helpBox.centerX(), sticker.helpBox.centerY(), -sticker.roatetAngle);
        return sticker.helpBox.contains(mPoint.x, mPoint.y);
    }

    public class StickerTask extends AsyncTask<Bitmap, Void, Bitmap> {
        public Activity mActivity;

        public StickerTask(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            Bitmap resultBit = Bitmap.createBitmap(mImageR.width(), mImageR.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBit);

            Rect r1 = new Rect(0, 0, bitmaps[0].getWidth(), bitmaps[0].getHeight());
            Rect r2 = new Rect(0, 0, mImageR.width(), mImageR.height());
            canvas.drawBitmap(bitmaps[0], r1, r2, null);
            for (StickerItem sticker : mStickerItems) {
                canvas.drawBitmap(sticker.bitmap, sticker.matrix, null);
            }
            saveImage(resultBit);

            return resultBit;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Toast.makeText(mActivity.getApplicationContext(), "保存成功", Toast.LENGTH_LONG).show();
        }

        private void saveImage(final Bitmap image) {
            String imageFileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + "/local");
            boolean success = true;
            if (!storageDir.exists()) {
                success = storageDir.mkdirs();
            }
            if (success) {
                File imageFile = new File(storageDir, imageFileName);
                final String savedImagePath = imageFile.getAbsolutePath();
                try {
                    OutputStream fOut = new FileOutputStream(imageFile);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                galleryAddPic(savedImagePath);
            }
        }

        private void galleryAddPic(String imagePath) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(imagePath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            mActivity.sendBroadcast(mediaScanIntent);
        }
    }

    public void onSave() {
        if (mTask != null) {
            return;
        }

        mTask = new StickerTask(mActivity);
        mTask.execute(mBaseImage);
    }
}
