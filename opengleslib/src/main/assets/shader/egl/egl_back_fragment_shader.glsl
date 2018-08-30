precision mediump float;

uniform sampler2D vTexture;
varying vec2 aCoordinate;

void main(){
    vec4 color=texture2D( vTexture, aCoordinate);
        float rgb=color.g;
        vec4 c=vec4(rgb,rgb,rgb,color.a);
        gl_FragColor = c;
}