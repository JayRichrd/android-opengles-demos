package com.demo.openglesdemos.utils;

import android.content.Context;
import static android.opengl.GLES30.*;
import static android.opengl.GLUtils.texImage2D;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

/**
 * Created by wangyt on 2019/5/9
 */
public class EGLUtil {
    private static final String TAG = "EGLUtil";

    private static Context context;

    public static void init(Context ctx){
        context = ctx.getApplicationContext();
    }

    /*********************** 纹理 ************************/
    public static int loadTexture(int resId){
        //创建纹理对象
        int[] textureObjIds = new int[1];
        //生成纹理：纹理数量、保存纹理的数组，数组偏移量
        glGenTextures(1, textureObjIds,0);
        if(textureObjIds[0] == 0){
            throw new RuntimeException("创建纹理对象失败");
        }
        //原尺寸加载位图资源（禁止缩放）
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        if (bitmap == null){
            //删除纹理对象
            glDeleteTextures(1, textureObjIds, 0);
            throw new RuntimeException("加载位图失败");
        }
        //绑定纹理到opengl
        glBindTexture(GL_TEXTURE_2D, textureObjIds[0]);
        //设置放大、缩小时的纹理过滤方式，必须设定，否则纹理全黑
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        //将位图加载到opengl中，并复制到当前绑定的纹理对象上
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        //创建 mip 贴图，能够优化显示效果，但同时也会增大内存占用
        glGenerateMipmap(GL_TEXTURE_2D);
        //释放bitmap资源（上面已经把bitmap的数据复制到纹理上了）
        bitmap.recycle();
        //解绑当前纹理，防止其他地方以外改变该纹理
        glBindTexture(GL_TEXTURE_2D, 0);
        //返回纹理对象
        return textureObjIds[0];
    }

    /*********************** 着色器、程序 ************************/
    public static String loadShaderSource(int resId){
        StringBuilder res = new StringBuilder();

        InputStream is = context.getResources().openRawResource(resId);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String nextLine;
            try {
                while ((nextLine = br.readLine()) != null) {
                    res.append(nextLine);
                    res.append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "loadShaderSource: error msg:" + e.getLocalizedMessage());
            }
        return res.toString();
    }

    /**
     * 加载着色器源，并编译
     *
     * @param type         顶点着色器（GL_VERTEX_SHADER）/片段着色器（GL_FRAGMENT_SHADER）
     * @param shaderSource 着色器源
     * @return 着色器
     */
    public static int loadShader(int type, String shaderSource){
        //创建着色器对象
        int shader = glCreateShader(type);
        if (shader == 0){
            Log.e(TAG, "loadShader fail! type = " + type);
            return 0;//创建失败
        }
        //加载着色器源
        glShaderSource(shader, shaderSource);
        //编译着色器
        glCompileShader(shader);
        //检查编译状态
        int[] compiled = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, compiled, 0);
        Log.i(TAG, "loadShader: compiled = " + Arrays.toString(compiled));
        if (compiled[0] == 0) {
            Log.e(TAG, glGetShaderInfoLog(shader));
            glDeleteShader(shader);
            return 0;//编译失败
        }

        return shader;
    }

    public static int createAndLinkProgram(int vertexShader, int fragmentShader){
        //创建程序
        int program = glCreateProgram();
        if (program == 0) {
            //创建失败
            throw new RuntimeException("opengl error: 程序创建失败");
        }
        //绑定着色器到程序
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        //连接程序
        glLinkProgram(program);
        //检查连接状态
        int[] linked = new int[1];
        glGetProgramiv(program,GL_LINK_STATUS, linked, 0);
        Log.i(TAG, "createAndLinkProgram: linked = " + Arrays.toString(linked));
        if (linked[0] == 0){
            glDeleteProgram(program);
            throw new RuntimeException("opengl error: 程序连接失败");
        }
        return program;
    }


    /*********************** （暂时放这，后面统一组织）**************/
    public static FloatBuffer getVertexBuffer(){
        return getFloatBuffer(VERTEX);
    }

    public static FloatBuffer getVertexColorBuffer(){
        return getFloatBuffer(VERTEX_COLORS);
    }

    public static FloatBuffer getTextureCoordinateBuffer(){
        return getFloatBuffer(TEXTURE_COORDINATE);
    }

    public static FloatBuffer getFloatBuffer(float[] array){
        //将数据拷贝映射到 native 内存中，以便opengl能够访问
        FloatBuffer buffer = ByteBuffer
                .allocateDirect(array.length * BYTES_PER_FLOAT)//直接分配 native 内存，不会被gc
                .order(ByteOrder.nativeOrder())//和本地平台保持一致的字节序（大/小头）
                .asFloatBuffer();//将底层字节映射到FloatBuffer实例，方便使用
        buffer.put(array)//将顶点拷贝到 native 内存中
                .position(0);//每次 put position 都会 + 1，需要在绘制前重置为0

        return buffer;
    }

    public static ShortBuffer getShortBuffer(short[] array){
        //将数据拷贝映射到 native 内存中，以便opengl能够访问
        ShortBuffer buffer = ByteBuffer
                .allocateDirect(array.length * BYTES_PER_SHORT)//直接分配 native 内存，不会被gc
                .order(ByteOrder.nativeOrder())//和本地平台保持一致的字节序（大/小头）
                .asShortBuffer();//将底层字节映射到Buffer实例，方便使用
        buffer
                .put(array)//将顶点拷贝到 native 内存中
                .position(0);//每次 put position 都会增加，需要在绘制前重置为0

        return buffer;
    }

    //各数值类型字节数
    public static final int BYTES_PER_FLOAT = 4;
    public static final int BYTES_PER_SHORT = 2;
    //顶点，按逆时针顺序排列
//    public static final float[] VERTEX = {
//            -0.5f, 0.5f, 0.0f,
//            0.5f, 0.5f, 0.0f,
//            -0.5f, -0.5f, 0.0f,
//            0.5f, -0.5f, 0.0f};
    public static final float[] VERTEX = {
            // 第一个矩形
            -1.0f, 1.0f,// 三角形1
            -1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 1.0f,// 三角形2
            -1.0f, 0.0f,
            0.0f, 0.0f,
            // 第二个矩形
            0.0f, 0.0f,// 三角形
            0.0f, -1.0f,
            1.0f, 0.0f,
            1.0f, 0.0f,// 三角形
            0.0f, -1.0f,
            1.0f, -1.0f
    };
    //顶点颜色
    public static final float[] VERTEX_COLORS = {
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };
    //纹理坐标，（s,t），t坐标方向和顶点y坐标反着
//    public static final float[] TEXTURE_COORDINATE = {
//            0.0f,0.0f,
//            1.0f,0.0f,
//            0.0f,1.0f,
//            1.0f,1.0f
//    };
    public static final float[] TEXTURE_COORDINATE = {
            //第一个矩形
            0.0f, 0.0f,//三角形1
            0.0f, 0.5f,
            0.5f, 0.0f,
            0.5f, 0.0f,//三角形2
            0.0f, 0.5f,
            0.5f, 0.5f,
            //第二个矩形
            0.5f, 0.5f,//三角形1
            0.5f, 1.0f,
            1.0f, 0.5f,
            1.0f, 0.5f,//三角形2
            0.5f, 1.0f,
            1.0f, 1.0f
    };
}
