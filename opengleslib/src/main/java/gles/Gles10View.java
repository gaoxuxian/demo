package gles;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

import filter.img.WaterMarkFilter;

public class Gles10View extends GLSurfaceView
{
    private final WaterMarkFilter mFilter;

    public Gles10View(Context context)
    {
        super(context);

        setEGLContextClientVersion(2);
        mFilter = new WaterMarkFilter(getResources());
        setRenderer(mFilter);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        requestRender();
    }

    public void setBitmap(Bitmap bitmap)
    {
        mFilter.setTextureBitmap(bitmap);
        mFilter.setRefreshBmp();
    }

    public void setWaterBitmap(Bitmap bitmap)
    {
        mFilter.setTextureWatermarkBitmap(bitmap);
        mFilter.setRefreshWaterBmp();
    }
}
