package gles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import lib.util.FileUtil;

public class Gles2View extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private float[] triangleCoords;

    private FloatBuffer triangleVertexBuffer;

    private float[] triangleColor;
    private int mProgram;

    public Gles2View(Context context)
    {
        super(context);

        triangleCoords = new float[]{
                        0.0f, 0.5f,
                        -0.5f, -0.5f,
                        0.5f, -0.5f,
                };

        triangleColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};

        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        ByteBuffer b = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        b.order(ByteOrder.nativeOrder());
        triangleVertexBuffer = b.asFloatBuffer();
        triangleVertexBuffer.put(triangleCoords);
        triangleVertexBuffer.position(0);

        String vertex_resource = FileUtil.getAssetsResource(getResources(), "gles/shader/triangle_vertex_shader");

        int vertex_shader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertex_shader, vertex_resource);
        GLES20.glCompileShader(vertex_shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(vertex_shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0)
        {
            GLES20.glDeleteShader(vertex_shader);
            vertex_shader = 0;
        }

        String fragment_resource = FileUtil.getAssetsResource(getResources(), "gles/shader/triangle_fragment_shader");

        int fragment_shader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragment_shader, fragment_resource);
        GLES20.glCompileShader(fragment_shader);
        compiled = new int[1];
        GLES20.glGetShaderiv(fragment_shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0)
        {
            GLES20.glDeleteShader(vertex_shader);
            fragment_shader = 0;
        }

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertex_shader);
        GLES20.glAttachShader(mProgram, fragment_shader);
        GLES20.glLinkProgram(mProgram);
        GLES20.glUseProgram(mProgram);

        int vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, triangleVertexBuffer);

        int vColor = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(vColor, 1, triangleColor, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

//        GLES20.glDisableVertexAttribArray(vPosition);
    }
}
