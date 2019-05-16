package com.demo.openglesdemos.shape;


import androidx.fragment.app.Fragment;

import com.demo.openglesdemos.R;
import com.demo.openglesdemos.base.BaseGLFragment;
import com.demo.openglesdemos.base.BaseRender;

/**
 * A simple {@link Fragment} subclass.
 */
public class RectFragment extends BaseGLFragment {


    public RectFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_rect;
    }

    @Override
    public int getGLSurfaceViewId() {
        return R.id.glsfv;
    }

    @Override
    public BaseRender getRender() {
        return new RectRender();
    }

}
