#version 300 es

in vec4 vPosition;
in vec4 aColor;
uniform mat4 matrix;

out vec4 vColor;

void main()
{
    gl_Position = matrix * vPosition;
    vColor = aColor;
}