package util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;
import android.util.Log;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/16.
 */
public class GLUtil
{
    private static final String TAG = GLUtil.class.getName();

    public static int sGetGlSupportVersionInt(Context context)
    {
        return (int) sGetGlSupportVersion(context);
    }

    public static float sGetGlSupportVersion(Context context)
    {
        float version = 0;
        if (context != null)
        {
            final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null)
            {
                final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
                if (configurationInfo != null)
                {
                    version = Float.parseFloat(configurationInfo.getGlEsVersion());
                }
            }
        }
        return version;
    }

    public static boolean sCheckSupportGlVersion(Context context, float version)
    {
        return sGetGlSupportVersion(context) >= version;
    }

    public static void sCheckGlError(String op)
    {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR)
        {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Log.e(TAG, msg);
            throw new RuntimeException(msg);
        }
    }
}
