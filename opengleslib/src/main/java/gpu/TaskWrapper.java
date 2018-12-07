package gpu;

import java.util.LinkedList;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/20.
 */
public class TaskWrapper
{
    // 任务列表
    private final LinkedList<AbsTask> mTaskQueue;

    private boolean mClear;

    public TaskWrapper()
    {
        mTaskQueue = new LinkedList<>();
    }

    public void queueRunnable(final AbsTask runnable)
    {
        synchronized (mTaskQueue)
        {
            mTaskQueue.addLast(runnable);
        }
    }

    public void clearTask()
    {
        mClear = true;
        synchronized (mTaskQueue)
        {
            int size = getTaskSize();
            for (int i = 0; i < size; i++)
            {
                AbsTask task = mTaskQueue.get(i);
                if (task != null)
                {
                    task.clear();
                    task.destroy();
                }
            }
            mTaskQueue.clear();
        }
    }

    public boolean isClear()
    {
         return mClear;
    }

    public int getTaskSize()
    {
        synchronized (mTaskQueue)
        {
            return mTaskQueue.size();
        }
    }

    public void startTask()
    {
        if (!mClear)
        {
            synchronized (mTaskQueue)
            {
                if (mTaskQueue.isEmpty() || mClear)
                {
                    return;
                }
                AbsTask task = mTaskQueue.get(0);
                if (task != null)
                {
                    task.start();
                    if (task.isFinish() && !mClear)
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
    }

    public void runTask()
    {
        if (!mClear)
        {
            synchronized (mTaskQueue)
            {
                if (mTaskQueue.isEmpty() || mClear)
                {
                    return;
                }
                AbsTask task = mTaskQueue.get(0);
                if (task != null)
                {
                    task.run();
                    if (task.isFinish() && !mClear)
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
    }
}
