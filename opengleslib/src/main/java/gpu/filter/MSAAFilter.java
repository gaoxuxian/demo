package gpu.filter;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;

import lib.opengles.R;
import util.GLES20Util;
import util.GLUtil;
import util.ResReadUtils;

/**
 * OpenGLES 3.0 抗锯齿
 * @author Gxx
 * Created by Gxx on 2018/11/21.
 */
public class MSAAFilter extends GPUImageFilter
{
    private final int[] mTextureArr;
    private final int[] mRenderBufferArr;
    private final int[] mFrameBufferArr;
    private float[] mMatrix;
    private boolean mPreDrawToClear;
    private boolean mCopyRenderBuffer;

    public MSAAFilter(Context context)
    {
        super(context, ResReadUtils.readResource(context, R.raw.msaa_vertex_shader), ResReadUtils.readResource(context, R.raw.msaa_fragment_shader));
        mTextureArr = new int[1];
        mRenderBufferArr = new int[1];
        mFrameBufferArr = new int[2];
        mMatrix = GLES20Util.sGetOpenGLUnitMatrix();
    }

    @Override
    protected void onInitProgramHandle()
    {
        super.onInitProgramHandle();
    }

    @Override
    protected void onDrawArraysPre()
    {
        if (!GLES30.glIsTexture(mTextureArr[0]))
        {
            GLES30.glGenFramebuffers(2, mFrameBufferArr, 0);

            GLES30.glGenTextures(1, mTextureArr, 0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureArr[0]);

            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, getSurfaceW(), getSurfaceH(), 0,
                    GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);

            // 构建 一个一般使用的 framebuffer
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferArr[0]);
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, mTextureArr[0], 0);

            // 构建 颜色渲染缓冲区 (render buffer)
            GLES30.glGenRenderbuffers(1, mRenderBufferArr, 0);
            GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, mRenderBufferArr[0]);
            GLES30.glRenderbufferStorageMultisample(GLES30.GL_RENDERBUFFER, 4, GLES30.GL_RGBA8, getSurfaceW(), getSurfaceH());

            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferArr[1]);
            GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_RENDERBUFFER, mRenderBufferArr[0]);
            GLES20.glClearColor(1f, 1f, 1f, 1f);
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        }
        else
        {
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferArr[1]);
            if (mPreDrawToClear)
            {
                // 清除上一帧的缓冲
                GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
            }
        }
        GLUtil.checkFramebufferStatus("AAA");

        GLES30.glViewport(0, 0, getSurfaceW(), getSurfaceH());
        GLES30.glUniformMatrix4fv(vMatrixHandle, 1, false, mMatrix, 0);
    }

    @Override
    public int onDraw(int textureId)
    {
        GLES30.glUseProgram(getProgram());

        mTaskWrapper.runTask();

        mVertexBuffer.position(0);
        GLES30.glVertexAttribPointer(vPositionHandle, 3, GLES30.GL_FLOAT, false, 0, mVertexBuffer);
        GLES30.glEnableVertexAttribArray(vPositionHandle);

        mTextureIndexBuffer.position(0);
        GLES30.glVertexAttribPointer(vCoordinateHandle, 2, GLES30.GL_FLOAT, false, 0, mTextureIndexBuffer);
        GLES30.glEnableVertexAttribArray(vCoordinateHandle);

        onDrawArraysPre();

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(getTextureType(), textureId);
        GLES30.glUniform1i(vTextureHandle, 0);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

        if (mCopyRenderBuffer)
        {
            // 绘制后，复制 render buffer 到正常的 framebuffer 纹理上
            GLES30.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, mFrameBufferArr[1]);
            GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, mFrameBufferArr[0]);
            GLES30.glBlitFramebuffer(0, 0, getSurfaceW(), getSurfaceH(), 0, 0, getSurfaceW(), getSurfaceH(), GLES30.GL_COLOR_BUFFER_BIT, GLES30.GL_LINEAR);
        }

        GLES30.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER,0);
        GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER,0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        GLES30.glDisableVertexAttribArray(vPositionHandle);
        GLES30.glDisableVertexAttribArray(vTextureHandle);
        onDrawArraysAfter();

        return mTextureArr[0];
    }

    @Override
    public void onClear()
    {
        if (mFrameBufferArr != null)
        {
            GLES30.glDeleteFramebuffers(mFrameBufferArr.length, mFrameBufferArr, 0);
        }

        if (mRenderBufferArr != null)
        {
            GLES30.glDeleteRenderbuffers(mRenderBufferArr.length, mRenderBufferArr, 0);
        }

        if (mTextureArr != null)
        {
            GLES30.glDeleteTextures(mTextureArr.length, mTextureArr, 0);
        }

        super.onClear();
    }

    public void setMatrix(float[] matrix)
    {
        if (matrix == null || matrix.length != 16)
        {
            return;
        }

        mMatrix = matrix;
    }

    public void setPreDrawToClear(boolean clear)
    {
        mPreDrawToClear = clear;
    }

    public void setNeedToCopyRenderBuffer(boolean need)
    {
        mCopyRenderBuffer = need;
    }
}
