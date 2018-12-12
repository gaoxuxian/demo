package gpu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import lib.opengles.R;
import util.GLTextureUtil;
import util.GLUtil;
import util.VaryTools;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/11.
 */
public class CopyTextureFilter extends GPUImageFilter
{
    private int[] mTextureIDArr;// 0 - 遮罩 1 - 内容 2 - copy 的新纹理
    private boolean mDrawMask;
    private boolean mDrawContent;
    private boolean mAfterCopy;

    public CopyTextureFilter(Context context)
    {
        super(context);
    }

    @Override
    protected void onInitBaseData()
    {
        mTextureIDArr = new int[3];
    }

    @Override
    protected void preDrawSteps2BindTexture(int textureID)
    {
        super.preDrawSteps2BindTexture(textureID);
    }

    @Override
    protected void preDrawSteps3Matrix()
    {
        VaryTools matrix = getMatrix();
        float sv = (float) getSurfaceH() / getSurfaceW();
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        matrix.frustum(-1, 1, -sv, sv, 3, 5);
        matrix.pushMatrix();

        if (mDrawMask)
        {
            matrix.scale(1f*0.6f, 1.5f * 0.6f, 1f);
        }
        else if (mDrawContent)
        {
            matrix.scale(1f, 2f, 1f);
        }
        else if (mAfterCopy)
        {
            matrix.rotate(30, 0, 0, 1);
            matrix.scale(1f*0.6f, 1.5f * 0.6f, 1f);
        }

        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    protected void preDrawSteps4Other()
    {
        if (!GLES20.glIsTexture(mTextureIDArr[0]))
        {
            GLES20.glGenTextures(1, mTextureIDArr, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIDArr[0]);
            GLTextureUtil.bindTexture2DParams();
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.copy_texture_res);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }

        if (!GLES20.glIsTexture(mTextureIDArr[1]))
        {
            GLES20.glGenTextures(1, mTextureIDArr, 1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIDArr[1]);
            GLTextureUtil.bindTexture2DParams();
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.opengl_test_1);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }

        if (!GLES20.glIsTexture(mTextureIDArr[2]))
        {
            GLES20.glGenTextures(1, mTextureIDArr, 2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIDArr[2]);
            GLTextureUtil.bindTexture2DParams();
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 200, 300, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }

        if (mDrawMask)
        {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIDArr[0]);
            GLES20.glUniform1i(vTextureHandle, 0);

            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
            GLES20.glBlendFuncSeparate(GLES20.GL_ONE, GLES20.GL_ONE, GLES20.GL_ONE, GLES20.GL_ONE);
        }
        else if (mDrawContent)
        {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIDArr[1]);
            GLES20.glUniform1i(vTextureHandle, 0);

            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
            GLES20.glBlendFuncSeparate(GLES20.GL_DST_ALPHA, GLES20.GL_ZERO, GLES20.GL_DST_ALPHA, GLES20.GL_ONE);
        }
        else if (mAfterCopy)
        {
            blendEnable(true);
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
            mFrameBufferMgr.clearDepth(true, true);
            mFrameBufferMgr.clearStencil(true, true);

            GLUtil.checkGlError("xxxx");
            mDrawMask = true;
            draw(0);
            mDrawMask = false;

            GLUtil.checkGlError("xxxx");
            mDrawContent = true;
            draw(0);
            mDrawContent = false;

            GLUtil.checkGlError("xxxx");
            int id = mFrameBufferMgr.getCurrentTextureId();
            mFrameBufferMgr.unbind();

            GLUtil.checkGlError("xxxx");
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIDArr[2]);
            GLES20.glClearColor(1, 1, 1, 1);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            GLUtil.checkGlError("xxxx");
            mFrameBufferMgr.bindNext(id);
            GLUtil.checkGlError("xxxx");
            GLES20.glCopyTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 0, 0, 200, 300, 0);
            GLUtil.checkGlError("xxxx");
            mFrameBufferMgr.unbind();
            GLUtil.checkGlError("xxxx");

            GLES20.glClearColor(0, 1, 0, 1);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            mAfterCopy = true;
            draw(id);
            mAfterCopy = false;
            GLUtil.checkGlError("xxxx");
        }

        return textureID;
    }

    @Override
    protected void afterDraw()
    {
        super.afterDraw();

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    @Override
    public void initFrameBuffer(int width, int height)
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
            mFrameBufferMgr = new TextureFboMgr30(width, height, createFrameBufferSize());
        }
    }
}
