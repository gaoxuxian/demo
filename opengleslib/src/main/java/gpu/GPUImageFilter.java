package gpu;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;

import lib.opengles.R;
import util.ByteBufferUtil;
import util.GLUtil;
import util.VaryTools;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/05.
 * <p>
 * 生命周期: onSurfaceCreated() --> onSurfaceChanged()
 * 如需管理 FBO : initFrameBuffer()
 */
public class GPUImageFilter
{
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private Context mContext;
    private VaryTools mMatrixTools;

    // 句柄
    protected int vPositionHandle;
    protected int vCoordinateHandle;
    protected int vMatrixHandle;
    protected int vTextureHandle;

    private String mVertexStr;
    private String mFragmentStr;

    protected FloatBuffer mVertexBuffer;
    protected ShortBuffer mVertexIndexBuffer;
    protected FloatBuffer mTextureIndexBuffer;

    private int mProgram;
    private int mVertexShader;
    private int mFragmentShader;

    // fbo
    protected AbsFboMgr mFrameBufferMgr;

    protected final TaskWrapper mTasksMgr;

    public GPUImageFilter(Context context)
    {
        this(context, GLUtil.readShaderFromRaw(context, R.raw.image_default_vertex), GLUtil.readShaderFromRaw(context, R.raw.image_default_fragment));
    }

    public GPUImageFilter(Context context, String vertex, String fragment)
    {
        if (!GLUtil.checkSupportGlVersion(context, 2.0f))
        {
            throw new RuntimeException("手机系统所支持的 Open GL ES 版本低于 2.0, Filter 创建失败!!!");
        }

        mContext = context;
        mVertexStr = vertex;
        mFragmentStr = fragment;
        mMatrixTools = new VaryTools();
        mTasksMgr = new TaskWrapper();

        onInitBaseData();
    }

