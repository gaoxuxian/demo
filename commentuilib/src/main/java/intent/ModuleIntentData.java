package intent;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;

public class ModuleIntentData
{
    private final static Object DATA_LOCK = new Object();
    // 数据最好控制在千级以内, (数据量大, 遍历轮询耗时, 意图等待触发时间较长)
    private static volatile ArrayList<IntentLogInObj> mLogInObjArr = new ArrayList<>();

    static void sAddLogInObjArr(IntentLogInObj[] objArr)
    {
        if (objArr != null)
        {
            synchronized (DATA_LOCK)
            {
                for (IntentLogInObj obj : objArr)
                {
                    if (obj != null && obj.isFilterValid())
                    {
                        Iterator<IntentLogInObj> iterator = mLogInObjArr.iterator();
                        while (iterator.hasNext())
                        {
                            IntentLogInObj next = iterator.next();
                            if (next != null && next.isFilterValid() && next.getFilter().equals(obj.getFilter()))
                            {
                                iterator.remove();
                                break;
                            }
                        }
                        mLogInObjArr.add(obj);
                    }
                }
            }
        }
    }

    static void sRemoveLogInObjArr(String[] filters)
    {
        if (filters != null)
        {
            synchronized (DATA_LOCK)
            {
                for (String filter : filters)
                {
                    if (!TextUtils.isEmpty(filter))
                    {
                        Iterator<IntentLogInObj> iterator = mLogInObjArr.iterator();
                        while (iterator.hasNext())
                        {
                            IntentLogInObj next = iterator.next();
                            if (next != null && next.isFilterValid() && next.getFilter().equals(filter))
                            {
                                // 一般只存在一个
                                iterator.remove();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    static void sRemoveLogInObj(IntentListener listener)
    {
        if (listener != null)
        {
            synchronized (DATA_LOCK)
            {
                Iterator<IntentLogInObj> iterator = mLogInObjArr.iterator();
                while (iterator.hasNext())
                {
                    IntentLogInObj next = iterator.next();
                    if (next != null && next.getIntentListener() == listener)
                    {
                        // 可能存在多个
                        iterator.remove();
                    }
                }
            }
        }
    }

    static void sClearAllLogInObj()
    {
        synchronized (DATA_LOCK)
        {
            mLogInObjArr.clear();
        }
    }

    static IntentLogInObj sGetLogInObj(String filter)
    {
        IntentLogInObj out = null;
        if (!TextUtils.isEmpty(filter))
        {
            synchronized (DATA_LOCK)
            {
                for (IntentLogInObj obj : mLogInObjArr)
                {
                    if (obj != null && obj.isFilterValid() && obj.getFilter().equals(filter))
                    {
                        out = obj;
                        break;
                    }
                }
            }
        }
        return out;
    }
}
