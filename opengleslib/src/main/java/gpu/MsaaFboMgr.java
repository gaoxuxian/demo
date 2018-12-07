package gpu;

import android.opengl.GLES30;

import util.GLUtil;

public class MsaaFboMgr extends AbsFboMgr
{
    private int[] mMsaaFrameBufferArr;

    private int[] mFrameBufferArr;

    private int[] mTexture2dArr;

    private int[] mColorRenderBufferArr;

    private int[] mDepthRenderBufferArr;

    private int[] mStencilRenderBufferArr;

    public MsaaFboMgr(int width, int height)
    {
        super(width, height);
    }

    public MsaaFboMgr(int width, int height, int size)
    {
        super(width, height, size);
    }

    @Override
    protected void init(int width, int height, int size)
    {
        mMsaaFrameBufferArr = new int[size];
        GLES30.glGenFramebuffers(size, mMsaaFrameBufferArr, 0);
        mColorRenderBufferArr = new int[size];
        GLES30.glGenRenderbuffers(size, mColorRenderBufferArr, 0);
        mDepthRenderBufferArr = new int[size];
        GLES30.glGenRenderbuffers(size, mDepthRenderBufferArr, 0);
        mStencilRenderBufferArr = new int[size];
        GLES30.glGenRenderbuffers(size, mStencilRenderBufferArr, 0);

        mFrameBufferArr = new int[size];
        GLES30.glGenFramebuffers(size, mFrameBufferArr, 0);

        mTexture2dArr = new int[size];
        GLES30.glGenTextures(size, mTexture2dArr, 0);

        int[] param = new int[1];
        GLES30.glGetIntegerv(GLES30.GL_MAX_SAMPLES, param, 0);

        for (int i = 0; i < size; i++)
        {
            // 绑定
            int frameBufferID = mFrameBufferArr[i];
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferID);

            // 空白纹理
            int texture2dID = mTexture2dArr[i];
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture2dID);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);

            // 挂载
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, texture2dID, 0);
            GLUtil.checkFramebufferStatus("DDDD");

            // 绑定
            int msaaFrameBufferID = mMsaaFrameBufferArr[i];
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, msaaFrameBufferID);

            // 存储RGBA-抗锯齿
            int colorRenderBufferID = mColorRenderBufferArr[i];
            GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, colorRenderBufferID);
            GLES30.glRenderbufferStorageMultisample(GLES30.GL_RENDERBUFFER, param[0], GLES30.GL_RGBA8, width, height);
            GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_RENDERBUFFER, colorRenderBufferID);
            GLUtil.checkFramebufferStatus("DDDD");

            // 存储模板测试信息
            int stencilRenderBufferID = mStencilRenderBufferArr[i];
            GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, stencilRenderBufferID);
            GLES30.glRenderbufferStorageMultisample(GLES30.GL_RENDERBUFFER, param[0], GLES30.GL_STENCIL_INDEX8, width, height);
            GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_STENCIL_ATTACHMENT, GLES30.GL_RENDERBUFFER, stencilRenderBufferID);
            GLUtil.checkFramebufferStatus("DDDD");

            // // 存储深度信息
            // int depthRenderBufferID = mDepthRenderBufferArr[i];
            // GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, depthRenderBufferID);
            // GLES30.glRenderbufferStorageMultisample(GLES30.GL_RENDERBUFFER, param[0], GLES30.GL_DEPTH_COMPONENT24, width, height);
            // GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, depthRenderBufferID);
            // GLUtil.checkFramebufferStatus("DDDD");
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, 0);
    }

    private boolean checkAvailable()
    {
        return mMsaaFrameBufferArr != null && mMsaaFrameBufferArr.length > 0;
    }

    @Override
    public boolean bindNext()
    {
        return checkAvailable() && bindByIndex(mCurrentTextureIndex);
    }

    @Override
    public boolean bindNext(int textureID)
    {
        return false; // 不能直接绑定纹理,没有抗锯齿效果
    }

    private boolean bindByIndex(int index)
    {
        index = checkNextIndex(index);

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mMsaaFrameBufferArr[index]);

        clearColor(false, true, true, true, true);
        clearDepth(false, true);
        clearStencil(false, true);

        mCurrentTextureIndex = index;
        return true;
    }

    @Override
    public int getCurrentTextureId()
    {
        if (checkAvailable())
        {
            int index = mCurrentTextureIndex;

            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferArr[index]);

            clearColor(true, true, true, true, true);
            clearDepth(true, true);
            clearStencil(true, true);

            GLES30.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, mMsaaFrameBufferArr[index]);
            GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, mFrameBufferArr[index]);

            GLES30.glBlitFramebuffer(0, 0, mBufferWidth, mBufferHeight, 0, 0, mBufferWidth, mBufferHeight, GLES30.GL_COLOR_BUFFER_BIT, GLES30.GL_LINEAR);

            GLES30.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER,0);
            GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER,0);
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

            return mTexture2dArr[index];
        }
        return 0;
    }

    @Override
    public int getPreviousTextureId()
    {
        if (checkAvailable())
        {
            int index = checkPreviousIndex(mCurrentTextureIndex);

            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferArr[index]);

            clearColor(true, true, true, true, true);
            clearStencil(true, true);

            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

            GLES30.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, mMsaaFrameBufferArr[index]);
            GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, mFrameBufferArr[index]);
            GLES30.glBlitFramebuffer(0, 0, mBufferWidth, mBufferHeight, 0, 0, mBufferWidth, mBufferHeight, GLES30.GL_COLOR_BUFFER_BIT, GLES30.GL_LINEAR);

            GLES30.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, 0);
            GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, 0);

            return mTexture2dArr[index];
        }
        return 0;
    }

    @Override
    public void destroy()
    {
        if (mFrameBufferArr != null && mFrameBufferArr.length > 0)
        {
            GLES30.glDeleteFramebuffers(mFrameBufferArr.length, mFrameBufferArr, 0);
            mFrameBufferArr = null;
        }

        if (mTexture2dArr != null && mTexture2dArr.length > 0)
        {
            GLES30.glDeleteTextures(mTexture2dArr.length, mTexture2dArr, 0);
            mTexture2dArr = null;
        }

        if (mMsaaFrameBufferArr != null && mMsaaFrameBufferArr.length > 0)
        {
            GLES30.glDeleteFramebuffers(mMsaaFrameBufferArr.length, mMsaaFrameBufferArr, 0);
            mMsaaFrameBufferArr = null;
        }

        if (mColorRenderBufferArr != null && mColorRenderBufferArr.length > 0)
        {
            GLES30.glDeleteRenderbuffers(mColorRenderBufferArr.length, mColorRenderBufferArr, 0);
            mColorRenderBufferArr = null;
        }

        if (mDepthRenderBufferArr != null && mDepthRenderBufferArr.length > 0)
        {
            GLES30.glDeleteRenderbuffers(mDepthRenderBufferArr.length, mDepthRenderBufferArr, 0);
            mDepthRenderBufferArr = null;
        }

        if (mStencilRenderBufferArr != null && mStencilRenderBufferArr.length > 0)
        {
            GLES30.glDeleteRenderbuffers(mStencilRenderBufferArr.length, mStencilRenderBufferArr, 0);
            mStencilRenderBufferArr = null;
        }
    }
}
