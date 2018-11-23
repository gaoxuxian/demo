package gpu.filter;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;

import gpu.AbsTask;
import gpu.GLConstant;
import gpu.TaskWrapper;
import util.ByteBufferUtil;
import util.GLES20Util;
import util.VaryTools;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/20.
 */
public class GPUImageFilter
{
    protected static final String DEFAULT_VERTEX_SHADER =
            "attribute vec4 vPosition;\n" +
            "attribute vec2 vCoordinate;\n" +
            "uniform mat4 vMatrix;\n" +
            "\n" +
            "varying vec2 aCoordinate;\n" +
            "\n" +
            "void main(){\n" +
            "    gl_Position = vMatrix * vPosition;\n" +
            "    aCoordinate = vCoordinate;\n" +
            "}";

    protected static final String DEFAULT_FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "\n" +
            "uniform sampler2D vTexture;\n" +
            "varying vec2 aCoordinate;\n" +
            "\n" +
            "void main(){\n" +
            "    gl_FragColor = texture2D(vTexture,aCoordinate);\n" +
            "}";

    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private int mProgram;

    private Context mContext;
    private VaryTools mMatrixTools;

    protected int vPositionHandle;
    protected int vCoordinateHandle;
    protected int vMatrixHandle;
    protected int vTextureHandle;

    private String mVertexStr;
    private String mFragmentStr;

    protected FloatBuffer mVertexBuffer;
    protected ShortBuffer mVertexIndexBuffer;
    protected FloatBuffer mTextureIndexBuffer;

    protected final TaskWrapper mTaskWrapper;

    protected int mVertexShader;
    protected int mFragmentShader;

    public GPUImageFilter(Context context)
    {
        this(context, DEFAULT_VERTEX_SHADER, DEFAULT_FRAGMENT_SHADER);
    }

    public GPUImageFilter(Context context, String vertex, String fragment)
    {
        mContext = context;
        mVertexStr = vertex;
        mFragmentStr = fragment;
        mMatrixTools = new VaryTools();
        mTaskWrapper = new TaskWrapper();
    }

    public void onSurfaceCreated(EGLConfig config)
    {
        if (GLES20.glIsProgram(mProgram))
        {
            if (GLES20.glIsShader(mVertexShader))
            {
                GLES20.glDetachShader(mProgram, mVertexShader);
                GLES20.glDeleteShader(mVertexShader);
                mVertexShader = 0;
            }

            if (GLES20.glIsShader(mFragmentShader))
            {
                GLES20.glDetachShader(mProgram, mFragmentShader);
                GLES20.glDeleteShader(mFragmentShader);
                mFragmentShader = 0;
            }
            GLES20.glDeleteProgram(mProgram);
            mProgram = 0;
        }

        onInitBufferData();

        mVertexShader = GLES20Util.sGetShader(GLES20.GL_VERTEX_SHADER, mVertexStr);
        mFragmentShader = GLES20Util.sGetShader(GLES20.GL_FRAGMENT_SHADER, mFragmentStr);

        mProgram = GLES20Util.sCreateAndLinkProgram(mVertexShader, mFragmentShader);

        onInitProgramHandle();
    }

    protected void onInitProgramHandle()
    {
        vPositionHandle = GLES20.glGetAttribLocation(getProgram(), "vPosition");
        vCoordinateHandle = GLES20.glGetAttribLocation(getProgram(), "vCoordinate");

        vMatrixHandle = GLES20.glGetUniformLocation(getProgram(), "vMatrix");
        vTextureHandle = GLES20.glGetUniformLocation(getProgram(), "vTexture");
    }

    protected void onInitBufferData()
    {
        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.VERTEX_CUBE);
        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(GLConstant.VERTEX_INDEX);
        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.TEXTURE_INDEX);
    }

    public void onSurfaceChanged(int width, int height)
    {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    public int onDraw(int textureId)
    {
        if (!GLES20.glIsProgram(getProgram()) || !GLES20.glIsTexture(textureId))
        {
            return textureId;
        }

        GLES20.glViewport(0, 0, getSurfaceW(), getSurfaceH());
        GLES20.glUseProgram(getProgram());
        mTaskWrapper.runTask();

        // 绑定顶点坐标缓冲
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPositionHandle);

        // 绑定纹理坐标缓冲
        mTextureIndexBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureIndexBuffer);
        GLES20.glEnableVertexAttribArray(vCoordinateHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureType(), textureId);
        GLES20.glUniform1i(vTextureHandle, 0);

        VaryTools matrix = getMatrix();
        matrix.pushMatrix();
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();

        onDrawArraysPre();
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

        GLES20.glDisableVertexAttribArray(vPositionHandle);
        GLES20.glDisableVertexAttribArray(vTextureHandle);
        onDrawArraysAfter();

        GLES20.glBindTexture(getTextureType(), 0);
        return textureId;
    }

    protected void onDrawArraysPre()
    {

    }

    protected void onDrawArraysAfter()
    {

    }

    protected int getTextureType()
    {
        return GLES20.GL_TEXTURE_2D;
    }

    protected Context getContext()
    {
        return mContext;
    }

    protected VaryTools getMatrix()
    {
        return mMatrixTools;
    }

    protected int getProgram()
    {
        return mProgram;
    }

    public int getSurfaceW()
    {
        return mSurfaceWidth;
    }

    public int getSurfaceH()
    {
        return mSurfaceHeight;
    }

    public void onClear()
    {
        mContext = null;
        mTaskWrapper.clearTask();
    }

    protected void queueRunnable(final AbsTask runnable)
    {
        synchronized (mTaskWrapper)
        {
            mTaskWrapper.queueRunnable(runnable);
        }
    }
}
