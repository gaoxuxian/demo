package xx.demo.activity;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * 测试 java 对象锁 wait 、 notify 之间的关系
 * <p>
 * 1、sleep() --> 不释放对象锁。
 * 2、wait() ---> 释放对象锁。
 * 3、notify() ---> 不释放对象锁。
 * <p>
 * (1) notify释放锁吗？不要误导别人。notify()只是唤醒此对象监视器上等待的单个线程，直到当前线程释放此对象上的锁，才有可能继续执行被唤醒的线程。
 * <p>
 * (2) 对的!这个说法是准确的。notify只是唤醒了一个因为调用了wait而自愿阻塞的线程，它现在可以执行了，
 * 但是，能不能访问，要看该对该对象加锁的线程是否已经释放了锁 (两种方式：第一该线程运行同步方法已经结束，第二该线程调用了wait方法，自愿阻塞)。
 * <p>
 * 4、下面补充wait() 和 sleep() 的区别:
 * <p>
 * (1) 核心区别：sleep用于线程控制，wait用于线程间的通信。
 * <p>
 * (2) wait() 执行后，释放执行权，也释放锁，与它同步的线程或者其它的线程都可以拿到执行权。
 * <p>
 * (3) sleep() 执行后，释放执行权，但不释放锁，即与它不拥有同一个锁的线程可以拿到执行权，但与它同步的线程不可以拿到执行权。
 * <p>
 * 对比理解，效果更好。
 * <p>
 * 代码运行结果:
 * <p>
 * TestLockRunnable --> TestLockRunnable --> CurrentThread == main start to wait 当前时间: 1519625247269
 * <p>
 * TestLockRunnable --> run: start runnable
 * <p>
 * TestLockRunnable --> run: start to notify
 * <p>
 * TestLockRunnable --> run: notify finish
 * <p>
 * TestLockRunnable --> TestLockRunnable --> CurrentThread == main resume 当前时间: 1519625247270
 * <p>
 * 阻塞主线程时间大概 几ms (最好先处理了 主线程的必需事件，再阻塞)
 */
public class JavaLockActivity extends BaseActivity implements View.OnClickListener
{
    private Button mTestBtn;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mTestBtn = new Button(parent.getContext());
        mTestBtn.setText("开始测试 Java锁，请留意log");
        mTestBtn.setOnClickListener(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(mTestBtn, params);
    }

    @Override
    public void onClick(View v)
    {
        if (v == mTestBtn)
        {
            new TestLockRunnable();
        }
    }

    private static class TestLockRunnable implements Runnable
    {
        private final Object mSync = new Object();
        private String TAG = getClass().getSimpleName();

        TestLockRunnable()
        {
            synchronized (mSync)
            {
                new Thread(this, getClass().getSimpleName()).start();
                try
                {
                    Log.d(TAG, "TestLockRunnable --> TestLockRunnable --> CurrentThread == " + Thread.currentThread().getName() + " start to wait 当前时间: " + System.currentTimeMillis());
                    mSync.wait();
                    Log.d(TAG, "TestLockRunnable --> TestLockRunnable --> CurrentThread == " + Thread.currentThread().getName() + " resume 当前时间: " + System.currentTimeMillis());
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run()
        {
            Log.d(TAG, "TestLockRunnable --> run: start runnable");

            synchronized (mSync)
            {
                Log.d(TAG, "TestLockRunnable --> run: start to notify");
                mSync.notify();
                Log.d(TAG, "TestLockRunnable --> run: notify finish");
            }
        }
    }
}
