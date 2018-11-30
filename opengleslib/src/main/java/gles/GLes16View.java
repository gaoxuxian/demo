package gles;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import gpu.FrameBufferMgr;
import gpu.filter.DisplayFilter;
import gpu.filter.ImageFilter;
import gpu.filter.MaskTestFilter;
import util.GLUtil;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/20.
 */
public class GLes16View extends GLSurfaceView implements GLSurfaceView.Renderer
{
    ImageFilter mImageFilter;
    DisplayFilter mDisplayFilter;
    MaskTestFilter mMaskTestFilter;

    FrameBufferMgr mFrameBufferMgr;

    public GLes16View(Context context)
    {
        super(context);

        mImageFilter = new ImageFilter(context);
        mDisplayFilter = new DisplayFilter(context);
        mMaskTestFilter = new MaskTestFilter(context);

        setEGLContextClientVersion(GLUtil.sGetGlSupportVersionInt(context));
        setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        setRenderer(this);
        // setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        // requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        mImageFilter.onSurfaceCreated(config);
        mDisplayFilter.onSurfaceCreated(config);
        mMaskTestFilter.onSurfaceCreated(config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mFrameBufferMgr = new FrameBufferMgr(width, height, 2);

        mImageFilter.onSurfaceChanged(width, height);
        mDisplayFilter.onSurfaceChanged(width, height);
        mMaskTestFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        mMaskTestFilter.onDraw(0);
    }
}
