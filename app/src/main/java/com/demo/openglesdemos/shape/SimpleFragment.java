package com.demo.openglesdemos.shape;


import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demo.openglesdemos.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SimpleFragment extends Fragment {
    private static final String TAG = "opengl-demos";
    private static final int GL_VERSION = 3;

    private GLSurfaceView glSurfaceView;

    public SimpleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //初始化 GLSurfaceView
        glSurfaceView = view.findViewById(R.id.glsfv);
        //使用 opengles 3.0
        glSurfaceView.setEGLContextClientVersion(GL_VERSION);
        //设置渲染器
        glSurfaceView.setRenderer(new SimpleRender());
    }

    @Override
    public void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    public void onPause() {
        glSurfaceView.onPause();
        super.onPause();
    }
}
