package util;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.text.TextUtils;
import android.util.Log;

public class GLES20Util
{
    private final static String TAG = GLES20Util.class.getName();

    public static void sGetFrustumM(float[] matrix, int imgWidth, int imgHeight, int viewWidth, int viewHeight)
    {
        if (imgWidth > 0 && imgHeight > 0 && viewWidth > 0 && viewHeight > 0)
        {
            float sWH = imgWidth / (float) imgHeight;
            float sWidthHeight = viewWidth / (float) viewHeight;

            float[] projectMatrix = new float[16];
            float[] viewMatrix = new float[16];

            float scaleX = (float) viewWidth / imgWidth;
            float scaleY = (float) viewHeight / imgHeight;

            float scale = Math.min(scaleX, scaleY);
            if (sWH == sWidthHeight)
            {
                Matrix.frustumM(projectMatrix, 0, -1, 1, -1 / sWidthHeight, 1 / sWidthHeight, 3, 5);
            }
            else if (scale == scaleX)
            {
                Matrix.frustumM(projectMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 5);
            }
            else if (scale == scaleY)
            {
                Matrix.frustumM(projectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 5);
            }
            //设置相机位置
            Matrix.setLookAtM(viewMatrix, 0, 0, 0, 3.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            //计算变换矩阵
            Matrix.multiplyMM(matrix, 0, projectMatrix, 0, viewMatrix, 0);
        }
    }

    public static float[] sGetOpenGLUnitMatrix()
    {
        float[] out = new float[16];
        Matrix.setIdentityM(out, 0);
        return out;
    }

    // =================================== 着色器 start ====================================== //

    public static int sGetShader(int type, String resource)
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

    public static int sGetShader(Context context, int type, String assetsResPath)
    {
        if (TextUtils.isEmpty(assetsResPath)) return 0;

        return sGetShader(type, FileUtil.getAssetsResource(context.getResources(), assetsResPath));
    }

    public static int sGetShader(Resources res, int type, String assetsResPath)
    {
        if (TextUtils.isEmpty(assetsResPath)) return 0;

        return sGetShader(type, FileUtil.getAssetsResource(res, assetsResPath));
    }

    // =================================== 着色器 end ====================================== //

    // =================================== GL Program start ====================================== //

    public static int sCreateProgram(int vertexShader, int fragmentShader)
    {
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        return program;
    }

    public static int sCreateAndLinkProgram(int vertexShader, int fragmentShader)
    {
        int program = sCreateProgram(vertexShader, fragmentShader);
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

    // =================================== GL Program end ====================================== //
}
