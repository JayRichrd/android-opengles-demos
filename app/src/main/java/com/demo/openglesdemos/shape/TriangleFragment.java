package com.demo.openglesdemos.shape;


import androidx.fragment.app.Fragment;

import com.demo.openglesdemos.base.BaseGLFragment;
import com.demo.openglesdemos.base.BaseRender;
import com.demo.openglesdemos.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TriangleFragment extends BaseGLFragment {

    public TriangleFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_triangle;
    }

    @Override
    public int getGLSurfaceViewId() {
        return R.id.glsfv;
    }

    @Override
    public BaseRender getRender() {
        return new TriangleRender();
    }
}
