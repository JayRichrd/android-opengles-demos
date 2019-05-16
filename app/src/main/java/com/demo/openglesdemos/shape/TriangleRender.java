package com.demo.openglesdemos.shape;

import com.demo.openglesdemos.base.BaseRender;
import com.demo.openglesdemos.R;
import com.demo.openglesdemos.utils.EGLUtil;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES30.*;

/**
 * Created by wangyt on 2019/5/8
 */
public class TriangleRender extends BaseRender {
    private static final String VERTEX_ATTRIB_POSITION = "vPosition";
    private static final int VERTEX_ATTRIB_POSITION_SIZE = 3;
    private static final String VERTEX_ATTRIB_COLOR = "aColor";
    private static final int VERTEX_ATTRIB_COLOR_SIZE = 4;
    private static final int STRID =
            (VERTEX_ATTRIB_POSITION_SIZE + VERTEX_ATTRIB_COLOR_SIZE) * EGLUtil.BYTES_PER_FLOAT;
    private static final float[] VERTEX_ATTRIBS = {
            //X Y Z R G B A
            0.0f, 1f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            -1f, -1f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            1f, -1f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f};
    private static final short[] VERTEX_INDEX = {0, 1, 2};
    //顶点属性buffer
    private FloatBuffer vertexAttribsBuffer;
    //顶点绘制顺序buffer
    private ShortBuffer vertexIndexBuffer;

    public TriangleRender() {
        vertexAttribsBuffer = EGLUtil.getFloatBuffer(VERTEX_ATTRIBS);
        vertexIndexBuffer = EGLUtil.getShortBuffer(VERTEX_INDEX);
    }

    @Override
    public int getVertexShaderResId() {
        return R.raw.triangle_vertex_shader;
    }

    @Override
    public int getFragmentShaderResId() {
        return R.raw.triangle_fragment_shader;
    }

    @Override
    public void draw() {
        //获取各属性的位置
        int positionLoc = glGetAttribLocation(program, VERTEX_ATTRIB_POSITION);
        int colorLoc = glGetAttribLocation(program, VERTEX_ATTRIB_COLOR);
        //加载属性数据
        vertexAttribsBuffer.position(0);
        glVertexAttribPointer(positionLoc,
                VERTEX_ATTRIB_POSITION_SIZE,
                GL_FLOAT,
                false,
                STRID,
                vertexAttribsBuffer);
        vertexAttribsBuffer.position(VERTEX_ATTRIB_POSITION_SIZE);
        glVertexAttribPointer(colorLoc,
                VERTEX_ATTRIB_COLOR_SIZE,
                GL_FLOAT,
                false,
                STRID,
                vertexAttribsBuffer);
        //启用属性
        glEnableVertexAttribArray(positionLoc);
        glEnableVertexAttribArray(colorLoc);
        //绘制
        glDrawElements(GL_TRIANGLES, VERTEX_INDEX.length, GL_UNSIGNED_SHORT, vertexIndexBuffer);
        //停用属性
        glDisableVertexAttribArray(positionLoc);
        glDisableVertexAttribArray(colorLoc);
    }
}
