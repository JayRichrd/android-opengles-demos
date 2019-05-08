package com.demo.openglesdemos.render;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by wangyt on 2019/5/8
 */
public class TriangleRender implements GLSurfaceView.Renderer {
    private static final String TAG = "opengl-demos";

    //float 字节数
    private static final int BYTES_PER_FLOAT = 4;
    //顶点，按逆时针顺序排列
    private static final float[] vertices = {
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f};
    //native 内存中存储顶点
    private FloatBuffer verticesBuffer;
    //程序
    private int program;

    public TriangleRender() {
        //将顶点数据拷贝映射到 native 内存中，以便opengl能够访问
        verticesBuffer = ByteBuffer
                .allocateDirect(vertices.length * BYTES_PER_FLOAT)//直接分配 native 内存，不会被gc
                .order(ByteOrder.nativeOrder())//和本地平台保持一致的字节序（大/小头）
                .asFloatBuffer();//将底层字节映射到FloatBuffer实例，方便使用
        verticesBuffer
                .put(vertices)//将顶点拷贝到 native 内存中
                .position(0);//每次 put position 都会 + 1，需要在绘制前重置为0
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //获取顶点着色器
        int vertextShader = loadShader(GLES30.GL_VERTEX_SHADER, vertextShaderSource);
        //获取片段着色器
        int fragmentShader =loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderSource);
        //创建程序
        int tmpProgram = GLES30.glCreateProgram();
        if (tmpProgram == 0) return;//创建失败
        //绑定着色器到程序
        GLES30.glAttachShader(tmpProgram, vertextShader);
        GLES30.glAttachShader(tmpProgram, fragmentShader);
        //绑定属性位置 vPosition ：0
//        GLES30.glBindAttribLocation(tmpProgram, 0, "vPosition");
        //连接程序
        GLES30.glLinkProgram(tmpProgram);
        //检查连接状态
        int[] linked = new int[1];
        GLES30.glGetProgramiv(tmpProgram,GLES30.GL_LINK_STATUS, linked, 0);
        if (linked[0] == 0){
            Log.e(TAG, "tmpProgram linked error");
            Log.e(TAG, GLES30.glGetProgramInfoLog(tmpProgram));
            GLES30.glDeleteProgram(tmpProgram);
            return;//连接失败
        }
        //保存程序，后面使用
        program = tmpProgram;

        //设置清除渲染时的颜色
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视口
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //擦除屏幕
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        //使用程序
        GLES30.glUseProgram(program);
        //获取 vPosition 属性的位置
        int vposition = GLES30.glGetAttribLocation(program, "vPosition");
        //加载顶点数据到 vPosition 属性位置
        GLES30.glVertexAttribPointer(vposition,3,GLES30.GL_FLOAT,false,0,verticesBuffer);
        GLES30.glEnableVertexAttribArray(vposition);
        //绘制
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,3);
    }

    /**
     * 加载着色器源，并编译
     *
     * @param type         顶点着色器（GL_VERTEX_SHADER）/片段着色器（GL_FRAGMENT_SHADER）
     * @param shaderSource 着色器源
     * @return 着色器
     */
    private int loadShader(int type, String shaderSource) {
        //创建着色器对象
        int shader = GLES30.glCreateShader(type);
        if (shader == 0) return 0;//创建失败
        //加载着色器源
        GLES30.glShaderSource(shader, shaderSource);
        //编译着色器
        GLES30.glCompileShader(shader);
        //检查编译状态
        int[] compiled = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;//编译失败
        }

        return shader;
    }

    /*********************** 着色器源（暂时放这，后面统一组织）**************/
    private static final String vertextShaderSource =
            "#version 300 es\n"
                    + "layout (location = 0) in vec4 vPosition;\n"
                    + "void main()\n"
                    + "{\n"
                    + "    gl_Position = vPosition;\n"
                    + "}\n";

    private static final String fragmentShaderSource =
            "#version 300 es		 			          	\n"
                    + "precision mediump float;					  	\n"
                    + "out vec4 fragColor;	 			 		  	\n"
                    + "void main()                                  \n"
                    + "{                                            \n"
                    + "  fragColor = vec4 ( 1.0, 0.0, 0.0, 1.0 );	\n"
                    + "}                                            \n";
}
