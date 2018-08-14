package lib.opengles;

import android.opengl.Matrix;

import java.util.Arrays;
import java.util.Stack;

public class VaryTools
{
    private float[] mMatrixCamera;      //相机矩阵
    private float[] mMatrixProjection;  //投影矩阵
    private float[] mMatrixCurrent;     //原始矩阵

    private Stack<float[]> mStack;      //变换矩阵堆栈

    public VaryTools()
    {
        mStack = new Stack<>();

        mMatrixCamera = new float[16];
        mMatrixProjection = new float[16];

        mMatrixCurrent = new float[]{
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        };
    }

    //保护现场
    public void pushMatrix()
    {
        mStack.push(Arrays.copyOf(mMatrixCurrent, 16));
    }

    //恢复现场
    public void popMatrix()
    {
        mMatrixCurrent = mStack.pop();
    }

    public void clearStack()
    {
        mStack.clear();
    }

    //平移变换
    public void translate(float x, float y, float z)
    {
        Matrix.translateM(mMatrixCurrent, 0, x, y, z);
    }

    //旋转变换
    public void rotate(float angle, float x, float y, float z)
    {
        Matrix.rotateM(mMatrixCurrent, 0, angle, x, y, z);
    }

    //缩放变换
    public void scale(float x, float y, float z)
    {
        Matrix.scaleM(mMatrixCurrent, 0, x, y, z);
    }

    //设置相机
    public void setCamera(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ)
    {
        Matrix.setLookAtM(mMatrixCamera, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    public void frustum(float left, float right, float bottom, float top, float near, float far)
    {
        Matrix.frustumM(mMatrixProjection, 0, left, right, bottom, top, near, far);
    }

    public void ortho(float left, float right, float bottom, float top, float near, float far)
    {
        Matrix.orthoM(mMatrixProjection, 0, left, right, bottom, top, near, far);
    }

    public float[] getFinalMatrix()
    {
        float[] ans = new float[16];
        Matrix.multiplyMM(ans, 0, mMatrixCamera, 0, mMatrixCurrent, 0);
        Matrix.multiplyMM(ans, 0, mMatrixProjection, 0, ans, 0);
        return ans;
    }
}
