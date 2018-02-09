package xx.demo;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

import xx.demo.util.ShareData;
import xx.demo.videocache.MyFileNameGenerator;

/**
 * Created by Gxx on 2018/2/9.
 */

public class MyApplication extends Application
{
    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApplication app = (MyApplication) context.getApplicationContext();

        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this).fileNameGenerator(new MyFileNameGenerator()).build();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        ShareData.InitData(this);
    }
}
