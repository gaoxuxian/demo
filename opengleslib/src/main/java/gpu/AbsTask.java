package gpu;

import android.content.Context;

public abstract class AbsTask implements Runnable
{
    private volatile boolean mIsRun;
    private volatile int mState;//0:初始状态, 1:处理中, 2:处理完成
    private Context mContext;

    public AbsTask(Context context)
    {
        this.mContext = context;
    }

    /**
     * 耗时的任务在此方法内执行
     */
    public abstract void runOnThread();

    public abstract void executeTaskCallback();

    public final void start()
    {
        if (!mIsRun && mState == 0)
        {
            GLThreadPool.sExecuteTask(this);
            mIsRun = true;
        }
    }

    @Override
    public void run()
    {
        mState = 1;
        runOnThread();
        mState = 2;
        mIsRun = false;
    }

    public final boolean isFinish()
    {
        return mState == 2;
    }

    public Context getContext()
    {
        return mContext;
    }

    public void clear()
    {

    }

    public void destroy()
    {
        GLThreadPool.sShutdown(this);
        mContext = null;
    }
}
