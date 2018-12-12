package gles;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import gpu.CopyTextureFilter;
import util.GLUtil;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/20.
 */
public class GLes17View extends GLSurfaceView implements GLSurfaceView.Renderer
{
    CopyTextureFilter mFilter;

    public GLes17View(Context context)
    {
        super(context);
        mFilter = new CopyTextureFilter(context);

        setEGLContextClientVersion(GLUtil.getGlSupportVersionInt(context));
        setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        requestRender();
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
        mFilter.initFrameBuffer(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        GLUtil.checkGlError("xxxx");
        mFilter.onDrawBuffer(0);
        GLUtil.checkGlError("xxxx");
    }
}
