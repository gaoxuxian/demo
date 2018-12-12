package gpu;

import android.opengl.GLES30;

public class TextureFboMgr30 extends AbsFboMgr
{
    private int[] mFrameBufferArr;

    private int[] mEmptyColorTextureArr;

    private int[] mDepthStencilRenderBufferArr;

    public TextureFboMgr30(int width, int height)
    {
        super(width, height);
    }

    public TextureFboMgr30(int width, int height, int size)
    {
        super(width, height, size);
    }

    public TextureFboMgr30(int width, int height, int size, boolean color, boolean depth, boolean stencil)
    {
        super(width, height, size, color, depth, stencil);
    }

    @Override
    protected void init(int width, int height, int size, boolean color, boolean depth, boolean stencil)
    {
        if (!color && !depth && !stencil)
        {
            throw new RuntimeException("构建一个颜色、深度、模板都没有挂载的 FrameBuffer 并没有意义");
        }

        mFrameBufferArr = new int[size];
        GLES30.glGenFramebuffers(size, mFrameBufferArr, 0);

        if (color)
        {
            mEmptyColorTextureArr = new int[size];
            GLES30.glGenTextures(size, mEmptyColorTextureArr, 0);
        }

        if (depth || stencil)
        {
            mDepthStencilRenderBufferArr = new int[size];
            GLES30.glGenRenderbuffers(size, mDepthStencilRenderBufferArr, 0);
        }

        for (int i = 0; i < size; i++)
        {
            // 绑定fbo
            int frameBufferID = mFrameBufferArr[i];
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferID);

            if (color)
            {
                // 存储RGBA
                int textureID = mEmptyColorTextureArr[i];
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
                // 挂载颜色缓冲纹理
                GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, textureID, 0);
            }

            if (depth || stencil)
            {
                int renderBufferID = mDepthStencilRenderBufferArr[i];
                GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderBufferID);

                if (depth && stencil)
                {
                    // 存储深度、模板测试信息
                    GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH24_STENCIL8, width, height);
                    // 挂载深度、模板缓冲区
                    GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, renderBufferID);
                    GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_STENCIL_ATTACHMENT, GLES30.GL_RENDERBUFFER, renderBufferID);
                }
                else if (depth)
                {
                    // 存储深度测试信息
                    GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH_COMPONENT16, width, height);
                    // 挂载深度缓冲区
                    GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, renderBufferID);
                }
                else
                {
                    // 存储模板测试信息
                    GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_STENCIL_INDEX8, width, height);
                    // 挂载模板缓冲区
                    GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_STENCIL_ATTACHMENT, GLES30.GL_RENDERBUFFER, renderBufferID);
                }
            }
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, 0);
    }

    @Override
    public void reMount(int width, int height)
    {
        if ((width == mBufferWidth && height == mBufferHeight) || !isInitialized())
        {
            return;
        }

        mBufferWidth = width;
        mBufferHeight = height;

        int size = mBufferSize;
        boolean color = mNeedColorRbo;
        boolean depth = mNeedDepthRbo;
        boolean stencil = mNeedStencilRbo;

        if (mEmptyColorTextureArr != null)
        {
            GLES30.glDeleteTextures(size, mEmptyColorTextureArr, 0);
            for (int i = 0; i < size; i++)
            {
                mEmptyColorTextureArr[i] = 0;
            }
            GLES30.glGenTextures(size, mEmptyColorTextureArr, 0);
        }

        if (mDepthStencilRenderBufferArr != null)
        {
            GLES30.glDeleteRenderbuffers(size, mDepthStencilRenderBufferArr, 0);
            for (int i = 0; i < size; i++)
            {
                mDepthStencilRenderBufferArr[i] = 0;
            }
            GLES30.glGenRenderbuffers(size, mDepthStencilRenderBufferArr, 0);
        }

        for (int i = 0; i < size; i++)
        {
            // 绑定fbo
            int frameBufferID = mFrameBufferArr[i];
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferID);

            if (color)
            {
                // 存储RGBA
                int textureID = mEmptyColorTextureArr[i];
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
                // 挂载颜色缓冲纹理
                GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, textureID, 0);
            }

            if (depth || stencil)
            {
                int renderBufferID = mDepthStencilRenderBufferArr[i];
                GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderBufferID);

                if (depth && stencil)
                {
                    // 存储深度、模板测试信息
                    GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH24_STENCIL8, width, height);
                    // 挂载深度、模板缓冲区
                    GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, renderBufferID);
                    GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_STENCIL_ATTACHMENT, GLES30.GL_RENDERBUFFER, renderBufferID);
                }
                else if (depth)
                {
                    // 存储深度测试信息
                    GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH_COMPONENT16, width, height);
                    // 挂载深度缓冲区
                    GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, renderBufferID);
                }
                else
                {
                    // 存储模板测试信息
                    GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_STENCIL_INDEX8, width, height);
                    // 挂载模板缓冲区
                    GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_STENCIL_ATTACHMENT, GLES30.GL_RENDERBUFFER, renderBufferID);
                }
            }
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, 0);
    }

    private boolean bindByIndex(int index, int textureID)
    {
        index = checkNextIndex(index);

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferArr[index]);
        if (textureID != GLES30.GL_NONE)
        {
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, textureID, 0);
            clearColor(false, true, true, true, true);
            clearDepth(false, true);
            clearStencil(false, true);
        }
        else
        {
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, mEmptyColorTextureArr[index], 0);
            clearColor(true, true, true, true, true);
            clearDepth(true, true);
            clearStencil(true, true);
        }

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
        return isInitialized() && mFrameBufferArr != null && mFrameBufferArr.length > 0;
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
        int size = mBufferSize;

        if (mFrameBufferArr != null)
        {
            GLES30.glDeleteFramebuffers(size, mFrameBufferArr, 0);
            mFrameBufferArr = null;
        }

        if (mEmptyColorTextureArr != null)
        {
            GLES30.glDeleteTextures(size, mEmptyColorTextureArr, 0);
            mEmptyColorTextureArr = null;
        }

        if (mDepthStencilRenderBufferArr != null)
        {
            GLES30.glDeleteRenderbuffers(size, mDepthStencilRenderBufferArr, 0);
            mDepthStencilRenderBufferArr = null;
        }
    }
}
