package com.demo.openglesdemos.render;

import android.opengl.GLSurfaceView;

import com.demo.openglesdemos.R;
import com.demo.openglesdemos.utils.EGLUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES30.*;
import static com.demo.openglesdemos.utils.EGLUtil.*;

/**
 * Created by wangyt on 2019/5/8
 */
public class TriangleRender implements GLSurfaceView.Renderer {
    private static final String TAG = "opengl-demos";

    //native 内存中存储顶点
    private FloatBuffer verticesBuffer;
    //native 内存中存储的顶点颜色
    private FloatBuffer verticeColorsBuffer;
    //程序
    private int program;

    public TriangleRender() {
        verticesBuffer = getVertextBuffer();
        verticeColorsBuffer = getVertexColorBuffer();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //获取顶点着色器
        int vertexShader = loadShader(GL_VERTEX_SHADER, loadShaderSource(R.raw.triangle_vertex_shader));
        //获取片段着色器
        int fragmentShader = loadShader(GL_FRAGMENT_SHADER, loadShaderSource(R.raw.triangle_fragment_shader));
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

        //获取 vPosition 属性的位置
        int vposition = glGetAttribLocation(program, "vPosition");
        //加载顶点数据到 vPosition 属性位置
        glVertexAttribPointer(vposition,3,GL_FLOAT,false,0,verticesBuffer);
        glEnableVertexAttribArray(vposition);

        //获取 vColor 属性位置
        int aColor = glGetAttribLocation(program, "aColor");
        //加载顶点颜色数据到 vColor 属性位置
        glEnableVertexAttribArray(aColor);
        glVertexAttribPointer(aColor, 4, GL_FLOAT, false, 0, verticeColorsBuffer);

        //绘制
        glDrawArrays(GL_TRIANGLES,0,3);
    }
}
