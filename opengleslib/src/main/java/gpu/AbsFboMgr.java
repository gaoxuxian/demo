package gpu;

import android.opengl.GLES20;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/7.
 */
public abstract class AbsFboMgr
{
    protected static final int DEFAULT_SIZE = 3;
    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 100;

    protected int mBufferSize;
    protected int mBufferWidth;
    protected int mBufferHeight;

    protected int mCurrentTextureIndex;

    public AbsFboMgr(int width, int height)
    {
        this(width, height, DEFAULT_SIZE);
    }

    public AbsFboMgr(int width, int height, int size)
    {
        if (width <= 0)
        {
            width = DEFAULT_WIDTH;
        }

        if (height <= 0)
        {
            height = DEFAULT_HEIGHT;
        }

        if (size <= 0)
        {
            size = DEFAULT_SIZE;
        }
        else if (size > DEFAULT_SIZE * 2)
        {
            size = DEFAULT_SIZE * 2;
        }

        this.mBufferSize = size;
        this.mBufferWidth = width;
        this.mBufferHeight = height;
        this.mCurrentTextureIndex = -1;

        init(width, height, size);
    }

    protected abstract void init(int width, int height, int size);

    public boolean bindNext()
    {
        return bindNext(GLES20.GL_NONE);
    }

    public abstract boolean bindNext(int textureID);

    public void unbind()
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
    }

    public void clearColor(boolean clear, boolean canStoreR, boolean canStoreG, boolean canStoreB, boolean canStoreA)
    {
        GLES20.glColorMask(canStoreR, canStoreG, canStoreB, canStoreA);
        if (clear)
        {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        }
    }

    public void clearDepth(boolean clear, boolean canStore)
    {
        GLES20.glDepthMask(canStore);
        if (clear)
        {
            GLES20.glClearDepthf(1);
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        }
    }

    public void clearStencil(boolean clear, boolean canStore)
    {
        GLES20.glStencilMask(canStore ? 1 : 0);
        if (clear)
        {
            GLES20.glClearStencil(0);
            GLES20.glClear(GLES20.GL_STENCIL_BUFFER_BIT);
        }
    }

    public int getBufferWidth()
    {
        return mBufferWidth;
    }

    public int getBufferHeight()
    {
        return mBufferHeight;
    }

    protected int checkNextIndex(int index)
    {
        index += 1;
        if (index < 0)
        {
            index = 0;
        }
        return index % mBufferSize;
    }

    protected int checkPreviousIndex(int index)
    {
        index -= 1;
        if (index < 0)
        {
            index = mBufferSize - 1;
        }
        return index % mBufferSize;
    }

    public abstract int getCurrentTextureId();

    public abstract int getPreviousTextureId();

    public abstract void destroy();
}
