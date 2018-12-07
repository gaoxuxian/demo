package gpu;

import android.opengl.GLES20;

public class Texture2dFboMgr extends AbsFboMgr
{
    private int[] mFrameBufferArr;

    private int[] mEmptyColorTextureArr;

    private int[] mDepthRenderBufferArr;

    private int[] mStencilRenderBufferArr;

    public Texture2dFboMgr(int width, int height)
    {
        super(width, height);
    }

    public Texture2dFboMgr(int width, int height, int size)
    {
        super(width, height, size);
    }

    @Override
    protected void init(int width, int height, int size)
    {
        mFrameBufferArr = new int[size];
        GLES20.glGenFramebuffers(size, mFrameBufferArr, 0);
        mEmptyColorTextureArr = new int[size];
        GLES20.glGenTextures(size, mEmptyColorTextureArr, 0);
        mDepthRenderBufferArr = new int[size];
        GLES20.glGenRenderbuffers(size, mDepthRenderBufferArr, 0);
        mStencilRenderBufferArr = new int[size];
        GLES20.glGenRenderbuffers(size, mStencilRenderBufferArr, 0);

        for (int i = 0; i < size; i++)
        {
            // 存储RGBA
            int textureID = mEmptyColorTextureArr[i];
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

            // // 存储深度信息
            // int depthRenderBufferID = mDepthRenderBufferArr[i];
            // GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRenderBufferID);
            // GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
            // GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);

            // 存储模板测试信息
            int stencilRenderBufferID = mStencilRenderBufferArr[i];
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, stencilRenderBufferID);
            GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_STENCIL_INDEX8, width, height);
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);

            // 绑定
            int frameBufferID = mFrameBufferArr[i];
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferID);

            // 挂载颜色、深度、模板缓冲
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureID, 0);
            // GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRenderBufferID);
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_STENCIL_ATTACHMENT, GLES20.GL_RENDERBUFFER, stencilRenderBufferID);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
    }

    private boolean bindByIndex(int index, int textureID)
    {
        index = checkNextIndex(index);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferArr[index]);
        if (textureID != GLES20.GL_NONE)
        {
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureID, 0);
        }
        else
        {
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mEmptyColorTextureArr[index], 0);
        }

        clearColor(false, true, true, true, true);
        clearDepth(false, true);
        clearStencil(false, true);

        mCurrentTextureIndex = index;

        return true;
    }

    @Override
    public boolean bindNext(int textureID)
    {
        return checkAvailable() && bindByIndex(mCurrentTextureIndex, textureID);
    }

    private boolean checkAvailable()
    {
        return mFrameBufferArr != null && mFrameBufferArr.length > 0;
    }

    public int getCurrentTextureId()
    {
        return checkAvailable() ? mEmptyColorTextureArr[mCurrentTextureIndex] : 0;
    }

    public int getPreviousTextureId()
    {
        if (checkAvailable())
        {
            int index = checkPreviousIndex(mCurrentTextureIndex);
            return mEmptyColorTextureArr[index];
        }
        return 0;
    }

    @Override
    public void destroy()
    {
        if (mFrameBufferArr != null && mFrameBufferArr.length > 0)
        {
            GLES20.glDeleteFramebuffers(mFrameBufferArr.length, mFrameBufferArr, 0);
            mFrameBufferArr = null;
        }

        if (mEmptyColorTextureArr != null && mEmptyColorTextureArr.length > 0)
        {
            GLES20.glDeleteTextures(mEmptyColorTextureArr.length, mEmptyColorTextureArr, 0);
            mEmptyColorTextureArr = null;
        }

        if (mDepthRenderBufferArr != null && mDepthRenderBufferArr.length > 0)
        {
            GLES20.glDeleteRenderbuffers(mDepthRenderBufferArr.length, mDepthRenderBufferArr, 0);
            mDepthRenderBufferArr = null;
        }

        if (mStencilRenderBufferArr != null && mStencilRenderBufferArr.length > 0)
        {
            GLES20.glDeleteRenderbuffers(mStencilRenderBufferArr.length, mStencilRenderBufferArr, 0);
            mStencilRenderBufferArr = null;
        }
    }
}
