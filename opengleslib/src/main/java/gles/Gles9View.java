package gles;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

import filter.img.ImageFilter;

public class Gles9View extends GLSurfaceView
{
    private final ImageFilter render;

    public Gles9View(Context context)
    {
        super(context);

        setEGLContextClientVersion(2);
        render = new ImageFilter(getResources());
        setRenderer(render);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        requestRender();
    }
    
    public void setBitmap(Bitmap bitmap)
    {
        render.setTextureBitmap(bitmap);
        render.setRefresh();
        requestRender();
    }
}
