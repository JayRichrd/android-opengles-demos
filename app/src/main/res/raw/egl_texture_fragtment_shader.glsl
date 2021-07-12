#version 300 es
precision mediump float;
// 从上一个shader中获取输入
in vec2 v_texCoord;
out vec4 out_Color;
// 纹理单元(采样器)
uniform sampler2D s_texture;

void main(){
    out_Color = texture(s_texture, v_texCoord);
//    gl_FragColor = texture(s_texture, v_texCoord);
}