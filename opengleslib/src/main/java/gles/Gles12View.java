package gles;

import android.content.Context;
import android.opengl.GLSurfaceView;

import filter.fbo.ImgFBOFilter;

public class Gles12View extends GLSurfaceView
{
    public Gles12View(Context context)
    {
        super(context);
        setEGLContextClientVersion(2);
        setRenderer(new ImgFBOFilter(getResources()));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        requestRender();
    }
}
