package xx.demo;

import android.app.Application;

import gpu.GLThreadPool;
import util.PxUtil;
import util.ThreadUtil;

/**
 * Created by Gxx on 2018/2/9.
 */

public class MyApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        PxUtil.init(this);
        ThreadUtil.init();
        GLThreadPool.sInit();
    }
}
