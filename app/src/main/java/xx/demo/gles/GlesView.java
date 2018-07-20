package xx.demo.gles;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class GlesView extends GLSurfaceView
{
    private GlesRender mRender;

    public GlesView(Context context)
    {
        super(context);

        mRender = new GlesRender();
        setEGLContextClientVersion(2);
        setRenderer(mRender);
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }
}
