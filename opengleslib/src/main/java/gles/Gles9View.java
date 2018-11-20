package gles;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import filter.img.ImageFilter;

public class Gles9View extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private final ImageFilter render;

    public Gles9View(Context context)
    {
        super(context);

        setEGLContextClientVersion(2);
        setEGLConfigChooser(new EGLConfigChooser()
        {
            @Override
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display)
            {
                int[] arr = new int[1];
                GLES20.glGetIntegerv(GLES30.GL_MAX_SAMPLES, arr, 0);
                int attribs[] = {
                        EGL10.EGL_LEVEL, 0,
                        EGL10.EGL_RENDERABLE_TYPE, 4,  // EGL_OPENGL_ES2_BIT
                        EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                        EGL10.EGL_RED_SIZE, 8,
                        EGL10.EGL_GREEN_SIZE, 8,
                        EGL10.EGL_BLUE_SIZE, 8,
                        EGL10.EGL_DEPTH_SIZE, 16,
                        EGL10.EGL_SAMPLE_BUFFERS, GLES10.GL_TRUE,
                        EGL10.EGL_SAMPLES, arr[0],  // 在这里修改MSAA的倍数，4就是4xMSAA，再往上开程序可能会崩
                        EGL10.EGL_NONE
                };
                EGLConfig[] configs = new EGLConfig[1];
                int[] configCounts = new int[1];
                egl.eglChooseConfig(display, attribs, configs, 1, configCounts);

                if (configCounts[0] == 0) {
                    // Failed! Error handling.
                    return null;
                } else {
                    return configs[0];
                }
            }
        });
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
