package com.example.pc.guaguaka;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class GuaGuaKa extends View {
    private Paint mOutterPaint;
    private Path mPath;

    private Canvas mCanvas;
    private Bitmap mBitmap;

    private float mLastX;
    private float mLastY;

    private static final int DEFAULT_STROKE_WIDTH = 20;
    private int mStrokeWidth = DEFAULT_STROKE_WIDTH;

    private static final int DEFAULT_MASK_COLOR = Color.parseColor("#c0c0c0");
    private Drawable mMaskDrawable;

    private static final int TYPE_DRAWABLE = 0;
    private static final int TYPE_TEXT = 1;
    private int type = TYPE_DRAWABLE;

    private Drawable mBackDrawable;
    private Canvas mBackCanvas;
    private Bitmap mBackBitmap;

    private Paint mTextPaint;

    private static final int DEFAULT_TEXT_SIZE = 22;
    private int mTextSize = 22;

    private static final int DEFAULT_TEXT_COLOR = Color.DKGRAY;
    private int mTextColor = Color.DKGRAY;

    private String mText;
    private Rect mTextBound;

    private boolean mAutoClear = false;
    private Runnable mRunnable;
    private boolean isCompleted = false;
    private static final int COMPLETE_PERCENT = 70;

    public GuaGuaKa(Context context) {
        this(context, null);
    }

    public GuaGuaKa(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuaGuaKa(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GuaGuaKa);
        int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            switch (a.getIndex(i)) {
                case R.styleable.GuaGuaKa_type:
                    type = a.getInt(R.styleable.GuaGuaKa_type, TYPE_DRAWABLE);
                    break;
                case R.styleable.GuaGuaKa_back_image:
                    mBackDrawable = a.getDrawable(R.styleable.GuaGuaKa_back_image);
                    break;
                case R.styleable.GuaGuaKa_maskDrawable:
                    mMaskDrawable = a.getDrawable(R.styleable.GuaGuaKa_maskDrawable);
                    break;
                case R.styleable.GuaGuaKa_strokeWidth:
                    mStrokeWidth = a.getColor(R.styleable.GuaGuaKa_strokeWidth, DEFAULT_MASK_COLOR);
                    break;
                case R.styleable.GuaGuaKa_text:
                    mText = a.getString(R.styleable.GuaGuaKa_text);
                    break;
                case R.styleable.GuaGuaKa_textColor:
                    mTextColor = a.getColor(R.styleable.GuaGuaKa_textColor, DEFAULT_TEXT_COLOR);
                    break;
                case R.styleable.GuaGuaKa_textSize:
                    mTextSize = a.getDimensionPixelSize(R.styleable.GuaGuaKa_textSize,
                            (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.GuaGuaKa_autoClear:
                    mAutoClear = a.getBoolean(R.styleable.GuaGuaKa_autoClear, false);
                    break;
            }
        }
        a.recycle();

        init();
    }

    private void init() {
        mPath = new Path();
        mOutterPaint = new Paint();
        mOutterPaint.setColor(Color.RED);
        mOutterPaint.setAntiAlias(true);
        mOutterPaint.setDither(true);
        mOutterPaint.setStyle(Paint.Style.STROKE);
        mOutterPaint.setStrokeJoin(Paint.Join.ROUND);
        mOutterPaint.setStrokeCap(Paint.Cap.ROUND);
        mOutterPaint.setStrokeWidth(mStrokeWidth);

        if (type == TYPE_TEXT) {
            mTextBound = new Rect();
            mTextPaint = new Paint();
            mTextPaint.setStyle(Paint.Style.FILL);
            mTextPaint.setTextScaleX(2.0f);
            mTextPaint.setColor(mTextColor);
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
        }
    }

    private void setBounds(Drawable drawable, int reqWidth, int reqHeight) {
        float scale = 1.0f;
        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();
        if (reqWidth != dWidth || reqHeight != dHeight) {
            scale = Math.max(reqWidth*1.0f/dWidth, reqHeight*1.0f/dHeight);
        }
        drawable.setBounds(0, 0, (int)(dWidth*scale), (int)(dHeight*scale));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        if (mMaskDrawable == null) {
            mMaskDrawable = new ColorDrawable(DEFAULT_MASK_COLOR);
        }
        if (mMaskDrawable instanceof ColorDrawable) {
            mMaskDrawable.setBounds(0, 0, width, height);
        } else {
            setBounds(mMaskDrawable, width, height);
        }
        mMaskDrawable.draw(mCanvas);

        if (type == TYPE_DRAWABLE && mBackDrawable != null) {
            mBackBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mBackCanvas = new Canvas(mBackBitmap);

            setBounds(mBackDrawable, width, height);
            mBackDrawable.draw(mBackCanvas);
        }

        if (mAutoClear) {
            mRunnable = new Runnable() {
                private int w = getWidth();
                private int h = getHeight();
                private int[] mPixels = new int[w*h];

                @Override
                public void run() {

                    int wipeArea = 0;
                    int totalArea = w*h;
                    mBitmap.getPixels(mPixels, 0, w, 0, 0, w, h);
                    for (int i = 0; i < h; i++) {
                        for (int j = 0; j < w; j++) {
                            int index = i*w + j;
                            if (mPixels[index] == 0) {
                                wipeArea++;
                            }
                        }
                    }
                    if (wipeArea>0 && totalArea>0) {
                        int percent = 100 * wipeArea / totalArea;
                        if (percent>COMPLETE_PERCENT) {
                            isCompleted = true;
                            postInvalidate();
                        }
                    }
                }
            };
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (type == TYPE_DRAWABLE && mBackBitmap != null) {
            canvas.drawBitmap(mBackBitmap, 0, 0, null);
        } else if (type == TYPE_TEXT) {
            canvas.drawText(mText, getWidth()/2-mTextBound.width()/2, getHeight()/2+mTextBound.height()/2, mTextPaint);
        }

        if (!isCompleted) {
            mOutterPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            mCanvas.drawPath(mPath, mOutterPaint);

            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(x, y);
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mLastX);
                float dy = Math.abs(y - mLastY);
                if (dx>3 || dy<3) {
                    mPath.lineTo(x, y);
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                if (mAutoClear) {
                    new Thread(mRunnable).start();
                }
                break;
        }
        invalidate();
        return true;
    }
}
