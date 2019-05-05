package com.leo.frameanimview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.leo.libframeanimview.FrameSurfaceView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FrameSurfaceView mFrameSurfaceView;

    private List<Integer> bitmaps = Arrays.asList(R.drawable.loading001,
            R.drawable.loading002,
            R.drawable.loading003);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFrameSurfaceView = findViewById(R.id.frameSurfaceView);
        mFrameSurfaceView.setRepeat(true);
        mFrameSurfaceView.setFrameDuration(600);
        mFrameSurfaceView.setBitmaps(bitmaps);
        mFrameSurfaceView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mFrameSurfaceView) {
            mFrameSurfaceView.recycle();
        }
    }
}
