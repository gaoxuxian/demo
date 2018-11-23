#version 300 es
precision mediump float;

uniform sampler2D vTexture;
in vec2 aCoordinate;
out vec4 o_fragColor;

void main(){
    o_fragColor = texture(vTexture, aCoordinate);
}