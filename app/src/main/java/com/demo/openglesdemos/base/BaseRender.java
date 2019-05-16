package com.demo.openglesdemos.base;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES30.*;
import static com.demo.openglesdemos.utils.EGLUtil.createAndLinkProgram;
import static com.demo.openglesdemos.utils.EGLUtil.loadShader;
import static com.demo.openglesdemos.utils.EGLUtil.loadShaderSource;

/**
 * Created by wangyt on 2019/5/16
 */
public abstract class BaseRender implements GLSurfaceView.Renderer {

    public int program;

    public abstract int getVertexShaderResId();

    public abstract int getFragmentShaderResId();

    public abstract void draw();

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //获取顶点着色器
        int vertexShader = loadShader(GL_VERTEX_SHADER, loadShaderSource(getVertexShaderResId()));
        //获取片段着色器
        int fragmentShader = loadShader(GL_FRAGMENT_SHADER, loadShaderSource(getFragmentShaderResId()));
        //创建并连接程序
        program = createAndLinkProgram(vertexShader, fragmentShader);

        //设置清除渲染时的颜色
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视口
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //擦除屏幕
        glClear(GL_COLOR_BUFFER_BIT);
        //使用程序
        glUseProgram(program);

        draw();
    }
}
