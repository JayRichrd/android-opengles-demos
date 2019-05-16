package com.demo.openglesdemos.base;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by wangyt on 2019/5/16
 */
public class BaseGLSurfaceView extends GLSurfaceView {

    public BaseGLSurfaceView(Context context) {
        this(context,null);
    }

    public BaseGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public void init() {
        //使用opengles 3.0
        setEGLContextClientVersion(3);
    }
}
