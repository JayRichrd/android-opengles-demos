package com.demo.openglesdemos.fragment;


import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demo.openglesdemos.R;
import com.demo.openglesdemos.render.DemoRender;
import com.demo.openglesdemos.render.TriangleRender;
import com.demo.openglesdemos.utils.CommonUtil;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DemoFragment extends Fragment {
    private static final String TAG = "opengl-demos";
    private static final int GL_VERSION = 3;

    private GLSurfaceView glSurfaceView;

    public DemoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_demo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //初始化 GLSurfaceView
        glSurfaceView = view.findViewById(R.id.glsfv);
        //检验是否支持 opengles3.0
        if (!CommonUtil.checkGLVersion(getContext())){
            Log.e(TAG, "not supported opengl es 3.0+");
            getActivity().finish();
        }
        //使用 opengles 3.0
        glSurfaceView.setEGLContextClientVersion(GL_VERSION);
        //设置渲染器
        glSurfaceView.setRenderer(new DemoRender());
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
