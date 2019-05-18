package com.demo.openglesdemos.texture;


import androidx.fragment.app.Fragment;

import com.demo.openglesdemos.R;
import com.demo.openglesdemos.base.BaseGLFragment;
import com.demo.openglesdemos.base.BaseRender;
import com.demo.openglesdemos.shape.RectRender;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextureFragment extends BaseGLFragment {


    public TextureFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_texture;
    }

    @Override
    public int getGLSurfaceViewId() {
        return R.id.glsfv;
    }

    @Override
    public BaseRender getRender() {
        return new TextureRender();
    }

}
