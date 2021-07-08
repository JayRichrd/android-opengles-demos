package com.demo.openglesdemos.egl;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.demo.openglesdemos.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EGLTextureFragment extends Fragment {
    public static final String TAG = "EGLTextureFragment";

    private EGLTextureRender eglTextureRender;

    public EGLTextureFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_texture_egl, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eglTextureRender = new EGLTextureRender();
        eglTextureRender.start();

        SurfaceView surfaceView = view.findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.i(TAG, "surfaceCreated: ");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.i(TAG, "surfaceChanged: format = " + format + ", width = " + width + ", height = " + height);
                eglTextureRender.render(holder.getSurface(), width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "surfaceDestroyed: ");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eglTextureRender.release();
        eglTextureRender = null;
    }
}
