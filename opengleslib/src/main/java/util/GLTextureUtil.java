package util;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.text.TextUtils;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/10.
 */
public class GLTextureUtil
{
    public static void bindTexture2DParams()
    {
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    public static boolean isTextureResValid(Object res)
    {
        boolean out = false;
        if (res != null)
        {
            if (res instanceof Integer)
            {
                if (GLES20.glIsTexture((Integer) res) || (Integer) res > 0) // 纹理或者内置资源
                {
                    out = true;
                }
            }
            else if (res instanceof String)
            {
                if (!TextUtils.isEmpty((String) res) && FileUtil.isFileExists((String) res)) // 本地文件
                {
                    out = true;
                }
            }
            else if (res instanceof Bitmap)
            {
                out = true;
            }
        }
        return out;
    }
}
