package egl;

import android.opengl.EGL14;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.EGL14.EGL_CONTEXT_CLIENT_VERSION;

public class EGLHelper
{
    private EGLConfig mEglConfig;
    private EGL10 mEgl;
    private EGLDisplay mEglDisplay;
    private EGLSurface mEglSurface;
    private EGLContext mEglContext;
    public GL10 mGL;

    public static final int SURFACE_PBUFFER = 1;
    public static final int SURFACE_PIM = 2;
    public static final int SURFACE_WINDOW = 3;

    private int mEglSurfaceType = SURFACE_PBUFFER;

    private EGLContext shareContext=EGL10.EGL_NO_CONTEXT;

    public GlError init(int width, int height)
    {
        mEgl = (EGL10) EGLContext.getEGL();
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        int[] version = new int[2];
        mEgl.eglInitialize(mEglDisplay, version);

        int[] attributes = new int[]{
                EGL10.EGL_RED_SIZE, 8,  //指定RGB中的R大小（bits）
                EGL10.EGL_GREEN_SIZE, 8, //指定G大小
                EGL10.EGL_BLUE_SIZE, 8,  //指定B大小
                EGL10.EGL_ALPHA_SIZE, 8, //指定Alpha大小，以上四项实际上指定了像素格式
                EGL10.EGL_DEPTH_SIZE, 16, //指定深度缓存(Z Buffer)大小
                EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, //指定渲染api版本, EGL14.EGL_OPENGL_ES2_BIT
                EGL10.EGL_NONE};  //总是以EGL10.EGL_NONE结尾

        int[] configNumber = new int[1];
        mEgl.eglChooseConfig(mEglDisplay, attributes, null, 0, configNumber);
        if (configNumber[0] == 0)
        {
            return GlError.ConfigErr;
        }

        EGLConfig[] configs = new EGLConfig[configNumber[0]];
        mEgl.eglChooseConfig(mEglDisplay, attributes, configs, configNumber[0], configNumber);
        mEglConfig = configs[0];

        int[] attrib_list = new int[]{
                EGL10.EGL_WIDTH, width,
                EGL10.EGL_HEIGHT, height,
                EGL10.EGL_NONE
                };
        mEglSurface = createEglSurface(attrib_list);

        //创建Context
        int[] contextAttr=new int[]{
                EGL_CONTEXT_CLIENT_VERSION,2,
                EGL10.EGL_NONE
        };

        mEglContext = mEgl.eglCreateContext(mEglDisplay, mEglConfig, shareContext, contextAttr);

        makeCurrent();

        return GlError.OK;
    }

    public void makeCurrent()
    {
        mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext);
        mGL = (GL10) mEglContext.getGL();
    }

    public void destroy()
    {
        mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT);
        mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
        mEgl.eglDestroyContext(mEglDisplay, mEglContext);
        mEgl.eglTerminate(mEglDisplay);
        mEglSurface = EGL10.EGL_NO_SURFACE;
        mEglDisplay = EGL10.EGL_NO_DISPLAY;
        mEglContext = EGL10.EGL_NO_CONTEXT;
        mEgl = null;
    }

    private EGLSurface createEglSurface(int[] attrib_list)
    {
        switch (mEglSurfaceType)
        {
            case SURFACE_WINDOW:
                return mEgl.eglCreateWindowSurface(mEglDisplay, mEglConfig, null, attrib_list);
            case SURFACE_PIM:
                return mEgl.eglCreatePixmapSurface(mEglDisplay, mEglConfig, null, attrib_list);
            default:
                return mEgl.eglCreatePbufferSurface(mEglDisplay, mEglConfig, attrib_list);
        }
    }
}
