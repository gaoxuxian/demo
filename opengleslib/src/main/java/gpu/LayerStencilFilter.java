package gpu;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import lib.opengles.R;
import util.GLES20Util;
import util.GLUtil;
import util.VaryTools;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/7.
 */
public class LayerStencilFilter extends GPUImageFilter
{
    private int mTextureAlphaHandle;
    private int mVMaskHandle;
    private boolean mDrawMask;
    private boolean mDrawBg;
    private boolean mDrawLayer;

    private float mTextureAlpha;

    private int[] mTextureIDArr;

    private LayerImageTask mLayerImgTask;
    private boolean mNeedReloadBmp;

    public LayerStencilFilter(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.stencil_vertex), GLUtil.readShaderFromRaw(context, R.raw.stencil_fragment));
    }

    @Override
    protected void onInitBaseData()
    {
        mTextureIDArr = new int[1];

        mLayerImgTask = new LayerImageTask(getContext(), new LayerImageTask.Listener()
        {
            @Override
            public void onStart()
            {
                if (mNeedReloadBmp)
                {
                    if (GLES20.glIsTexture(mTextureIDArr[0]))
                    {
                        GLES20.glDeleteTextures(1, mTextureIDArr, 0);
                        mTextureIDArr[0] = 0;
                    }
                    mNeedReloadBmp = false;
                }
            }

            @Override
            public void onBitmapSucceed(Bitmap bitmap)
            {
                if (!GLES20.glIsTexture(mTextureIDArr[0]) && bitmap != null)
                {
                    int[] textureIdArr = new int[1];
                    GLES20.glGenTextures(textureIdArr.length, textureIdArr, 0);
                    mTextureIDArr[0] = textureIdArr[0];

                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIdArr[0]);
                    GLES20Util.sBindTextureParams();
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                }
            }
        });

        mLayerImgTask.setBitmapResource(R.drawable.open_test_5);
        queueRunnable(mLayerImgTask);
    }

    @Override
    protected void onInitProgramHandle()
    {
        super.onInitProgramHandle();

        mTextureAlphaHandle = GLES20.glGetUniformLocation(getProgram(), "vAlpha");
        mVMaskHandle = GLES20.glGetUniformLocation(getProgram(), "vMask");
    }

    @Override
    protected void preDrawSteps2BindTexture(int textureID)
    {
        if (GLES20.glIsTexture(textureID))
        {
            super.preDrawSteps2BindTexture(textureID);
        }
    }

    @Override
    protected void preDrawSteps3Matrix()
    {
        VaryTools matrix = getMatrix();
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        matrix.pushMatrix();

        if (mDrawBg)
        {
            matrix.frustum(-1, 1, -1, 1, 3, 7);
            matrix.scale(1f, -1f, 1f);
        }
        else if (mDrawMask)
        {
            float vs = (float) getSurfaceH() / getSurfaceW();
            matrix.frustum(-1, 1, -vs, vs, 3, 7);
            // 前乘
            matrix.scale(0.2f, 0.2f, 1);
        }
        else if (mDrawLayer)
        {
            float vs = (float) getSurfaceH() / getSurfaceW();
            matrix.frustum(-1, 1, -vs, vs, 3, 7);
            // 前乘
            matrix.scale(1, 1, 1);
        }

        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    protected void preDrawSteps4Other()
    {
        blendEnable(true);

        if (mDrawMask)
        {
            mTextureAlpha = 1f;
            GLES20.glEnable(GLES20.GL_STENCIL_TEST);
            GLES20.glStencilFunc(GLES20.GL_ALWAYS, 1, 0xFF);
            GLES20.glStencilOp(GLES20.GL_KEEP, GLES20.GL_KEEP, GLES20.GL_REPLACE);
            GLES20.glUniform1f(mVMaskHandle, 1);
        }
        else if (mDrawLayer)
        {
            mTextureAlpha = 1f;
            GLES20.glEnable(GLES20.GL_STENCIL_TEST);
            GLES20.glStencilFunc(GLES20.GL_EQUAL, 0, 0xFF);
            GLES20.glStencilOp(GLES20.GL_KEEP, GLES20.GL_KEEP, GLES20.GL_KEEP);
            GLES20.glUniform1f(mVMaskHandle, 0);
        }

        GLES20.glUniform1f(mTextureAlphaHandle, mDrawBg ? 1 : mTextureAlpha);
    }

    @Override
    protected void afterDraw()
    {
        blendEnable(false);

        // if (!mDrawBg)
        // {
        //     if (GLES20.glIsTexture(mTextureIDArr[0]))
        //     {
        //         GLES20.glDeleteTextures(1, mTextureIDArr, 0);
        //         mTextureIDArr[0] = 0;
        //     }
        // }

        if (mDrawLayer)
        {
            GLES20.glDisable(GLES20.GL_STENCIL_TEST);
        }
    }

    @Override
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

            mDrawBg = true;
            draw(textureID);
            mDrawBg = false;

            mDrawMask = true;
            mFrameBufferMgr.clearColor(false, false, false, false, false);
            mFrameBufferMgr.clearStencil(true, true);
            draw(0);
            mDrawMask = false;

            mDrawLayer = true;
            runTask(true);
            mFrameBufferMgr.clearColor(false, true, true, true, true);
            draw(mTextureIDArr[0]);
            mDrawLayer = false;

            mFrameBufferMgr.unbind();
            return mFrameBufferMgr.getCurrentTextureId();
        }

        return textureID;
    }

    @Override
    public void onDrawFrame(int textureID)
    {
        if (!GLES20.glIsProgram(getProgram()))
        {
            return;
        }
        GLES20.glClearColor(1, 1, 1, 1);
        GLES20.glClearStencil(0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);

        mDrawBg = false;
        mDrawMask = true;
        GLES20.glColorMask(false, false, false, false);
        GLES20.glStencilMask(0xFF);
        draw(0);
        mDrawMask = false;

        mDrawLayer = true;
        runTask(true);
        // queueRunnable(mLayerImgTask);
        GLES20.glColorMask(true, true, true, true);
        draw(mTextureIDArr[0]);
        mDrawLayer = false;
    }

    @Override
    public void destroy()
    {
        super.destroy();

        if (mTextureIDArr != null && mTextureIDArr.length > 0)
        {
            if (GLES20.glIsTexture(mTextureIDArr[0]))
            {
                GLES20.glDeleteTextures(1, mTextureIDArr, 0);
                mTextureIDArr[0] = 0;
            }
        }

        if (mLayerImgTask != null)
        {
            mLayerImgTask.destroy();
        }
    }
}
