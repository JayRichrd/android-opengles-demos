#version 300 es
precision mediump float;

uniform vec4 aColor;

out vec4 fragColor;

void main()
{
    fragColor = aColor;
}