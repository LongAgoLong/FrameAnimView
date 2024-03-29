package com.leo.libframeanimview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

public abstract class BaseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = BaseSurfaceView.class.getSimpleName();
    public static final int DEFAULT_FRAME_DURATION_MILLISECOND = 50;

    private HandlerThread handlerThread;
    private SurfaceViewHandler handler;
    private int frameDuration = DEFAULT_FRAME_DURATION_MILLISECOND;
    private Canvas canvas;
    private boolean isAlive;
    private boolean isRepeat;

    public BaseSurfaceView(Context context) {
        super(context);
        init();
    }

    public BaseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        getHolder().addCallback(this);
        setBackgroundTransparent();
    }

    private void setBackgroundTransparent() {
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isAlive = true;
        startDrawThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopDrawThread();
        isAlive = false;
    }

    private void stopDrawThread() {
        handlerThread.quit();
        handler = null;
    }

    private void startDrawThread() {
        handlerThread = new HandlerThread("SurfaceViewThread");
        handlerThread.start();
        handler = new SurfaceViewHandler(handlerThread.getLooper());
        handler.post(new DrawRunnable(this));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int originWidth = getMeasuredWidth();
        int originHeight = getMeasuredHeight();
        int width = widthMode == MeasureSpec.AT_MOST ? getDefaultWidth() : originWidth;
        int height = heightMode == MeasureSpec.AT_MOST ? getDefaultHeight() : originHeight;
        setMeasuredDimension(width, height);
//        Log.v(TAG, "BaseSurfaceView.onMeasure()" + "  default Width=" + getDefaultWidth() + " default height=" + getDefaultHeight());
    }

    /**
     * the width is used when wrap_content is set to layout_width
     * the child knows how big it should be
     *
     * @return
     */
    protected abstract int getDefaultWidth();

    /**
     * the height is used when wrap_content is set to layout_height
     * the child knows how big it should be
     *
     * @return
     */
    protected abstract int getDefaultHeight();


    private static class SurfaceViewHandler extends Handler {
        public SurfaceViewHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    private static class DrawRunnable implements Runnable {
        private final WeakReference<BaseSurfaceView> viewWReference;

        public DrawRunnable(BaseSurfaceView baseSurfaceView) {
            viewWReference = new WeakReference<>(baseSurfaceView);
        }

        @Override
        public void run() {
            BaseSurfaceView baseSurfaceView = viewWReference.get();
            if (baseSurfaceView == null) {
                return;
            }
            if (!baseSurfaceView.isAlive) {
                return;
            }
            try {
                baseSurfaceView.canvas = baseSurfaceView.getHolder().lockCanvas();
                baseSurfaceView.onFrameDraw(baseSurfaceView.canvas);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                baseSurfaceView.getHolder().unlockCanvasAndPost(baseSurfaceView.canvas);
                baseSurfaceView.onFrameDrawFinish();
            }

            baseSurfaceView.handler.postDelayed(this, baseSurfaceView.frameDuration);
        }
    }

    /**
     * 设置帧间隔
     *
     * @param frameDuration
     */
    public void setFrameDuration(int frameDuration) {
        this.frameDuration = frameDuration;
    }

    /**
     * 是否循环播放
     *
     * @param repeat
     */
    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    /**
     * 是否循环播放
     *
     * @return
     */
    public boolean isRepeat() {
        return isRepeat;
    }

    /**
     * it is will be invoked after one frame is drawn
     */
    protected abstract void onFrameDrawFinish();

    /**
     * draw one frame to the surface by canvas
     *
     * @param canvas
     */
    protected abstract void onFrameDraw(Canvas canvas);

    public abstract void start();

    public abstract void stop();

    public abstract boolean isRunning();
}
