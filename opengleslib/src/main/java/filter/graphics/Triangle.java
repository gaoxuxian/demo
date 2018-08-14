package filter.graphics;

import android.content.res.Resources;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import filter.AFilter;
import lib.opengles.ByteBufferUtil;
import lib.opengles.GL20ShaderUtil;
import lib.opengles.GLUtil;
import lib.opengles.VaryTools;

public class Triangle extends AFilter
{
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mVertexColorBuffer;

    private int vPosition;
    private int aColor;
    private int vMatrix;

    private int vPositionSize;

    private int aColorSize;

    // test
    private int program2;

    private FloatBuffer mVertexBuffer2;
    private FloatBuffer mVertexColorBuffer2;

    private int vPosition2;
    private int aColor2;
    private int vMatrix2;

    public Triangle(Resources res)
    {
        super(res);
    }

    @Override
    protected void onInitBaseData()
    {
        float[] vertex = new float[]{
                -1.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f
        };

        vPositionSize = vertex.length / 3;

        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(vertex);

        float[] color = new float[]{
                0.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f
        };

        aColorSize = color.length / 3;

        mVertexColorBuffer = ByteBufferUtil.getNativeFloatBuffer(color);

        float[] vertex2 = new float[]{
                -1.0f, 1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f
        };

        mVertexBuffer2 = ByteBufferUtil.getNativeFloatBuffer(vertex2);

        float[] color2 = new float[]{
                1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f
        };

        mVertexColorBuffer2 = ByteBufferUtil.getNativeFloatBuffer(color2);
    }

    @Override
    protected void onSurfaceCreateSet(GL10 gl, EGLConfig config)
    {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
    }

    @Override
    protected int onCreateProgram()
    {
        // 生成、加载 着色器
        int vertex_shader = GL20ShaderUtil.getShader(getResources(), GLES20.GL_VERTEX_SHADER, "gles/shader/Isosceles_triangle_vertex_shader");
        int fragment_shader = GL20ShaderUtil.getShader(getResources(), GLES20.GL_FRAGMENT_SHADER, "gles/shader/Isosceles_triangle_fragment_shader");

        // 生成 program
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertex_shader);
        GLES20.glAttachShader(program, fragment_shader);
        GLES20.glLinkProgram(program);

        // 获取句柄
        vPosition = GLES20.glGetAttribLocation(program, "vPosition");
        aColor = GLES20.glGetAttribLocation(program, "aColor");
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");

        // 生成 program
        program2 = GLES20.glCreateProgram();
        GLES20.glAttachShader(program2, vertex_shader);
        GLES20.glAttachShader(program2, fragment_shader);
        GLES20.glLinkProgram(program2);

        // 获取句柄
        vPosition2 = GLES20.glGetAttribLocation(program2, "vPosition");
        aColor2 = GLES20.glGetAttribLocation(program2, "aColor");
        vMatrix2 = GLES20.glGetUniformLocation(program2, "vMatrix");

        return program;
    }

    @Override
    protected void onSurfaceChangeSet(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
        float sWidthHeight = (float) width / height;
        VaryTools tools = getMatrixTools();
        tools.frustum(-1f, 1f, -1f / sWidthHeight, 1f / sWidthHeight, 3, 5);
        tools.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
    }

    @Override
    protected void onBe4DrawSet(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    protected void onDrawSelf()
    {
        // 第一个 program
        GLES20.glUseProgram(mGLProgram);

        setMatrix(getMatrixTools().getFinalMatrix());
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, getMatrix(), 0);

        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, vPositionSize, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(aColor);
        GLES20.glVertexAttribPointer(aColor, aColorSize, GLES20.GL_FLOAT, false, 0, mVertexColorBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vPositionSize);

        // test 第二个 program
        GLES20.glUseProgram(program2);

        GLES20.glUniformMatrix4fv(vMatrix2, 1, false, GLUtil.getOpenGLUnitMatrix(), 0);

        GLES20.glEnableVertexAttribArray(vPosition2);
        GLES20.glVertexAttribPointer(vPosition2, vPositionSize, GLES20.GL_FLOAT, false, 0, mVertexBuffer2);

        GLES20.glEnableVertexAttribArray(aColor2);
        GLES20.glVertexAttribPointer(aColor2, aColorSize, GLES20.GL_FLOAT, false, 0, mVertexColorBuffer2);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vPositionSize);

        GLES20.glDisableVertexAttribArray(vPosition);
        GLES20.glDisableVertexAttribArray(aColor);

        GLES20.glDisableVertexAttribArray(vPosition2);
        GLES20.glDisableVertexAttribArray(aColor2);
    }
}
