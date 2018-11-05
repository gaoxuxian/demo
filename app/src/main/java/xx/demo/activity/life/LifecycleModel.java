package xx.demo.activity.life;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import util.ThreadUtil;

import android.util.Log;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/5.
 */
public class LifecycleModel implements LifecycleObserver
{
    MyViewModel model;

    public LifecycleModel(MyViewModel model)
    {
        this.model = model;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate(LifecycleOwner owner)
    {
        Log.d("xxx", "onCreate: LifecycleModel == " + this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart(LifecycleOwner owner)
    {
        Log.d("xxx", "onStart: LifecycleModel == " + this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume(LifecycleOwner owner)
    {
        if (model != null)
        {
            ThreadUtil.runOnUiThreadDelay(new Runnable()
            {
                @Override
                public void run()
                {
                    if (model != null)
                    {
                        model.loadData();
                    }
                }
            }, 3000);
        }
        Log.d("xxx", "onResume: LifecycleModel == " + this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause(LifecycleOwner owner)
    {
        Log.d("xxx", "onPause: LifecycleModel == " + this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop(LifecycleOwner owner)
    {
        Log.d("xxx", "onStop: LifecycleModel == " + this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(LifecycleOwner owner)
    {
        Log.d("xxx", "onDestroy: LifecycleModel == " + this);
        owner.getLifecycle().removeObserver(this);
        model = null;
    }
}
