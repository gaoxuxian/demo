package gles;

import android.content.Context;
import android.opengl.GLSurfaceView;

import filter.graphics.Triangle;

public class Gles8View extends GLSurfaceView
{
    public Gles8View(Context context)
    {
        super(context);

        setEGLContextClientVersion(2);
        // 必须设置了Renderer 才能设置 渲染方式, 否则会抛空指针异常
        setRenderer(new Triangle(getResources()));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        requestRender();
    }
}
