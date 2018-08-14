package xx.demo;

import android.app.Application;
import lib.util.ShareData;
import lib.util.ThreadUtil;

/**
 * Created by Gxx on 2018/2/9.
 */

public class MyApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        ShareData.InitData(this);
        ThreadUtil.init();
    }
}
