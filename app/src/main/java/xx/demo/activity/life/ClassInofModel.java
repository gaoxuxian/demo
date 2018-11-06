package xx.demo.activity.life;

import android.util.Log;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/6.
 */
public class ClassInofModel
{
    @ClassInfo("test")
    public void onTest(String str)
    {
        Log.d("xxx", "onTest: str == " + str);
    }
}
