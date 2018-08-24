package intent;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * 模块之间的意图 (用于布局层次比较深的模块之间通信)
 * <p>
 * 比较简单的，建议直接用 callback
 */
public class ModuleIntent
{
    // 注册意图
    public static void sCreateAndLogInIntentFilter(IntentListener listener, String... filters)
    {
        if (filters != null)
        {
            int len = filters.length;
            IntentLogInObj[] out = new IntentLogInObj[len];
            for (int i = 0; i < len; i++)
            {
                String filter = filters[i];
                if (!TextUtils.isEmpty(filter))
                {
                    IntentLogInObj obj = new IntentLogInObj();
                    obj.setFilter(filter);
                    obj.setIntentListener(listener);
                    out[i] = obj;
                }
            }
            ModuleIntentData.sAddLogInObjArr(out);
        }
    }

    /**
     * 释放引用，{@link ModuleIntent#sRemoveIntentFilter(IntentListener listener)} 作用一致
     *
     * @param filters 注册了的意图
     */
    public static void sRemoveIntentFilter(String... filters)
    {
        ModuleIntentData.sRemoveLogInObjArr(filters);
    }

    /**
     * 释放注册时的监听，{@link ModuleIntent#sRemoveIntentFilter(String... filters)} 作用一致
     * @param listener 注册了的监听
     */
    public static void sRemoveIntentFilter(IntentListener listener)
    {
        ModuleIntentData.sRemoveLogInObj(listener);
    }

    /**
     * 释放全部引用，即使不是自己注册的也释放
     */
    public static void sClearAllIntentFilter()
    {
        ModuleIntentData.sClearAllLogInObj();
    }

    /**
     * 激活意图
     */
    public void launchIntent(String filter)
    {
        this.launchIntent(filter, null, null);
    }

    /**
     * 激活意图
     */
    public void launchIntent(String filter, String action)
    {
        this.launchIntent(filter, action, null);
    }

    /**
     * 激活意图
     */
    public void launchIntent(String filter, HashMap<Object, Object> extra)
    {
        this.launchIntent(filter, null, extra);
    }

    /**
     * 激活意图
     */
    public void launchIntent(String filter, String action, HashMap<Object, Object> extra)
    {
        IntentLogInObj obj = ModuleIntentData.sGetLogInObj(filter);
        if (obj != null && obj.isListenerValid())
        {
            IntentListener listener = obj.getIntentListener();
            listener.onIntentBeActivated(filter, action, extra);
        }
    }
}
