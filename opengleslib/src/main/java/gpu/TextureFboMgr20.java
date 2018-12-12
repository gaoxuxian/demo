package gpu;

import android.opengl.GLES20;

public class TextureFboMgr20 extends AbsFboMgr
{
    private int[] mFrameBufferArr;

    private int[] mEmptyColorTextureArr;

    private int[] mDepthStencilRenderBufferArr;

    public TextureFboMgr20(int width, int height)
    {
        super(width, height);
    }

    public TextureFboMgr20(int width, int height, int size)
    {
        super(width, height, size);
    }

    public TextureFboMgr20(int width, int height, int size, boolean color, boolean depth, boolean stencil)
    {
        super(width, height, size, color, depth, stencil);
    }

    @Override
    protected void init(int width, int height, int size, boolean color, boolean depth, boolean stencil)
    {
        if (depth && stencil)
        {
            throw new RuntimeException("Open GL ES2.0 不能在同一个 FrameBuffer 上同时挂载深度和模板缓冲区 \n" +
                    "(原因: fbo 同时挂载深度、模板rbo, 需要的格式是 GLES30.GL_DEPTH24_STENCIL8) \n" +
                    "如需必要, 请使用 TextureFboMgr30, 使用前请确保手机支持 Open GL ES 3.0或以上 \n " +
                    "详情请参考: https://www.khronos.org/opengl/wiki/Common_Mistakes");
        }

        if (!color && !depth && !stencil)
        {
            throw new RuntimeException("构建一个颜色、深度、模板都没有挂载的 FrameBuffer 并没有意义");
        }

        mFrameBufferArr = new int[size];
        GLES20.glGenFramebuffers(size, mFrameBufferArr, 0);

        if (color)
        {
            mEmptyColorTextureArr = new int[size];
            GLES20.glGenTextures(size, mEmptyColorTextureArr, 0);
        }

        if (depth || stencil)
        {
            mDepthStencilRenderBufferArr = new int[size];
            GLES20.glGenRenderbuffers(size, mDepthStencilRenderBufferArr, 0);
        }

        for (int i = 0; i < size; i++)
        {
            // 绑定fbo
            int frameBufferID = mFrameBufferArr[i];
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferID);

            if (color)
            {
                // 存储RGBA
                int textureID = mEmptyColorTextureArr[i];
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
                // 挂载颜色缓冲纹理
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureID, 0);
            }

            if (depth || stencil)
            {
                int renderBufferID = mDepthStencilRenderBufferArr[i];
                GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBufferID);

                if (depth)
                {
                    // 存储深度信息
                    GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
                    // 挂载深度缓冲区
                    GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, renderBufferID);
                }
                else
                {
                    // 存储模板信息
                    GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_STENCIL_INDEX8, width, height);
                    // 挂载模板缓冲区
                    GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_STENCIL_ATTACHMENT, GLES20.GL_RENDERBUFFER, renderBufferID);
                }
            }
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
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
            GLES20.glDeleteTextures(size, mEmptyColorTextureArr, 0);
            for (int i = 0; i < size; i++)
            {
                mEmptyColorTextureArr[i] = 0;
            }
            GLES20.glGenTextures(size, mEmptyColorTextureArr, 0);
        }

        if (mDepthStencilRenderBufferArr != null)
        {
            GLES20.glDeleteRenderbuffers(size, mDepthStencilRenderBufferArr, 0);
            for (int i = 0; i < size; i++)
            {
                mDepthStencilRenderBufferArr[i] = 0;
            }
            GLES20.glGenRenderbuffers(size, mDepthStencilRenderBufferArr, 0);
        }

        for (int i = 0; i < size; i++)
        {
            // 绑定fbo
            int frameBufferID = mFrameBufferArr[i];
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferID);

            if (color)
            {
                // 存储RGBA
                int textureID = mEmptyColorTextureArr[i];
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
                // 挂载颜色缓冲纹理
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureID, 0);
            }

            if (depth || stencil)
            {
                int renderBufferID = mDepthStencilRenderBufferArr[i];
                GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBufferID);

                if (depth)
                {
                    // 存储深度信息
                    GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
                    // 挂载深度缓冲区
                    GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, renderBufferID);
                }
                else
                {
                    // 存储模板信息
                    GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_STENCIL_INDEX8, width, height);
                    // 挂载模板缓冲区
                    GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_STENCIL_ATTACHMENT, GLES20.GL_RENDERBUFFER, renderBufferID);
                }
            }
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
            clearColor(false, true, true, true, true);
            clearDepth(false, true);
            clearStencil(false, true);
        }
        else
        {
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mEmptyColorTextureArr[index], 0);
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
            GLES20.glDeleteFramebuffers(size, mFrameBufferArr, 0);
            mFrameBufferArr = null;
        }

        if (mEmptyColorTextureArr != null)
        {
            GLES20.glDeleteTextures(size, mEmptyColorTextureArr, 0);
            mEmptyColorTextureArr = null;
        }

        if (mDepthStencilRenderBufferArr != null)
        {
            GLES20.glDeleteRenderbuffers(size, mDepthStencilRenderBufferArr, 0);
            mDepthStencilRenderBufferArr = null;
        }
    }
}
