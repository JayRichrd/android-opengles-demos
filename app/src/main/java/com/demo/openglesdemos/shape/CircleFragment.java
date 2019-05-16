package com.demo.openglesdemos.shape;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demo.openglesdemos.R;
import com.demo.openglesdemos.base.BaseGLFragment;
import com.demo.openglesdemos.base.BaseRender;

/**
 * A simple {@link Fragment} subclass.
 */
public class CircleFragment extends BaseGLFragment {


    public CircleFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_circle;
    }

    @Override
    public int getGLSurfaceViewId() {
        return R.id.glsfv;
    }

    @Override
    public BaseRender getRender() {
        return new CircleRender();
    }

}
