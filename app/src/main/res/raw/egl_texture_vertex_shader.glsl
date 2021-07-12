#version 300 es

layout (location = 0) in vec4 a_Position;
layout (location = 1) in vec2 a_texCoord;
// 作为下一个shader的输入
out vec2 v_texCoord;

void main()
{
    gl_Position = a_Position;
    v_texCoord = a_texCoord;
}