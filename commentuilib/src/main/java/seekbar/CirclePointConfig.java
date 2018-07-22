package seekbar;

import android.graphics.Color;

public class CirclePointConfig implements IConfig
{
    public @interface PointDrawType
    {
        int self = 0; // 自己控制参数画
        int resource = 1; // 画图
    }

    public @interface DataType
    {
        int type_int = 0;
        int type_float = 1;
    }

    public @interface MovablePointColorType
    {
        int fixed_one_color = 0; // 固定一种颜色
        int gradient = 1; // 渐变色
    }

    // =================================== 必须参数 =============================== //
    public int mPointSum; // 点的总数

    public int mZeroIndex; // 原点下标，从 0 开始

    /**
     * 选中的数值, 并非下标，具体数据, 与 {@link CirclePointConfig#mDataType} 有关
     */
    public float mSelectedValue;

    /**
     * 选中的数据的类型 {@link DataType}
     */
    public int mDataType;

    public int mZeroPointDrawType; // 原点的画法

    public int mPointDrawType; // 点的画法

    public int mPointWH; // 点的大小

    public int mMovablePointWH; // 可操作的点的大小

    public int[] mPointColorArr; // 点的颜色

    public float mDistanceBetweenPointAndPoint; // 点与点的距离

    public int mLeftMargin; // 第一个点与左边缘的距离

    public int mRightMargin; // 最后一个点与右边缘的距离

    // =================================== 非必须参数 ============================= //

    public int mZeroPointBmpResId; // 原点的图片资源

    public int mPointBmpResId; // 点的图片资源

    /**
     * 可操作的点的颜色类型 {@link MovablePointColorType}
     * <p>
     * 若类型是 {@link MovablePointColorType#gradient}, {@link CirclePointConfig#mMovablePointColor} 将没有意义
     * <p>
     * 会根据 {@link CirclePointConfig#mPointColorArr} 和 实际操作过程中，选中的位置判断渐变色
     */
    public int mMovablePointColorType = MovablePointColorType.fixed_one_color;

    /**
     * 可操作的点的颜色, 该参数与 {@link CirclePointConfig#mMovablePointColorType} 有关
     */
    public int mMovablePointColor = Color.WHITE;

    public boolean mShowSelectedValue; // 是否显示选中的数值文案

    public float mDistanceBetweenPointAndValue = 10; // 点与数值间的距离

    public float mValueTextSize = 10;

    public int mValueTextColor = Color.WHITE;

    public boolean mShowValuePlusLogo; // 如果 数值文案 > 0 ，是否显示 + 号

    public float mPointsTranslationY; // 默认点是居中view 画，+往下偏，-往上偏
}
