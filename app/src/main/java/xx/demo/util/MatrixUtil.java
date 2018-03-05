package xx.demo.util;

import android.graphics.Matrix;

public class MatrixUtil
{

    /**
     * @return OpenGL 单位矩阵
     */
    public static float[] getOpenGLUnitMatrix(){
        return new float[]{
            1,0,0,0,
            0,1,0,0,
            0,0,1,0,
            0,0,0,1
        };
    }

    /**
     * @return 图形库 单位矩阵
     */
    public static Matrix getGraphicsUnitMatrix()
    {
        return new Matrix();
    }
}
