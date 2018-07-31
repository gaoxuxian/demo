package xx.demo.activity.view;

import android.graphics.Matrix;

public class Shape
{
    public Matrix mOwnMatrix; // 用于自身缩放、平移
    public Matrix mExtraMatrix; // 用于额外变换

    public Matrix mCurrentStateMatrix;

    public Shape()
    {
        mOwnMatrix = new Matrix();
        mExtraMatrix = new Matrix();
        mCurrentStateMatrix = new Matrix();
    }
}
