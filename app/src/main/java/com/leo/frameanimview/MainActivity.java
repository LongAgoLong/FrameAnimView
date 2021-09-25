package com.leo.frameanimview;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.leo.libframeanimview.FrameSurfaceView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FrameSurfaceView mFrameSurfaceView;
    private Button mStartBtn;
    private Button mStopBtn;
    private Button mChangeBtn;
    private int tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStartBtn = findViewById(R.id.startBtn);
        mStartBtn.setOnClickListener(this);
        mStopBtn = findViewById(R.id.stopBtn);
        mStopBtn.setOnClickListener(this);
        mChangeBtn = findViewById(R.id.changeBtn);
        mChangeBtn.setOnClickListener(this);

        mFrameSurfaceView = findViewById(R.id.frameSurfaceView);
        mFrameSurfaceView.setRepeat(true);
        mFrameSurfaceView.setFrameDuration(50);
        mFrameSurfaceView.setBitmaps(BitmapRes.BITMAP_01);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mFrameSurfaceView) {
            mFrameSurfaceView.stop();
            mFrameSurfaceView.recycle();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startBtn:
                mFrameSurfaceView.start();
                break;
            case R.id.stopBtn:
                mFrameSurfaceView.stop();
                break;
            case R.id.changeBtn:
                if (tag == 0) {
                    mFrameSurfaceView.stop();
                    mFrameSurfaceView.setBitmaps(BitmapRes.BITMAP_02);
                    mFrameSurfaceView.start();
                    tag = 1;
                } else if (tag == 1) {
                    mFrameSurfaceView.stop();
                    mFrameSurfaceView.setBitmaps(BitmapRes.BITMAP_01);
                    mFrameSurfaceView.start();
                    tag = 0;
                }
                break;
            default:
                break;
        }
    }
}
