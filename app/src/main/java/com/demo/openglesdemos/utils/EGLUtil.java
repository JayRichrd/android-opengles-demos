package com.demo.openglesdemos.utils;

import android.opengl.GLES30;
import android.util.Log;

/**
 * Created by wangyt on 2019/5/9
 */
public class EGLUtil {
    private static final String TAG = "opengl-demos";

    /**
     * 加载着色器源，并编译
     *
     * @param type         顶点着色器（GL_VERTEX_SHADER）/片段着色器（GL_FRAGMENT_SHADER）
     * @param shaderSource 着色器源
     * @return 着色器
     */
    public static int loadShader(int type, String shaderSource){
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

    public static int createAndLinkProgram(int vertextShader, int fragmentShader){
        //创建程序
        int program = GLES30.glCreateProgram();
        if (program == 0) return 0;//创建失败
        //绑定着色器到程序
        GLES30.glAttachShader(program, vertextShader);
        GLES30.glAttachShader(program, fragmentShader);
        //连接程序
        GLES30.glLinkProgram(program);
        //检查连接状态
        int[] linked = new int[1];
        GLES30.glGetProgramiv(program,GLES30.GL_LINK_STATUS, linked, 0);
        if (linked[0] == 0){
            Log.e(TAG, "program linked error");
            Log.e(TAG, GLES30.glGetProgramInfoLog(program));
            GLES30.glDeleteProgram(program);
            return 0;//连接失败
        }
        return program;
    }


    /*********************** （暂时放这，后面统一组织）**************/
    //float 字节数
    public static final int BYTES_PER_FLOAT = 4;
    //顶点，按逆时针顺序排列
    public static final float[] VERTEX = {
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f};
    //顶点颜色
    public static final float[] VERTEX_COLORS = {
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };

    public static final String VERTEX_SHADER_SOURCE =
            "#version 300 es\n"
                    + "layout (location = 0) in vec4 vPosition;\n"
                    + "layout (location = 1) in vec4 aColor;\n"
                    + "out vec4 vColor;\n"
                    + "void main()\n"
                    + "{\n"
                    + "    gl_Position = vPosition;\n"
                    + "    vColor = aColor;\n"
                    + "}\n";

    public static final String FRAGMENT_SHADER_SOURCE =
            "#version 300 es		 			          	\n"
                    + "precision mediump float;					  	\n"
                    + "in vec4 vColor;					  	\n"
                    + "out vec4 fragColor;	 			 		  	\n"
                    + "void main()                                  \n"
                    + "{                                            \n"
                    + "  fragColor = vColor;	\n"
                    + "}                                            \n";
}
