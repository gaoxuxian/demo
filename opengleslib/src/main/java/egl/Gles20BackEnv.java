package egl;

import android.graphics.Bitmap;

public class Gles20BackEnv
{
    private EGLHelper mEglHelper;
    private Bitmap mBitmap;

    public Gles20BackEnv()
    {
        mEglHelper = new EGLHelper();
    }

    public void setBitmap(Bitmap bitmap)
    {
        mBitmap = bitmap;
    }

    public void setFilter()
    {

    }
}
