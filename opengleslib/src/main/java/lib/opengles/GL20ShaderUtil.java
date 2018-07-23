package lib.opengles;

import android.content.Context;
import android.opengl.GLES20;
import android.text.TextUtils;

import lib.util.FileUtil;

public class GL20ShaderUtil
{
    public static int getShader(int type, String resource)
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

    public static int getShader(Context context, int type, String assetsResPath)
    {
        if (TextUtils.isEmpty(assetsResPath)) return 0;

        return getShader(type, FileUtil.getAssetsResource(context.getResources(), assetsResPath));
    }
}
