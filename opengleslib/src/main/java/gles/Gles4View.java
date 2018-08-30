package gles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import util.ByteBufferUtil;
import util.GLES20Util;

public class Gles4View extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private float[] square_vertex_arr;

    private FloatBuffer mSquareVertexBuffer;

    private short[] square_vertex_index_arr;

    private ShortBuffer mSquareVertexIndexBuffer;

    private float[] square_fragment_color;
    private int program;

    public Gles4View(Context context)
    {
        super(context);

        setEGLContextClientVersion(2);
        setRenderer(this);

        square_vertex_arr = new float[]{
                -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
        };

        square_vertex_index_arr = new short[]{
                0, 1, 2,
                0, 2, 3
        };

        square_fragment_color = new float[]{
                1.0f, 1.0f, 1.0f, 1.0f
        };
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        mSquareVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(square_vertex_arr);

        mSquareVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(square_vertex_index_arr);

        int vertex_shader = GLES20Util.sGetShader(getContext(), GLES20.GL_VERTEX_SHADER, "shader/default_vertex_shader.glsl");
        int fragment_shader = GLES20Util.sGetShader(getContext(), GLES20.GL_FRAGMENT_SHADER, "shader/default_fragment_shader.glsl");

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertex_shader);
        GLES20.glAttachShader(program, fragment_shader);
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);

        int vPosition = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mSquareVertexBuffer);

        int vColor = GLES20.glGetUniformLocation(program, "vColor");
        GLES20.glUniform4fv(vColor, 1, square_fragment_color, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        float[] matrix = new float[16];
        GLES20Util.sGetFrustumM(matrix, width, height, width, height);
        int vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mSquareVertexIndexBuffer);
    }
}
