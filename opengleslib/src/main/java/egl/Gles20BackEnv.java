package egl;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.IntBuffer;

import filter.EGLFilter;

public class Gles20BackEnv
{
    private EGLHelper mEglHelper;

    private EGLFilter mFilter;
    private Bitmap bitmap;

    public Gles20BackEnv()
    {
        mEglHelper = new EGLHelper();
    }

    public void setBitmap(Bitmap bitmap)
    {
        if (mFilter != null && bitmap != null && !bitmap.isRecycled())
        {
            this.bitmap = bitmap;
            mEglHelper.init(bitmap.getWidth(), bitmap.getHeight());
            mFilter.onInitBaseData();
            mFilter.setGLProgram(mFilter.onCreateProgram());
            mFilter.onSurfaceChangeSet(bitmap.getWidth(), bitmap.getHeight());
            mFilter.setTextureBmp(bitmap);
        }
    }

    public void setFilter(EGLFilter filter)
    {
        mFilter = filter;
    }

    public Bitmap getOutputBitmap()
    {
        Bitmap out = null;

        if (mFilter != null)
        {
            mFilter.onBe4DrawSet();
            mFilter.onDrawSelf();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] iat = new int[width * height];
            IntBuffer ib = IntBuffer.allocate(width * height);
            mEglHelper.mGL.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);

            int[] array = ib.array();

            for (int i = 0; i<height;i++)
            {
                System.arraycopy(array, i * width, iat, (height - i - 1) * width, width);
            }

            out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            out.copyPixelsFromBuffer(IntBuffer.wrap(iat));
        }

        return out;
    }

}
