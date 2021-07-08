package com.demo.openglesdemos.egl;

import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;

import com.demo.openglesdemos.R;

import java.nio.FloatBuffer;
import java.util.Arrays;

import static android.opengl.EGL14.EGL_ALPHA_SIZE;
import static android.opengl.EGL14.EGL_BLUE_SIZE;
import static android.opengl.EGL14.EGL_BUFFER_SIZE;
import static android.opengl.EGL14.EGL_CONTEXT_CLIENT_VERSION;
import static android.opengl.EGL14.EGL_DEFAULT_DISPLAY;
import static android.opengl.EGL14.EGL_GREEN_SIZE;
import static android.opengl.EGL14.EGL_NONE;
import static android.opengl.EGL14.EGL_NO_CONTEXT;
import static android.opengl.EGL14.EGL_NO_DISPLAY;
import static android.opengl.EGL14.EGL_OPENGL_ES2_BIT;
import static android.opengl.EGL14.EGL_RED_SIZE;
import static android.opengl.EGL14.EGL_RENDERABLE_TYPE;
import static android.opengl.EGL14.EGL_SURFACE_TYPE;
import static android.opengl.EGL14.EGL_WINDOW_BIT;
import static android.opengl.EGL14.eglChooseConfig;
import static android.opengl.EGL14.eglCreateContext;
import static android.opengl.EGL14.eglCreateWindowSurface;
import static android.opengl.EGL14.eglDestroyContext;
import static android.opengl.EGL14.eglDestroySurface;
import static android.opengl.EGL14.eglGetDisplay;
import static android.opengl.EGL14.eglGetError;
import static android.opengl.EGL14.eglInitialize;
import static android.opengl.EGL14.eglMakeCurrent;
import static android.opengl.EGL14.eglSwapBuffers;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static com.demo.openglesdemos.utils.EGLUtil.createAndLinkProgram;
import static com.demo.openglesdemos.utils.EGLUtil.getTextureCoordinateBuffer;
import static com.demo.openglesdemos.utils.EGLUtil.getVertexBuffer;
import static com.demo.openglesdemos.utils.EGLUtil.loadShader;
import static com.demo.openglesdemos.utils.EGLUtil.loadShaderSource;
import static com.demo.openglesdemos.utils.EGLUtil.loadTexture;

/**
 * Created by wangyt on 2019/5/10
 */
public class EGLTextureRender extends HandlerThread {
    public static final String TAG = "EGLTextureRender";
    public static final String TEXTURE_RENDER_THREAD = "texture_render_t";

    private EGLConfig eglConfig;
    private EGLDisplay eglDisplay;
    private EGLContext eglContext;

    public EGLTextureRender() {
        super(TEXTURE_RENDER_THREAD);
    }

    private void destroyEGL() {
        eglDestroyContext(eglDisplay, eglContext);
        eglContext = EGL_NO_CONTEXT;
        eglDisplay = EGL_NO_DISPLAY;
    }

    @Override
    public synchronized void start() {
        super.start();
        new Handler(getLooper()).post(this::createEGL);
    }

    private void createEGL() {
        //1.获取显示设备
        eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL_NO_DISPLAY) {
            throw new RuntimeException("egl error:" + eglGetError());
        }

        //2.初始化EGL
        int[] version = new int[2];
        if (!eglInitialize(eglDisplay, version, 0, version, 1)) {
            throw new RuntimeException("egl error:" + eglGetError());
        }
        Log.i(TAG, "createEGL: version = " + Arrays.toString(version));

        //3.EGL选择配置
        int[] configAttribList = {
                EGL_BUFFER_SIZE, 32,//颜色缓冲区
                EGL_ALPHA_SIZE, 8, //透明度
                EGL_BLUE_SIZE, 8, //蓝色分量
                EGL_GREEN_SIZE, 8, //绿色分量
                EGL_RED_SIZE, 8, //红色分量
                EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, //渲染类型
                EGL_SURFACE_TYPE, EGL_WINDOW_BIT, //EGL窗口支持的类型
                EGL_NONE //结束
        };
        int[] numConfig = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        if (!eglChooseConfig(eglDisplay,
                configAttribList, 0,
                configs, 0, configs.length,
                numConfig, 0)) {
            throw new RuntimeException("egl error:" + eglGetError());
        }
        Log.i(TAG, "createEGL: configs = " + Arrays.toString(configs) + ", numConfig = " + Arrays.toString(numConfig));
        eglConfig = configs[0];

        //4.创建ELG上下文
        int[] contextAttribList = {
                EGL_CONTEXT_CLIENT_VERSION, 2,// opengl版本
                EGL_NONE
        };
        // 此处可以创建共享的egl上下文，share_context
        eglContext = eglCreateContext(eglDisplay, eglConfig, EGL_NO_CONTEXT, contextAttribList, 0);
        if (eglContext == EGL_NO_CONTEXT) {
            throw new RuntimeException("egl error:" + eglGetError());
        }
    }

    public void release() {
        new Handler(getLooper()).post(() -> {
            destroyEGL();
            quit();
        });
    }

    public void render(Surface surface, int width, int height) {
        //创建屏幕上渲染区域：EGL窗口
        int[] surfaceAttribList = {EGL_NONE};
        EGLSurface eglSurface = eglCreateWindowSurface(eglDisplay, eglConfig, surface, surfaceAttribList, 0);
        //指定当前上下文
        if (!eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            throw new RuntimeException("egl make current error:" + eglGetError());
        }
        //获取着色器
        int texVertexShader = loadShader(GL_VERTEX_SHADER, loadShaderSource(R.raw.egl_texture_vertex_shader));
        int texFragmentShader = loadShader(GL_FRAGMENT_SHADER, loadShaderSource(R.raw.egl_texture_fragtment_shader));
        //创建并连接程序
        int program = createAndLinkProgram(texVertexShader, texFragmentShader);
        //设置清除渲染时的颜色
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        //设置视口
        glViewport(0, 0, width, height);
        //获取顶点、纹理坐标数据
        FloatBuffer vertexBuffer = getVertexBuffer();
        FloatBuffer texCoordinateBuffer = getTextureCoordinateBuffer();
        //擦除屏幕
        glClear(GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        //使用程序
        glUseProgram(program);
        //绑定顶点、纹理坐标到指定属性位置
        int aPosition = glGetAttribLocation(program, "a_Position");
        glVertexAttribPointer(aPosition, 3, GL_FLOAT, false, 0, vertexBuffer);
        glEnableVertexAttribArray(aPosition);
        int aTexCoord = glGetAttribLocation(program, "a_texCoord");
        glVertexAttribPointer(aTexCoord, 2, GL_FLOAT, false, 0, texCoordinateBuffer);
        glEnableVertexAttribArray(aTexCoord);
        //绑定纹理
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, loadTexture(R.drawable.android_log));
        //Set the sampler texture unit to 0
        glUniform1i(glGetUniformLocation(program, "s_texture"), 0);
        //绘制
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        //交换 surface 和显示器缓存
        eglSwapBuffers(eglDisplay, eglSurface);
        //释放
        eglDestroySurface(eglDisplay, eglSurface);
    }
}