    protected void onInitBaseData()
    {

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

        mVertexShader = GLUtil.createShader(GLES20.GL_VERTEX_SHADER, mVertexStr);
        mFragmentShader = GLUtil.createShader(GLES20.GL_FRAGMENT_SHADER, mFragmentStr);
        mProgram = GLUtil.createAndLinkProgram(mVertexShader, mFragmentShader);

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
        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.VERTEX_SQUARE);
        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(GLConstant.VERTEX_INDEX);
        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.TEXTURE_INDEX);
    }

    public void onSurfaceChanged(int width, int height)
    {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    protected void preDrawSteps1DataBuffer()
    {
        // 绑定顶点坐标缓冲
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPositionHandle);

        // 绑定纹理坐标缓冲
        mTextureIndexBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureIndexBuffer);
        GLES20.glEnableVertexAttribArray(vCoordinateHandle);
    }

    protected void preDrawSteps2BindTexture(int textureID)
    {
        // 绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureType(), textureID);
        GLES20.glUniform1i(vTextureHandle, 0);
    }

    protected void preDrawSteps3Matrix()
    {
        // 矩阵变换
        VaryTools matrix = getMatrix();
        matrix.pushMatrix();
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    protected void preDrawSteps4Other()
    {

    }

    protected void draw(int textureID)
    {
        GLES20.glViewport(0, 0, getSurfaceW(), getSurfaceH());
        GLES20.glUseProgram(getProgram());

        preDrawSteps1DataBuffer();
        preDrawSteps2BindTexture(textureID);
        preDrawSteps3Matrix();
        preDrawSteps4Other();

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

        afterDraw();
    }

    protected void afterDraw()
    {
        GLES20.glDisableVertexAttribArray(vPositionHandle);
        GLES20.glDisableVertexAttribArray(vTextureHandle);
        GLES20.glBindTexture(getTextureType(), 0);
    }

    public void onDrawFrame(int textureID)
    {
        if (!GLES20.glIsProgram(getProgram()))
        {
            return;
        }
        draw(textureID);
    }

    public int onDrawBuffer(int textureID)
    {
        if (!GLES20.glIsProgram(getProgram()))
        {
            return textureID;
        }

        if (mFrameBufferMgr != null)
        {
            mFrameBufferMgr.bindNext();
            mFrameBufferMgr.clearColor(true, true, true, true, true);
            mFrameBufferMgr.clearDepth(true, true);
            mFrameBufferMgr.clearStencil(true, true);

            draw(textureID);
            mFrameBufferMgr.unbind();
            return mFrameBufferMgr.getCurrentTextureId();
        }

        return textureID;
    }

    protected int getTextureType()
    {
        return GLES20.GL_TEXTURE_2D;
    }

    protected int createFrameBufferSize()
    {
        return 1;
    }

    protected boolean needInitMsaaFbo()
    {
        return true;
    }

    public void initFrameBuffer(int width, int height)
    {
        this.initFrameBuffer(width, height, true, true, true);
    }

    /**
     * 3.0 可选择生成 抗锯齿fbo 或者 普通fbo, 2.0 只生成 普通fbo
     * <p>
     * needInitMsaaFbo() true --> 抗锯齿 render buffer
     * <p>
     * needInitMsaaFbo() false --> 普通 2d 纹理, 本身不能抗锯齿
     * @param width
     * @param height
     */
    public final void initFrameBuffer(int width, int height, boolean color, boolean depth, boolean stencil)
    {
        if (mFrameBufferMgr != null)
        {
            if (width != mFrameBufferMgr.getBufferWidth() || height != mFrameBufferMgr.getBufferHeight())
            {
                mFrameBufferMgr.destroy();
                mFrameBufferMgr = null;
            }
        }

        if (mFrameBufferMgr == null)
        {
            if (GLUtil.checkSupportGlVersion(getContext(), 3.0f))
            {
                if (needInitMsaaFbo())
                {
                    mFrameBufferMgr = new MsaaFboMgr(width, height, createFrameBufferSize(), depth, stencil);
                }
                else
                {
                    mFrameBufferMgr = new TextureFboMgr30(width, height, createFrameBufferSize(), color, depth, stencil);
                }
            }
            else
            {
                mFrameBufferMgr = new TextureFboMgr20(width, height, createFrameBufferSize(), color, depth, stencil);
            }
        }
    }

    /**
     * 手动检查当前 FrameBuffer 里的缓冲区是否需要重新挂载
     * @param width
     * @param height
     */
    protected final void checkFrameBufferReMount(int width, int height)
    {
        if (mFrameBufferMgr != null)
        {
            mFrameBufferMgr.reMount(width, height);
        }
    }

    public Context getContext()
    {
        return mContext;
    }

    public VaryTools getMatrix()
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

    public void blendEnable(boolean enable)
    {
        if (enable)
        {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
            GLES20.glBlendFuncSeparate(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);
        }
        else
        {
            GLES20.glDisable(GLES20.GL_BLEND);
        }
    }

    protected void queueRunnable(final AbsTask runnable)
    {
        synchronized (mTasksMgr)
        {
            mTasksMgr.queueRunnable(runnable);
        }
    }

    /**
     * 同步task
     *
     * @param runAll 是否将队列内任务全部执行
     */
    protected void runTask(boolean runAll)
    {
        if (mTasksMgr != null && !mTasksMgr.isClear())
        {
            if (runAll)
            {
                while (!mTasksMgr.isClear() && mTasksMgr.getTaskSize() != 0)
                {
                    mTasksMgr.runTask();
                }
            }
            else
            {
                mTasksMgr.runTask();
            }
        }
    }

    /**
     * 异步task
     */
    protected void startTask()
    {
        if (mTasksMgr != null && !mTasksMgr.isClear())
        {
            mTasksMgr.startTask();
        }
    }

    public void destroy()
    {
        mContext = null;

        if (mFrameBufferMgr != null)
        {
            mFrameBufferMgr.destroy();
        }

        if (mTasksMgr != null)
        {
            mTasksMgr.clearTask();
        }
    }
}
