package filter;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import util.VaryTools;

public abstract class AFilter implements IFilter
{
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private int mGLProgram;

    private float[] mMatrix;

    private Resources mResources;

    private VaryTools mMatrixTools;

    public AFilter(Resources res)
    {
        mResources = res;
        mMatrixTools = new VaryTools();
        mMatrix = mMatrixTools.getOpenGLUnitMatrix();
        onInitBaseData();
    }

    // 初始化一些要用的顶点坐标、颜色、纹理坐标
    protected abstract void onInitBaseData();

    // 当 surface 被构建时，需要配置某些属性
    protected abstract void onSurfaceCreateSet(EGLConfig config);

    protected abstract int onCreateProgram();

    // 当 surface size 发生变化时，需要重新配置某些属性
    protected abstract void onSurfaceChangeSet(int width, int height);

    // 在画内容之前，需要配置某些属性
    protected abstract void onBe4DrawSet();

    protected abstract void onDrawSelf();

    public void setMatrix(@NonNull float[] matrix)
    {
        mMatrix = matrix;
    }

    @NonNull
    public float[] getMatrix()
    {
        return mMatrix;
    }

    /**
     * 通过这个工具对矩阵做变化
     */
    @NonNull
    public VaryTools getMatrixTools()
    {
        return mMatrixTools;
    }

    public int getSurfaceWidth()
    {
        return mSurfaceWidth;
    }

    public int getSurfaceHeight()
    {
        return mSurfaceHeight;
    }

    public int getGLProgram()
    {
        return mGLProgram;
    }

    private void setSurfaceSize(int w, int h)
    {
        mSurfaceHeight = h;
        mSurfaceWidth = w;
    }

    public Resources getResources()
    {
        return mResources;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        onSurfaceCreateSet(config);
        mGLProgram = onCreateProgram();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        setSurfaceSize(width, height);
        onSurfaceChangeSet(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        onBe4DrawSet();
        onDrawSelf();
    }

    public void onClear()
    {
        mResources = null;
    }
}
