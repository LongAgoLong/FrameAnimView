package com.leo.frameanimview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.leo.libframeanimview.FrameSurfaceView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FrameSurfaceView mFrameSurfaceView;
    private Button mStartBtn;
    private Button mStopBtn;


    private List<Integer> bitmaps = Arrays.asList(R.drawable.loading001,
            R.drawable.loading002,
            R.drawable.loading003);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStartBtn = findViewById(R.id.startBtn);
        mStartBtn.setOnClickListener(this);
        mStopBtn = findViewById(R.id.stopBtn);
        mStopBtn.setOnClickListener(this);
        mFrameSurfaceView = findViewById(R.id.frameSurfaceView);
        mFrameSurfaceView.setRepeat(true);
        mFrameSurfaceView.setFrameDuration(600);
        mFrameSurfaceView.setBitmaps(bitmaps);
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
        }
    }
}
