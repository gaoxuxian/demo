#version 300 es
precision mediump float;

uniform sampler2D vTexture;
uniform float vAlpha;
uniform float vMask;
in vec2 aCoordinate;
out vec4 o_fragColor;

void main(){

     if (vMask == 1.0){
        o_fragColor = vec4(1.0, 1.0, 1.0, 1.0);
     }
     else {
        vec4 picColor = texture(vTexture, aCoordinate);
        o_fragColor = vec4(picColor.r, picColor.g, picColor.b, vAlpha*picColor.a);
     }
}