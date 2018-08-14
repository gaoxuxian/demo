package gles;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import filter.img.ImageFilter;

public class Gles9View extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private final ImageFilter render;

    public Gles9View(Context context)
    {
        super(context);

        setEGLContextClientVersion(2);
        render = new ImageFilter(getResources());
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        requestRender();
    }

    public void setBitmap(Bitmap bitmap)
    {
        render.setTextureBitmap(bitmap);
        render.setRefresh();
        requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        render.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        render.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        render.onDrawFrame(gl);
    }
}
