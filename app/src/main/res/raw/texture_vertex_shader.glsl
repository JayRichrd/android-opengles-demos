#version 300 es

in vec4 a_vertexCoord;
uniform mat4 matrix;
in vec2 a_textureCoord;

out vec2 textureCoord;

void main()
{
    gl_Position = matrix * a_vertexCoord;
    textureCoord = a_textureCoord;
}