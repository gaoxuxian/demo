package gpu;

import java.util.LinkedList;

import gpu.AbsTask;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/20.
 */
public class TaskWrapper
{
    // 任务列表
    private final LinkedList<AbsTask> mTaskQueue;

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
        synchronized (mTaskQueue)
        {
            int size = getTaskSize();
            for (int i = 0; i < size; i++)
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
}
