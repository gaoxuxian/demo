package gles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import util.BufferUtil;
import util.GLES20Util;

public class Gles3View extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private float[] isosceles_triangle_coords;
    private float[] isosceles_triangle_color;
    private float[] isosceles_triangle_color_arr;

    private FloatBuffer mIsoscelesTriangleBuffer;
    private FloatBuffer mIsoscelesTriangleColorBuffer;
    private int program;

    public Gles3View(Context context)
    {
        super(context);

        isosceles_triangle_coords = new float[]{
//                        -0.5f, 0.5f, 0f,
//                        -0.5f, -0.5f, 0f,
//                        0.5f, -0.5f, 0f
                -1f, 1f, 0f,
                -1f, -1f, 0f,
                1f, -1f, 0f
                };

        isosceles_triangle_color = new float[]{1.0f, 1.0f, 1.0f, 1.0f};

        isosceles_triangle_color_arr = new float[]{
                    1.0f, 0f, 0f, 1.0f,
                    0f, 1.0f, 0f, 1.0f,
                    0f, 0f, 1.0f, 1.0f
                };

        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        mIsoscelesTriangleBuffer = BufferUtil.getNativeFloatBuffer(isosceles_triangle_coords);

        mIsoscelesTriangleColorBuffer = BufferUtil.getNativeFloatBuffer(isosceles_triangle_color_arr);

        int vertex_shader = GLES20Util.sGetShader(getContext(), GLES20.GL_VERTEX_SHADER, "gles/shader/Isosceles_triangle_vertex_shader");

        int fragment_shader = GLES20Util.sGetShader(getContext(), GLES20.GL_FRAGMENT_SHADER, "gles/shader/Isosceles_triangle_fragment_shader");

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertex_shader);
        GLES20.glAttachShader(program, fragment_shader);
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);

        int vPosition = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mIsoscelesTriangleBuffer);

//        int vColor = GLES20.glGetUniformLocation(program, "vColor");
//        GLES20.glUniform4fv(vColor, 1, isosceles_triangle_color, 0);

        int aColor = GLES20.glGetAttribLocation(program, "aColor");
        GLES20.glEnableVertexAttribArray(aColor);
        GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0, mIsoscelesTriangleColorBuffer);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        /*
            视口是一个矩形窗口区域。是OpenGL渲染操作最终显示的地方

            x, y 是渲染的起点，0，0 是屏幕左上角
         */
        GLES20.glViewport(0, 0, width, height);

        int vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        float[] matrix = new float[16];
        float sWidthHeight = width / (float) height;
//        GLES20Util.getFrustumM(matrix, width, height, width, height);

        /*
            frustumM(float[] m, int offset, // 用于接收矩阵信息的数组， offset 从哪里开始接收
            float left, float right, float bottom, float top, // 近平面左右下上部与中心点的距离
            float near, float far) // 近平面和元平面与摄像机观察点的距离
         */
        float[] f = new float[16];
//        Matrix.frustumM(f, 0, -sWidthHeight, sWidthHeight, -1f, 1f, 3.0f, 5.0f);
        Matrix.frustumM(f, 0, -1f, 1f, -1f / sWidthHeight, 1f / sWidthHeight, 3.0f, 5.0f);
        float[] m = new float[16];
        Matrix.setLookAtM(m, 0, 0f, 0f, 3.0f, 0f, 0f, 0f, 0f, 1f, 0f);
        Matrix.multiplyMM(matrix, 0, f, 0, m, 0);
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
