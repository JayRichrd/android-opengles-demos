package com.demo.openglesdemos.shape;

import com.demo.openglesdemos.R;
import com.demo.openglesdemos.base.BaseRender;
import com.demo.openglesdemos.utils.EGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES30.GL_FLOAT;
import static android.opengl.GLES30.GL_TRIANGLE_FAN;
import static android.opengl.GLES30.glDisableVertexAttribArray;
import static android.opengl.GLES30.glDrawArrays;
import static android.opengl.GLES30.glEnableVertexAttribArray;
import static android.opengl.GLES30.glGetAttribLocation;
import static android.opengl.GLES30.glGetUniformLocation;
import static android.opengl.GLES30.glUniform4fv;
import static android.opengl.GLES30.glVertexAttribPointer;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

public class Rect3DRender extends BaseRender {
    private static final String VERTEX_ATTRIB_POSITION = "vPosition";
    private static final int VERTEX_ATTRIB_POSITION_SIZE = 3;
    private static final String VERTEX_ATTRIB_COLOR = "aColor";

    private  float[] vertex ={
            -0.5f,0.5f,0.0f,
            -0.5f,-0.5f,0.0f,
            0.5f,-0.5f,0.0f,
            0.5f,0.5f,0.0f
    };
    private float[] color = {1.0f, 0.5f, 0.3f, 1.0f};

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private float[] modelMatrix = new float[16];

    public Rect3DRender() {
        //初始化顶点数据
        initVertexAttrib();
    }

    private void initVertexAttrib() {
        colorBuffer = EGLUtil.getFloatBuffer(color);
        vertexBuffer = EGLUtil.getFloatBuffer(vertex);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视口
        glViewport(0, 0, width, height);
        //计算透视投影矩阵
        perspectiveM(projectionMatrix,
                0,
                45,
                (float) width / (float) height,
                1f,
                10f);
        //设置变换矩阵
        setIdentityM(modelMatrix, 0);
        //投影矩阵的近平面在 -1f 处，所以将图形z坐标负方向平移2.5，以能够被观察到
        translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        //旋转一定角度，看起来由3D效果
        rotateM(modelMatrix, 0, -45f, 1f, 0f, 0f);

        //将透视投影矩阵和变换矩阵合并
        float[] tmp = new float[16];
        multiplyMM(tmp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(tmp, 0, projectionMatrix, 0, tmp.length);
    }

    @Override
    public int getVertexShaderResId() {
        return R.raw.rect3d_vertex_shader;
    }

    @Override
    public int getFragmentShaderResId() {
        return R.raw.rect3d_fragment_shader;
    }

    @Override
    public void draw() {
        int vertexLoc = glGetAttribLocation(program, VERTEX_ATTRIB_POSITION);
        int colorLoc = glGetUniformLocation(program, VERTEX_ATTRIB_COLOR);

        glVertexAttribPointer(vertexLoc,
                VERTEX_ATTRIB_POSITION_SIZE,
                GL_FLOAT,
                false,
                0,
                vertexBuffer);

        glUniform4fv(colorLoc, 1, colorBuffer);

        glEnableVertexAttribArray(vertexLoc);

        glDrawArrays(GL_TRIANGLE_FAN,0,vertex.length / 3);

        glDisableVertexAttribArray(vertexLoc);
    }
}
