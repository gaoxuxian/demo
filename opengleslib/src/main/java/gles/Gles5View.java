package gles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import lib.opengles.ByteBufferUtil;
import lib.opengles.GL20ShaderUtil;
import lib.opengles.GLUtil;

public class Gles5View extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private float[] circle_vertex_arr;

    private float[] circle_fragment_color;

    private FloatBuffer mCircleVertexBuffer;
    private int mGLProgram;

    public Gles5View(Context context)
    {
        super(context);

        ArrayList<Float> data = new ArrayList<>();
        // 圆心
        data.add(0.0f);
        data.add(0.0f);
        data.add(0.0f);

        float angDegSpan = 360f / 360f;

        for (float i = 0; i < 360 + angDegSpan; i += angDegSpan)
        {
            data.add((float) (0.5f * Math.sin(i * Math.PI / 180f)));
            data.add((float) (0.5f * Math.cos(i * Math.PI / 180f)));
            data.add(0.0f);
        }

        int size = data.size();
        circle_vertex_arr = new float[size];

        for (int i = 0; i < size; i++)
        {
            circle_vertex_arr[i] = data.get(i);
        }

        circle_fragment_color = new float[]{0.0f, 1.0f, 0.0f, 1.0f};

        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        mCircleVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(circle_vertex_arr);
        if (mCircleVertexBuffer != null)
        {
            mCircleVertexBuffer.position(0);
        }

        int vertex_shader = GL20ShaderUtil.getShader(getContext(), GLES20.GL_VERTEX_SHADER, "gles/shader/circle_vertex_shader");
        int fragment_shader = GL20ShaderUtil.getShader(getContext(), GLES20.GL_FRAGMENT_SHADER, "gles/shader/circle_fragment_shader");

        mGLProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mGLProgram, vertex_shader);
        GLES20.glAttachShader(mGLProgram, fragment_shader);
        GLES20.glLinkProgram(mGLProgram);
        GLES20.glValidateProgram(mGLProgram);
        GLES20.glUseProgram(mGLProgram);

        int vPosition = GLES20.glGetAttribLocation(mGLProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mCircleVertexBuffer);

        int vColor = GLES20.glGetUniformLocation(mGLProgram, "vColor");
        GLES20.glUniform4fv(vColor, 1, circle_fragment_color, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        int vMatrix = GLES20.glGetUniformLocation(mGLProgram, "vMatrix");
        float[] matrix = new float[16];
        GLUtil.getFrustumM(matrix, width, height, width, height);
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, circle_vertex_arr.length/3);
    }
}
