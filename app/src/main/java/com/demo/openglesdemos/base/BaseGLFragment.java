package com.demo.openglesdemos.base;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseGLFragment extends Fragment {

    public BaseGLSurfaceView baseGLSurfaceView;

    public BaseGLFragment() {
        // Required empty public constructor
    }

    public abstract int getLayoutId();

    public abstract int getGLSurfaceViewId();

    public abstract BaseRender getRender();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //初始化 GLSurfaceView
        baseGLSurfaceView = view.findViewById(getGLSurfaceViewId());
        //设置渲染器
        baseGLSurfaceView.setRenderer(getRender());
    }

    @Override
    public void onResume() {
        super.onResume();
        baseGLSurfaceView.onResume();
    }

    @Override
    public void onPause() {
        baseGLSurfaceView.onPause();
        super.onPause();
    }
}
