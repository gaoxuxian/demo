package filter.graphics;

import android.content.res.Resources;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;

import filter.AFilter;
import util.ByteBufferUtil;
import util.GLES20Util;
import util.VaryTools;

/**
 * 尝试一个 Filter 分别连接两个 program 去画 两个彩色三角形
 */
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
    protected void onSurfaceCreateSet(EGLConfig config)
    {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
    }

    @Override
    protected int onCreateProgram()
    {
        // 生成、加载 着色器
        int vertex_shader = GLES20Util.sGetShader(getResources(), GLES20.GL_VERTEX_SHADER, "shader/graphics/triangle/vertex_shader.glsl");
        int fragment_shader = GLES20Util.sGetShader(getResources(), GLES20.GL_FRAGMENT_SHADER, "shader/graphics/triangle/fragment_shader.glsl");

        // 生成 program
        int program = GLES20Util.sCreateAndLinkProgram(vertex_shader, fragment_shader);

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
    protected void onSurfaceChangeSet(int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
        float sWidthHeight = (float) width / height;
        VaryTools tools = getMatrixTools();
        tools.frustum(-1f, 1f, -1f / sWidthHeight, 1f / sWidthHeight, 3, 5);
        tools.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        setMatrix(tools.getFinalMatrix());
    }

    @Override
    protected void onBe4DrawSet()
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    protected void onDrawSelf()
    {
        // 第一个 program
        GLES20.glUseProgram(getGLProgram());

        GLES20.glUniformMatrix4fv(vMatrix, 1, false, getMatrix(), 0);

        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, vPositionSize, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(aColor);
        GLES20.glVertexAttribPointer(aColor, aColorSize, GLES20.GL_FLOAT, false, 0, mVertexColorBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vPositionSize);

        // test 第二个 program
        GLES20.glUseProgram(program2);

        GLES20.glUniformMatrix4fv(vMatrix2, 1, false, getMatrixTools().getOpenGLUnitMatrix(), 0);

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
