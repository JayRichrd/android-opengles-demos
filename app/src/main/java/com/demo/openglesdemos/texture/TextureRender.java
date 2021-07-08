package com.demo.openglesdemos.texture;

import com.demo.openglesdemos.R;
import com.demo.openglesdemos.base.BaseRender;
import com.demo.openglesdemos.utils.EGLUtil;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES30.GL_FLOAT;
import static android.opengl.GLES30.GL_TEXTURE0;
import static android.opengl.GLES30.GL_TEXTURE_2D;
import static android.opengl.GLES30.GL_TRIANGLES;
import static android.opengl.GLES30.glActiveTexture;
import static android.opengl.GLES30.glBindTexture;
import static android.opengl.GLES30.glDisableVertexAttribArray;
import static android.opengl.GLES30.glDrawArrays;
import static android.opengl.GLES30.glEnableVertexAttribArray;
import static android.opengl.GLES30.glGetAttribLocation;
import static android.opengl.GLES30.glGetUniformLocation;
import static android.opengl.GLES30.glUniform1i;
import static android.opengl.GLES30.glVertexAttribPointer;

public class TextureRender extends BaseRender {
    private static final String VERTEX_ATTRIB_POSITION = "a_vertexCoord";
    private static final int VERTEX_ATTRIB_POSITION_SIZE = 3;
    private static final String VERTEX_ATTRIB_TEXTURE_POSITION = "a_textureCoord";
    private static final int VERTEX_ATTRIB_TEXTURE_POSITION_SIZE = 2;
    private static final String UNIFORM_TEXTURE = "tex";



    private  float[] vertex ={
            -1f,1f,0.0f,
            -1f,-1f,0.0f,
            1f,-1f,0.0f,
            1f,1f,0.0f
    };

    //纹理坐标，（s,t），t坐标方向和顶点y坐标反着
    public float[] textureCoord = {
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,1.0f,
            1.0f,0.0f
    };

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureCoordBuffer;

    public TextureRender() {
        //初始化顶点数据
        initVertexAttrib();
    }

    private void initVertexAttrib() {
        textureCoordBuffer = EGLUtil.getFloatBuffer(textureCoord);
        vertexBuffer = EGLUtil.getFloatBuffer(vertex);
    }

    @Override
    public int getVertexShaderResId() {
        return R.raw.texture_vertex_shader;
    }

    @Override
    public int getFragmentShaderResId() {
        return R.raw.texture_fragtment_shader;
    }

    @Override
    public void draw() {
        int vertexLoc = glGetAttribLocation(program, VERTEX_ATTRIB_POSITION);
        int textureLoc = glGetAttribLocation(program, VERTEX_ATTRIB_TEXTURE_POSITION);

        glVertexAttribPointer(vertexLoc,
                VERTEX_ATTRIB_POSITION_SIZE,
                GL_FLOAT,
                false,
                0,
                vertexBuffer);

        glVertexAttribPointer(textureLoc,
                VERTEX_ATTRIB_TEXTURE_POSITION_SIZE,
                GL_FLOAT,
                false,
                0,
                textureCoordBuffer);

        glEnableVertexAttribArray(vertexLoc);
        glEnableVertexAttribArray(textureLoc);

        //绑定纹理
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, EGLUtil.loadTexture(R.drawable.android_log));
        //Set the sampler texture unit to 0
        glUniform1i(glGetUniformLocation(program, UNIFORM_TEXTURE),0);

        glDrawArrays(GL_TRIANGLE_FAN,0,vertex.length / 3);

        glDisableVertexAttribArray(vertexLoc);
        glDisableVertexAttribArray(textureLoc);
    }
}
