package com.demo.openglesdemos.render;

import static android.opengl.EGL14.*;

import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

import com.demo.openglesdemos.R;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static com.demo.openglesdemos.utils.EGLUtil.*;

/**
 * Created by wangyt on 2019/5/9
 */
public class EGLRender extends HandlerThread {

    private EGLConfig eglConfig;
    private EGLDisplay eglDisplay;
    private EGLContext eglContext;

    public EGLRender() {
        super("ELGRender");
    }

    private void createEGL(){
        //获取显示设备
        eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL_NO_DISPLAY){
            throw new RuntimeException("egl error:" + eglGetError());
        }
        //初始化EGL
        int[] version = new int[2];
        if (!eglInitialize(eglDisplay, version,0,version,1)){
            throw new RuntimeException("egl error:" + eglGetError());
        }
        //EGL选择配置
        int[] configAttribList = {
                EGL_BUFFER_SIZE, 32,
                EGL_ALPHA_SIZE, 8,
                EGL_BLUE_SIZE, 8,
                EGL_GREEN_SIZE, 8,
                EGL_RED_SIZE, 8,
                EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
                EGL_NONE
        };
        int[] numConfig = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        if(!eglChooseConfig(eglDisplay,
                configAttribList, 0,
                configs,0, configs.length,
                numConfig,0)){
            throw new RuntimeException("egl error:" + eglGetError());
        }
        eglConfig = configs[0];
        //创建ELG上下文
        int[] contextAttribList = {
                EGL_CONTEXT_CLIENT_VERSION,2,
                EGL_NONE
        };
        eglContext = eglCreateContext(eglDisplay, eglConfig,EGL_NO_CONTEXT,contextAttribList,0);
        if (eglContext == EGL_NO_CONTEXT){
            throw new RuntimeException("egl error:" + eglGetError());
        }
    }

    private void destroyEGL(){
        eglDestroyContext(eglDisplay, eglContext);
        eglContext = EGL_NO_CONTEXT;
        eglDisplay = EGL_NO_DISPLAY;
    }

    @Override
    public synchronized void start() {
        super.start();

        new Handler(getLooper()).post(new Runnable() {
            @Override
            public void run() {
                createEGL();
            }
        });
    }

    public void release(){
        new Handler(getLooper()).post(new Runnable() {
            @Override
            public void run() {
                destroyEGL();
                quit();
            }
        });
    }

    public void render(Surface surface, int width, int height){
        //创建屏幕上渲染区域：EGL窗口
        int[] surfaceAttribList = {EGL_NONE};
        EGLSurface eglSurface = eglCreateWindowSurface(eglDisplay, eglConfig, surface, surfaceAttribList, 0);
        //指定当前上下文
        eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
        //获取着色器
        int vertexShader = loadShader(GL_VERTEX_SHADER, loadShaderSource(R.raw.triangle_vertex_shader));
        int fragmentShader = loadShader(GL_FRAGMENT_SHADER, loadShaderSource(R.raw.triangle_fragment_shader));
        //创建并连接程序
        int program = createAndLinkProgram(vertexShader, fragmentShader);
        //设置清除渲染时的颜色
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        //设置视口
        glViewport(0, 0, width, height);
        //获取顶点、颜色数据
        FloatBuffer vertexBuffer = getVertextBuffer();
        FloatBuffer vertexColorBuffer = getVertexColorBuffer();
        //擦除屏幕
        glClear(GL_COLOR_BUFFER_BIT);
        //使用程序
        glUseProgram(program);
        //绑定顶点、颜色数据到指定属性位置
        int vposition = glGetAttribLocation(program, "vPosition");
        glVertexAttribPointer(vposition,3,GL_FLOAT,false,0,vertexBuffer);
        glEnableVertexAttribArray(vposition);
        int aColor = glGetAttribLocation(program, "aColor");
        glEnableVertexAttribArray(aColor);
        glVertexAttribPointer(aColor, 4, GL_FLOAT, false, 0, vertexColorBuffer);
        //绘制
        glDrawArrays(GL_TRIANGLES,0,3);
        //交换 surface 和显示器缓存
        eglSwapBuffers(eglDisplay, eglSurface);
        //释放
        eglDestroySurface(eglDisplay, eglSurface);
    }
}
