package filter.camera;

import android.content.res.Resources;

import javax.microedition.khronos.egl.EGLConfig;

import filter.AFilter;

public class CameraPreviewFilter extends AFilter
{
    public CameraPreviewFilter(Resources res)
    {
        super(res);
    }

    @Override
    protected void onInitBaseData()
    {

    }

    @Override
    protected void onSurfaceCreateSet(EGLConfig config)
    {

    }

    @Override
    protected int onCreateProgram()
    {
        return 0;
    }

    @Override
    protected void onSurfaceChangeSet(int width, int height)
    {

    }

    @Override
    protected void onBe4DrawSet()
    {

    }

    @Override
    protected void onDrawSelf()
    {

    }
}
