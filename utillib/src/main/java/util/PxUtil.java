package util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Gxx
 * Created by Gxx on 2018/10/24.
 */
public class PxUtil
{
    /**
     * px = density * dp;
     * <p>
     * density = dpi / 160;
     * <p>
     * px = dp * (dpi / 160);
     */

    @interface Density1080
    {
        int width_px = 1080;
        int height_px = 1920;
        int width_dp = 540;
        int height_dp = 960;
    }

    private static float sNonCompatDensity;

    private static float sCompat540dpDensity;
    public static int sScreenWidth;
    public static int sScreenHeight;
    public static int sScreenRealWidth;
    public static int sScreenRealHeight;

    public static void init(Context context)
    {
        if (context != null)
        {
            Display display;
            DisplayMetrics dm = new DisplayMetrics();

            if (context instanceof Activity)
            {
                display = ((Activity) context).getWindowManager().getDefaultDisplay();
                display.getMetrics(dm);
            }
            else
            {
                WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
                display = wm.getDefaultDisplay();
                display.getMetrics(dm);
            }

            boolean initDisplayInfo = false;

            try
            {
                ClassLoader cl = ClassLoader.getSystemClassLoader();
                Class<?> DisplayInfoClass = cl.loadClass("android.view.DisplayInfo");
                Object o = DisplayInfoClass.newInstance();
                Field appWidth = DisplayInfoClass.getDeclaredField("appWidth");
                appWidth.setAccessible(true);
                Field appHeight = DisplayInfoClass.getDeclaredField("appHeight");
                appHeight.setAccessible(true);
                Field logicalWidth = DisplayInfoClass.getDeclaredField("logicalWidth");
                logicalWidth.setAccessible(true);
                Field logicalHeight = DisplayInfoClass.getDeclaredField("logicalHeight");
                logicalHeight.setAccessible(true);
                Class<? extends Display> aClass = display.getClass();
                Method getDisplayInfo = aClass.getDeclaredMethod("getDisplayInfo", DisplayInfoClass);
                getDisplayInfo.setAccessible(true);
                getDisplayInfo.invoke(display, o);
                sScreenWidth = appWidth.getInt(o);
                sScreenHeight = appHeight.getInt(o);
                sScreenRealWidth = logicalWidth.getInt(o);
                sScreenRealHeight = logicalHeight.getInt(o);
                initDisplayInfo = true;
            }
            catch (Throwable th)
            {
                th.printStackTrace();
                initDisplayInfo = false;
            }

            if (!initDisplayInfo)
            {
                sScreenWidth = dm.widthPixels;
                sScreenHeight = dm.heightPixels;
                if (sScreenWidth > sScreenHeight)
                {
                    sScreenWidth += sScreenHeight;
                    sScreenHeight = sScreenWidth - sScreenHeight;
                    sScreenWidth -= sScreenHeight;
                }

                sScreenRealWidth = sScreenWidth;
                sScreenRealHeight = sScreenHeight;
            }

            if (sNonCompatDensity == 0)
            {
                sNonCompatDensity = dm.density;
                sCompat540dpDensity = (float) sScreenRealWidth/ Density1080.width_dp;
            }

            dm.density = sCompat540dpDensity;
            dm.densityDpi = (int) (sCompat540dpDensity * 160);
        }
    }

    /**
     * 以宽为基准, 标准尺寸 1080 * 1920, 540dp * 960dp
     * @param px 在标准尺寸下的 px 值
     * @return 适配当前屏幕后的 数值
     */
    public static int sU_1080p(int px)
    {
        float density = Density1080.width_px / Density1080.width_dp;
        float dp = px / density;
        return (int) (dp * sCompat540dpDensity + 0.5f);
    }

    /**
     * 以高为基准, 标准尺寸 1080 * 1920, 540dp * 960dp
     * @param px 在标准尺寸下的 px 值
     * @return 适配当前屏幕后的 px 值
     */
    public static int sV_1080p(int px)
    {
        float density = Density1080.height_px / Density1080.height_dp;
        float dp = px / density;
        float compatDensity = sScreenRealHeight / Density1080.height_dp;
        return (int) (dp * compatDensity  + 0.5f);
    }
}