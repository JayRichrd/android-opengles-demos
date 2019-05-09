package com.demo.openglesdemos.render;

import android.opengl.GLSurfaceView;

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
        //将顶点数据拷贝映射到 native 内存中，以便opengl能够访问
        verticesBuffer = ByteBuffer
                .allocateDirect(VERTEX.length * BYTES_PER_FLOAT)//直接分配 native 内存，不会被gc
                .order(ByteOrder.nativeOrder())//和本地平台保持一致的字节序（大/小头）
                .asFloatBuffer();//将底层字节映射到FloatBuffer实例，方便使用
        verticesBuffer
                .put(VERTEX)//将顶点拷贝到 native 内存中
                .position(0);//每次 put position 都会 + 1，需要在绘制前重置为0

        //将顶点颜色数据拷贝映射到 native 内存中，以便opengl能够访问
        verticeColorsBuffer = ByteBuffer
                .allocateDirect(VERTEX_COLORS.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        verticeColorsBuffer
                .put(VERTEX_COLORS)
                .position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //获取顶点着色器
        int vertextShader = EGLUtil.loadShader(GL_VERTEX_SHADER, VERTEX_SHADER_SOURCE);
        //获取片段着色器
        int fragmentShader = EGLUtil.loadShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER_SOURCE);
        //创建并连接程序
        program = EGLUtil.createAndLinkProgram(vertextShader, fragmentShader);
        if(program == 0) return;
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
