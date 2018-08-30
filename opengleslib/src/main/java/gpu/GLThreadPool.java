package gpu;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GLThreadPool
{
    private static ThreadPoolExecutor mThreadPool;

    public static void sInit()
    {
        if (mThreadPool == null)
        {
            mThreadPool = new ThreadPoolExecutor(5, 7, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        }
    }

    public static void sExecuteTask(Runnable task)
    {
        if (mThreadPool != null)
        {
            mThreadPool.execute(task);
        }
    }

    public static void sShutdownAll()
    {
        if (mThreadPool != null)
        {
            mThreadPool.shutdown();
        }
    }

    public static void sShutdown(Runnable task)
    {
        if (mThreadPool != null)
        {
            mThreadPool.remove(task);
        }
    }
}
