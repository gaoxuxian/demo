package gpu;

import android.content.res.Resources;
import android.opengl.GLSurfaceView;

import java.util.HashMap;
import java.util.LinkedList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import util.VaryTools;

public abstract class GPUFilter implements GLSurfaceView.Renderer
{
    private Resources mRes;

    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private int mTextureWidth;
    private int mTextureHeight;

    private final VaryTools mMatrixTools;
    private volatile FrameBufferMgr mFrameBufferMgr;
    private final LinkedList<AbsTask> mTaskQueue;

    private final HashMap<String, Integer> mTextureArr;

    public GPUFilter(Resources resources)
    {
        mRes = resources;
        mMatrixTools = new VaryTools();
        mTaskQueue = new LinkedList<>();
        mTextureArr = new HashMap<>();
    }

    protected void addTexture(String key, int textureID)
    {
        mTextureArr.put(key, textureID);
    }

    protected int getTexture(String key)
    {
        return mTextureArr.get(key);
    }

    public void setFrameBuffer(FrameBufferMgr mgr)
    {
        mFrameBufferMgr = mgr;
    }

    public FrameBufferMgr getFrameBufferMgr()
    {
        return mFrameBufferMgr;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        onSurfaceChanged(width, height);
        setSurfaceWH(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        onDrawFrameAsRender();
    }

    public abstract void onSurfaceCreated();

    public abstract void onSurfaceChanged(int width, int height);

    /**
     * 直接作为一个 render 时的生命周期方法
     */
    public abstract void onDrawFrameAsRender();

    /**
     * 非 render 生命周期方法
     *
     * @param textureID
     */
    public abstract void onDrawFrame(int textureID);

    /**
     * 非 render 生命周期方法
     *
     * @param textureID
     */
    public abstract void onDrawFrameBuffer(int textureID);

    protected void setSurfaceWH(int width, int height)
    {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    public int getSurfaceWidth()
    {
        return mSurfaceWidth;
    }

    public int getSurfaceHeight()
    {
        return mSurfaceHeight;
    }

    public void setTextureWH(int width, int height)
    {
        mTextureWidth = width;
        mTextureHeight = height;
    }

    public int getTextureWidth()
    {
        return mTextureWidth;
    }

    public int getTextureHeight()
    {
        return mTextureHeight;
    }

    public void onDestory()
    {
        mRes = null;
    }

    public Resources getResource()
    {
        return mRes;
    }

    public void addTaskToQueue(AbsTask task)
    {
        if (task == null) return;

        synchronized (mTaskQueue)
        {
            if (mTaskQueue.isEmpty())
            {
                task.start();
            }
            mTaskQueue.add(task);
        }
    }

    public void clearTask()
    {
        synchronized (mTaskQueue)
        {
            for (int i = 0; i < mTaskQueue.size(); i++)
            {
                AbsTask task = mTaskQueue.get(i);
                if (task != null)
                {
                    task.clear();
                }
            }
            mTaskQueue.clear();
        }
    }

    public int getTaskSize()
    {
        return mTaskQueue.size();
    }

    public void runTask()
    {
        synchronized (mTaskQueue)
        {
            if (mTaskQueue.isEmpty())
            {
                return;
            }
            AbsTask task = mTaskQueue.get(0);
            if (task != null)
            {
                task.start();
                if (task.isFinish())
                {
                    task.executeTaskCallback();
                    task.clear();
                    mTaskQueue.remove(task);

                    if (mTaskQueue.isEmpty())
                    {
                        return;
                    }
                    task = mTaskQueue.get(0);
                    if (task != null)
                    {
                        task.start();
                    }
                }
            }
        }
    }

    public VaryTools getMatrixTools()
    {
        return mMatrixTools;
    }
}
