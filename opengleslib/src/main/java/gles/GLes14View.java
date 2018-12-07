package gles;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import gpu.filter.ImageFilter;
import util.GLUtil;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/20.
 */
public class GLes14View extends GLSurfaceView implements GLSurfaceView.Renderer
{
    ImageFilter mFilter;

    public GLes14View(Context context)
    {
        super(context);

        mFilter = new ImageFilter(context);
        setEGLContextClientVersion(GLUtil.getGlSupportVersionInt(context));
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        mFilter.onSurfaceCreated(config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        mFilter.onDraw(0);
    }
}
