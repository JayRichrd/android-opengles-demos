package com.demo.openglesdemos.shape;

import com.demo.openglesdemos.R;
import com.demo.openglesdemos.base.BaseRender;
import com.demo.openglesdemos.utils.EGLUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES30.*;

public class CircleRender extends BaseRender {
    private static final String VERTEX_ATTRIB_POSITION = "vPosition";
    private static final int VERTEX_ATTRIB_POSITION_SIZE = 3;
    private static final String VERTEX_ATTRIB_COLOR = "aColor";

    private float radius = 1.0f;//圆半径
    private int spanNum = 360;//圆切分成的扇形个数
    private float z = 0.0f;//z 坐标为0

    private  float[] vertex;
    private float[] color = {1.0f, 0.5f, 0.3f, 1.0f};

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    public CircleRender() {
        //初始化顶点数据
        initVertexAttrib();
    }

    private void initVertexAttrib() {
        colorBuffer = EGLUtil.getFloatBuffer(color);

        vertex = initVertex();
        vertexBuffer = EGLUtil.getFloatBuffer(vertex);
    }

    private float[] initVertex(){
        List<Float> vertexData = new ArrayList<>();
        //圆心
        vertexData.add(0.0f);
        vertexData.add(0.0f);
        vertexData.add(z);
        //扇形顶点
        float span = 360.0f / spanNum;
        for (float i = 0; i < 360 + span; i += span){
            float x = radius * (float) Math.cos(i * Math.PI/180f);
            float y = radius * (float) Math.sin(i * Math.PI/180f);
            vertexData.add(x);
            vertexData.add(y);
            vertexData.add(z);
        }
        float[] tmp = new float[vertexData.size()];
        for (int i = 0; i < vertexData.size(); i++){
            tmp[i] = vertexData.get(i);
        }
        return tmp;
    }

    @Override
    public int getVertexShaderResId() {
        return R.raw.circle_vertex_shader;
    }

    @Override
    public int getFragmentShaderResId() {
        return R.raw.circle_fragment_shader;
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
