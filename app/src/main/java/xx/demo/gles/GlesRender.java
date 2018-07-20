package xx.demo.gles;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GlesRender implements GLSurfaceView.Renderer
{
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        //First:设置清空屏幕用的颜色，前三个参数对应红绿蓝，最后一个对应alpha
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        //Second:设置视口尺寸，即告诉opengl可以用来渲染的surface大小
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        //Third:清空屏幕，擦除屏幕上所有的颜色，并用之前glClearColor定义的颜色填充整个屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
