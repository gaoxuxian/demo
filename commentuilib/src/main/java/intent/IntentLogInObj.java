package intent;

import android.text.TextUtils;

/**
 * 用于注册意图
 */
public class IntentLogInObj
{
    private IntentListener mIntentListener;
    private String mFilter; // 类似 android 配置清单中的 <intent-filter> 的作用，用于匹配, 存在重复filter的话，使用时可能会被覆盖

    public IntentListener getIntentListener()
    {
        return mIntentListener;
    }

    public void setIntentListener(IntentListener listener)
    {
        this.mIntentListener = listener;
    }

    public String getFilter()
    {
        return mFilter;
    }

    public void setFilter(String filter)
    {
        this.mFilter = filter;
    }

    public boolean isFilterValid()
    {
        return !TextUtils.isEmpty(mFilter);
    }

    public boolean isListenerValid()
    {
        return mIntentListener != null;
    }
}
