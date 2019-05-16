package com.demo.openglesdemos.shape;

import com.demo.openglesdemos.R;
import com.demo.openglesdemos.base.BaseRender;
import com.demo.openglesdemos.utils.EGLUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES30.GL_FLOAT;
import static android.opengl.GLES30.GL_TRIANGLE_FAN;
import static android.opengl.GLES30.glDisableVertexAttribArray;
import static android.opengl.GLES30.glDrawArrays;
import static android.opengl.GLES30.glEnableVertexAttribArray;
import static android.opengl.GLES30.glGetAttribLocation;
import static android.opengl.GLES30.glGetUniformLocation;
import static android.opengl.GLES30.glUniform4fv;
import static android.opengl.GLES30.glVertexAttribPointer;

public class RectRender extends BaseRender {
    private static final String VERTEX_ATTRIB_POSITION = "vPosition";
    private static final int VERTEX_ATTRIB_POSITION_SIZE = 3;
    private static final String VERTEX_ATTRIB_COLOR = "aColor";
    private static final int VERTEX_ATTRIB_COLOR_SIZE = 4;

    private  float[] vertex ={
            0.0f,0.0f,0.0f,
            -1.0f,1.0f,0.0f,
            -1.0f,-1.0f,0.0f,
            1.0f,-1.0f,0.0f,
            1.0f,1.0f,0.0f,
            -1.0f,1.0f,0.0f
    };
    private float[] color = {1.0f, 0.5f, 0.3f, 1.0f};

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    public RectRender() {
        //初始化顶点数据
        initVertexAttrib();
    }

    private void initVertexAttrib() {
        colorBuffer = EGLUtil.getFloatBuffer(color);
        vertexBuffer = EGLUtil.getFloatBuffer(vertex);
    }

    @Override
    public int getVertexShaderResId() {
        return R.raw.rect_vertex_shader;
    }

    @Override
    public int getFragmentShaderResId() {
        return R.raw.rect_fragment_shader;
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
