#version 300 es
precision mediump float;
// 从上一个shader中获取输入
in vec2 v_texCoord;
out vec4 out_Color;
// 采样器
uniform sampler2D s_texture;

void main(){
    vec4 final_color = texture(s_texture, v_texCoord);
    out_Color = vec4(final_color.g, final_color.g, final_color.g, final_color.a);
}