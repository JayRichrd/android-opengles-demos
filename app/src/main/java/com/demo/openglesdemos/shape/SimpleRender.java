package com.demo.openglesdemos.shape;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by wangyt on 2019/5/8
 */
public class SimpleRender implements GLSurfaceView.Renderer {
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //清空屏幕所用的颜色
        GLES30.glClearColor(1.0f, 0f, 0f, 0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置适口尺寸
        GLES30.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //使用 glClearColor 指定的颜色擦除屏幕
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
    }
}
