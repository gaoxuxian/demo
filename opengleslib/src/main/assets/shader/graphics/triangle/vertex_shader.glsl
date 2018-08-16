attribute vec4 vPosition;
varying vec4 vColor;
attribute vec4 aColor;
uniform mat4 vMatrix;

void main() {
    gl_Position = vMatrix * vPosition;
    vColor = aColor;
}