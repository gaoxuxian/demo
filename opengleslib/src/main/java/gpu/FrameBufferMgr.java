package gpu;

import android.opengl.GLES20;

public class FrameBufferMgr
{
    public static final int DefaultSize = 3;
    private final int mClearMask;

    private int[] mFrameBufferArr;

    private int[] mFrameBufferEmptyTextures;

    private int mBufferSize;

    private int mBufferWidth;

    private int mBufferHeight;

    private int mCurrentTextureIndex;
    private boolean mDoClearMask;

    public FrameBufferMgr()
    {
        this(0, 0, DefaultSize);
    }

    public FrameBufferMgr(int width, int height, int size)
    {
        mBufferSize = size;
        mBufferWidth = width;
        mBufferHeight = height;
        mCurrentTextureIndex = -1;
        mDoClearMask = true;
        mClearMask = GLES20.GL_COLOR_BUFFER_BIT;

        if (size > 0)
        {
            if (size > DefaultSize * 2)
            {
                size = DefaultSize * 2;
            }
            mFrameBufferArr = new int[size];
            GLES20.glGenFramebuffers(size, mFrameBufferArr, 0);
            if (width > 0 && height > 0)
            {
                mFrameBufferEmptyTextures = new int[size];
                GLES20.glGenTextures(size, mFrameBufferEmptyTextures, 0);
                for (int i = 0; i < size; i++)
                {
                    int frameBufferID = mFrameBufferArr[i];
                    int textureID = mFrameBufferEmptyTextures[i];
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferID);
                    GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureID, 0);
                }
            }
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public int getBufferSize()
    {
        return mBufferSize;
    }

    public int getBufferWidth()
    {
        return mBufferWidth;
    }

    public int getBufferHeight()
    {
        return mBufferHeight;
    }

    public void unbind()
    {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public boolean bindNext(boolean clear, int textureID)
    {
        return bindByIndex(mCurrentTextureIndex + 1, clear, textureID);
    }

    private boolean bindByIndex(int index, boolean clear, int textureID)
    {
        index = checkIndex(index);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferArr[index]);
        if (textureID != GLES20.GL_NONE)
        {
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureID, 0);
        }
        if (clear)
        {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            if (mDoClearMask && mClearMask > 0)
            {
                GLES20.glClear(mClearMask);
            }
        }

        mCurrentTextureIndex = index;
        return true;
    }

    private int checkIndex(int index)
    {
        if (index < 0)
        {
            return 0;
        }
        else if (index >= mBufferSize)
        {
            return mBufferSize - 1;
        }
        return index;
    }

    private int getTextureIdByIndex(int index)
    {
        index = checkIndex(index);
        return mFrameBufferEmptyTextures[index];
    }

    public int getCurrentTextureId()
    {
        return getTextureIdByIndex(mCurrentTextureIndex);
    }
}
