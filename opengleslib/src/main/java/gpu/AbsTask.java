package gpu;

public abstract class AbsTask implements Runnable
{
    private volatile boolean mIsRun;
    private volatile int mState;//0:初始状态, 1:处理中, 2:处理完成

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
    }

    public final boolean isFinish()
    {
        return mState == 2;
    }

    public void clear()
    {
        GLThreadPool.sShutdown(this);
    }
}
