package gles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import lib.opengles.ByteBufferUtil;
import lib.opengles.GL20ShaderUtil;

public class Gles6View extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private float[] cube_vertex_arr;

    private float[] cube_vertex_color;

    private FloatBuffer mCubeVertexBuffer;

    private FloatBuffer mCubeVertexColorBuffer;
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
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        mCubeVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(cube_vertex_arr);
        if (mCubeVertexBuffer != null)
        {
            mCubeVertexBuffer.position(0);
        }

        mCubeVertexColorBuffer = ByteBufferUtil.getNativeFloatBuffer(cube_vertex_color);
        if (mCubeVertexColorBuffer != null)
        {
            mCubeVertexColorBuffer.position(0);
        }

        int vertex_shader = GL20ShaderUtil.getShader(getContext(), GLES20.GL_VERTEX_SHADER, "gles/shader/cube_vertex_shader");
        int fragment_shader = GL20ShaderUtil.getShader(getContext(), GLES20.GL_FRAGMENT_SHADER, "gles/shader/cube_fragment_shader");

        mGLProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mGLProgram, vertex_shader);
        GLES20.glAttachShader(mGLProgram, fragment_shader);
        GLES20.glLinkProgram(mGLProgram);
        GLES20.glUseProgram(mGLProgram);

        int vPosition = GLES20.glGetAttribLocation(mGLProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mCubeVertexBuffer);

        int aColor = GLES20.glGetAttribLocation(mGLProgram, "aColor");
        GLES20.glEnableVertexAttribArray(aColor);
        GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0, mCubeVertexColorBuffer);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        float whScale = (float) width / height;

        int vMatrix = GLES20.glGetUniformLocation(mGLProgram, "vMatrix");
        float[] projectMatrix = new float[16];
        Matrix.frustumM(projectMatrix, 0, -1.0f, 1.0f, - 1.0f /whScale, 1.0f / whScale, 3, 5);
        float[] viewMatrix = new float[16];
        Matrix.setLookAtM(viewMatrix, 0, 5, 5, 10, 0, 0, 0, 0, 1, 0);
        float[] matrix = new float[16];
        Matrix.multiplyMM(matrix, 0, projectMatrix, 0, viewMatrix, 0);
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {

    }
}
