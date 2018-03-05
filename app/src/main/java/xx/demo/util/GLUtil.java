package xx.demo.util;

import android.opengl.Matrix;

public class GLUtil
{
    public static void getShowMatrix(float[] matrix, int imgWidth, int imgHeight, int viewWidth, int viewHeight)
    {
        if (imgWidth > 0 && imgHeight > 0 && viewWidth > 0 && viewHeight > 0)
        {
            float sWH = imgWidth / (float) imgHeight;
            float sWidthHeight = viewWidth / (float) viewHeight;

            float[] projectMatrix = new float[16];
            float[] viewMatrix = new float[16];

            if (viewWidth > viewHeight)
            {
                if (sWH > sWidthHeight)
                {
                    Matrix.frustumM(projectMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 5);
                }
                else
                {
                    Matrix.frustumM(projectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 5);
                }
            }
            else
            {
                if (sWH > sWidthHeight)
                {
                    Matrix.frustumM(projectMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 5);
                }
                else
                {
                    Matrix.frustumM(projectMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 5);
                }
            }
            //设置相机位置
            Matrix.setLookAtM(viewMatrix, 0, 0, 0, 3.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            //计算变换矩阵
            Matrix.multiplyMM(matrix, 0, projectMatrix, 0, viewMatrix, 0);
        }
    }

    public void getShowOrtMatrix(float[] matrix, int imgWidth, int imgHeight, int viewWidth, int viewHeight)
    {
        if (imgWidth > 0 && imgHeight > 0 && viewWidth > 0 && viewHeight > 0)
        {
            float sWH = imgWidth / (float) imgHeight;
            float sWidthHeight = viewWidth / (float) viewHeight;

            float[] projection = new float[16];
            float[] camera = new float[16];

            float sWhView = (float) viewWidth / viewHeight;
            float sWhImg = (float) imgWidth / imgHeight;
            if (sWhImg > sWhView)
            {
                Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1, 1, 1, 3);
            }
            else
            {
                Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
            }
            Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
            Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
        }
    }

    public static float[] rotate(float[] m, float angle)
    {
        Matrix.rotateM(m, 0, angle, 0, 0, 1);
        return m;
    }

    public static float[] flip(float[] m, boolean x, boolean y)
    {
        if (x || y)
        {
            Matrix.scaleM(m, 0, x ? -1 : 1, y ? -1 : 1, 1);
        }
        return m;
    }
}
