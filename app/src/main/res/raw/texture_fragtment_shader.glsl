#version 300 es
precision mediump float;

in vec2 textureCoord;
uniform sampler2D tex;

out vec4 outColor;

void main(){
    outColor = texture(tex, textureCoord);
}