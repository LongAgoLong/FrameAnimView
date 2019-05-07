package com.leo.libframeanimview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * a SurfaceView which draws bitmaps one after another like frame animation
 */
public class FrameSurfaceView extends BaseSurfaceView {
    private static final String TAG = FrameSurfaceView.class.getSimpleName();
    public static final int INVALID_BITMAP_INDEX = Integer.MAX_VALUE;

    private List<Integer> bitmaps = new ArrayList<>();
    private Bitmap frameBitmap;
    private int bitmapIndex = INVALID_BITMAP_INDEX;
    private Paint paint = new Paint();
    private BitmapFactory.Options options;
    private Rect srcRect;
    private Rect dstRect = new Rect();
    private int defaultWidth;
    private int defaultHeight;
    private boolean running;

    public void setBitmaps(List<Integer> bitmaps) {
        if (bitmaps == null || bitmaps.size() == 0) {
            return;
        }
        this.bitmaps = bitmaps;
        //by default, take the first bitmap's dimension into consideration
        getBitmapDimension(bitmaps.get(0));
    }

    private void getBitmapDimension(Integer integer) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(this.getResources(), integer, options);
        defaultWidth = options.outWidth;
        defaultHeight = options.outHeight;
        srcRect = new Rect(0, 0, defaultWidth, defaultHeight);
        Log.v(TAG, "FrameSurfaceView.getBitmapDimension()" + "  defaultWidth=" + defaultWidth + " defaultHeight=" + defaultHeight);
        //we have to re-measure to make defaultWidth in use in onMeasure()
        requestLayout();

    }

    public FrameSurfaceView(Context context) {
        super(context);
    }

    public FrameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        options = new BitmapFactory.Options();
        options.inMutable = true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        dstRect.set(0, 0, getWidth(), getHeight());
    }

    @Override
    protected int getDefaultWidth() {
        return defaultWidth;
    }

    @Override
    protected int getDefaultHeight() {
        return defaultHeight;
    }

    @Override
    protected void onFrameDrawFinish() {
//        recycle();
    }

    /**
     * recycle the bitmap used by frame animation.
     * Usually it should be invoked when the ui of frame animation is no longer visible
     */
    public void recycle() {
        if (frameBitmap != null) {
            frameBitmap.recycle();
            frameBitmap = null;
        }
    }

    @Override
    protected void onFrameDraw(Canvas canvas) {
        clearCanvas(canvas);
        if (!isRunning()) {
            resetFirstFrame(canvas);
            return;
        }
        if (!isFinish()) {
            drawOneFrame(canvas);
        } else {
            onFrameAnimationEnd();
            resetFirstFrame(canvas);
        }
    }

    /**
     * draw a single frame which is a bitmap
     *
     * @param canvas
     */
    private void drawOneFrame(Canvas canvas) {
        Log.v(TAG, "ProgressRingSurfaceView.onFrameDraw()" + "  bitmapIndex=" + bitmapIndex + " measureWidth=" + getMeasuredWidth());
        if (isRepeat() && bitmapIndex >= bitmaps.size()) {
            bitmapIndex = 0;
        }
        if (bitmapIndex < bitmaps.size()) {
            drawBitmap(canvas, bitmapIndex);
        }
        bitmapIndex++;
    }

    /**
     * invoked when frame animation is done
     */
    private void onFrameAnimationEnd() {
        stop();
    }

    /**
     * whether frame animation is finished
     *
     * @return true: animation is finished, false: animation is doing
     */
    private boolean isFinish() {
        if (isRepeat()) {
            return !isRunning();
        } else {
            return (!isRunning()) || (bitmapIndex >= bitmaps.size());
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * start frame animation which means draw list of bitmaps from 0 index
     */
    @Override
    public void start() {
        if (null == bitmaps || bitmaps.isEmpty()) {
            throw new RuntimeException("FrameSurfaceView must setBitmaps first");
        }
        if (!isRunning()) {
            running = true;
            bitmapIndex = 0;
        }
    }

    @Override
    public void stop() {
        if (isRunning()) {
            running = false;
            bitmapIndex = INVALID_BITMAP_INDEX;
        }
    }

    /**
     * 重置为第一帧
     *
     * @param canvas
     */
    private void resetFirstFrame(Canvas canvas) {
        if (!bitmaps.isEmpty()) {
            drawBitmap(canvas, 0);
        }
    }

    private void drawBitmap(Canvas canvas, int index) {
        frameBitmap = decodeOriginBitmap(getResources(), bitmaps.get(index), options);
        options.inBitmap = frameBitmap;
        canvas.drawBitmap(frameBitmap, srcRect, dstRect, paint);
    }

    /**
     * clear out the drawing on canvas,preparing for the next frame
     * * @param canvas
     */
    private void clearCanvas(Canvas canvas) {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    private Bitmap decodeOriginBitmap(Resources res, int resId, BitmapFactory.Options options) {
        options.inScaled = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
