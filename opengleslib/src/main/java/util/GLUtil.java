package util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/16.
 */
public class GLUtil
{
    private static final String TAG = GLUtil.class.getName();

    public static void checkGlError(String op)
    {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR)
        {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Log.e(TAG, msg);
            throw new RuntimeException(msg);
        }
    }

    public static int getGlSupportVersionInt(Context context)
    {
        return (int) getGlSupportVersion(context);
    }

    public static float getGlSupportVersion(Context context)
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

    public static boolean checkSupportGlVersion(Context context, float version)
    {
        return getGlSupportVersion(context) >= version;
    }

    public static void checkFramebufferStatus(String msg)
    {
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE)
        {
            String error = "";
            switch (status)
            {
                case GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                    msg = "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
                    break;
                case GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
                    msg = "GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS";
                    break;
                case GLES20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                    msg = "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
                    break;
                case GLES20.GL_FRAMEBUFFER_UNSUPPORTED:
                    msg = "GL_FRAMEBUFFER_UNSUPPORTED";
                    break;
            }
            throw new RuntimeException(msg + ":" + error + ", error code == " + Integer.toHexString(status));
        }
    }

    public static String readShaderFromRaw(Context context, final int resourceId)
    {
        if (context == null)
        {
            return null;
        }
        final InputStream inputStream = context.getResources().openRawResource(resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try
        {
            while ((nextLine = bufferedReader.readLine()) != null)
            {
                body.append(nextLine);
                body.append('\n');
            }
        }
        catch (IOException e)
        {
            return null;
        }
        finally
        {
            try
            {
                inputStream.close();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }

            try
            {
                inputStreamReader.close();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }

            try
            {
                bufferedReader.close();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        }
        return body.toString();
    }

    public static String readShaderFromAssets(Context context, String path)
    {
        if (context == null)
        {
            return null;
        }
        StringBuilder result = new StringBuilder();
        InputStream inputStream = null;
        try
        {
            inputStream = context.getAssets().open(path);
            int ch;
            byte[] buffer = new byte[1024];
            while (-1 != (ch = inputStream.read(buffer)))
            {
                result.append(new String(buffer, 0, ch));
            }
        }
        catch (Exception e)
        {
            return null;
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        }
        return result.toString().replaceAll("\\r\\n", "\n");
    }

    public static int createShader(int type, String resource)
    {
        if (TextUtils.isEmpty(resource)) return 0;

        // 构建一个着色器
        int shader = GLES20.glCreateShader(type);
        // 加载着色器内容
        GLES20.glShaderSource(shader, resource);
        // 绑定着色器
        GLES20.glCompileShader(shader);
        int[] compileResult = new int[1];
        // 检测着色器绑定情况
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileResult, 0);
        if (compileResult[0] == 0)
        {
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    public static int createAndLinkProgram(int vertexShader, int fragmentShader)
    {
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        int[] compileResult = new int[1];
        // 检测着色器绑定情况
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, compileResult, 0);
        if (compileResult[0] == 0)
        {
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }
}
