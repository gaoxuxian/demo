package gles;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class Gles1View extends GLSurfaceView
{
    private GlesRender1 mRender;

    public Gles1View(Context context)
    {
        super(context);

        mRender = new GlesRender1();
        setEGLContextClientVersion(2);
        setRenderer(mRender);
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }
}
