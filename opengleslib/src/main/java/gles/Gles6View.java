package gles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import util.ByteBufferUtil;
import util.GLES20Util;

public class Gles6View extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private float[] cube_vertex_arr;

    private float[] cube_vertex_color;

    private FloatBuffer mCubeVertexBuffer;

    private FloatBuffer mCubeVertexColorBuffer;

    private short[] cube_vertex_index;

    private ShortBuffer mCubeVertexIndexBuffer;

    private int mGLProgram;

    public Gles6View(Context context)
    {
        super(context);

        cube_vertex_arr = new float[]{
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f
        };

        cube_vertex_color = new float[]{
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,

                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f
        };

        cube_vertex_index = new short[]{
                0, 1, 2,
                0, 2, 3,
                1, 5, 6,
                1, 2, 6,
                5, 4, 7,
                5, 6, 7,
                4, 7, 0,
                0, 7, 3,
                0, 4, 5,
                0, 5, 1,
                3, 7, 6,
                3, 6, 2
        };

        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        //开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        mCubeVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(cube_vertex_arr);

        mCubeVertexColorBuffer = ByteBufferUtil.getNativeFloatBuffer(cube_vertex_color);

        mCubeVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(cube_vertex_index);

        int vertex_shader = GLES20Util.sGetShader(getContext(), GLES20.GL_VERTEX_SHADER, "shader/default_vertex_shader.glsl");
        int fragment_shader = GLES20Util.sGetShader(getContext(), GLES20.GL_FRAGMENT_SHADER, "shader/default_fragment_shader.glsl");

        mGLProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mGLProgram, vertex_shader);
        GLES20.glAttachShader(mGLProgram, fragment_shader);
        GLES20.glLinkProgram(mGLProgram);
        GLES20.glUseProgram(mGLProgram);

        if (mCubeVertexBuffer != null)
        {
            int vPosition = GLES20.glGetAttribLocation(mGLProgram, "vPosition");
            GLES20.glEnableVertexAttribArray(vPosition);
            GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mCubeVertexBuffer);
        }

        if (mCubeVertexColorBuffer != null)
        {
            int aColor = GLES20.glGetAttribLocation(mGLProgram, "aColor");
            GLES20.glEnableVertexAttribArray(aColor);
            GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0, mCubeVertexColorBuffer);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        float whScale = (float) width / height;

        int vMatrix = GLES20.glGetUniformLocation(mGLProgram, "vMatrix");
        float[] projectMatrix = new float[16];
        Matrix.frustumM(projectMatrix, 0, -1.0f, 1.0f, - 1.0f /whScale, 1.0f / whScale, 3, 20);
        float[] viewMatrix = new float[16];
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 4, 0, 0, 0, 0, 1, 0);
        float[] matrix = new float[16];
        Matrix.multiplyMM(matrix, 0, projectMatrix, 0, viewMatrix, 0);
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (mCubeVertexIndexBuffer != null)
        {
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, cube_vertex_index.length, GLES20.GL_UNSIGNED_SHORT, mCubeVertexIndexBuffer);
        }
    }
}
