package xx.demo.activity.view;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import lib.util.PixelPercentUtil;
import seekbar.CirclePointConfig;
import seekbar.CirclePointSeekBar;
import seekbar.IConfig;
import seekbar.SeekBarConfigFactory;
import xx.demo.activity.BaseActivity;

public class SeekBarActivity extends BaseActivity
{
    private CirclePointSeekBar mSeekBar;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mSeekBar = new CirclePointSeekBar(parent.getContext());
        mSeekBar.setCanTouchMaxValue(4);
        IConfig config = SeekBarConfigFactory.createConfig(SeekBarConfigFactory.ConfigType.circle_point);
        if (config instanceof CirclePointConfig)
        {
            ((CirclePointConfig) config).mPointSum = 11;
            ((CirclePointConfig) config).mZeroIndex = 5;
            ((CirclePointConfig) config).mSelectedValue = 3;
            ((CirclePointConfig) config).mDataType = CirclePointConfig.DataType.type_float;
            ((CirclePointConfig) config).mPointWH = PixelPercentUtil.WidthPxxToPercent(40);
            ((CirclePointConfig) config).mMovablePointWH = PixelPercentUtil.WidthPxxToPercent(60);
            ((CirclePointConfig) config).mMovablePointColorType = CirclePointConfig.MovablePointColorType.gradient;
            ((CirclePointConfig) config).mMovablePointColor = Color.GREEN;
            ((CirclePointConfig) config).mPointColorArr = new int[]{
            0xffdd1611, 0xffe23a35, 0xffeb706a, 0xfff4a39d, 0xfffbd1cb,  Color.WHITE, 0xfffde4e9, 0xfff5bacc, 0xffee9bb5, 0xffdd4070, 0xffd4114d};
            ((CirclePointConfig) config).mPointDrawType = CirclePointConfig.PointDrawType.self;
            ((CirclePointConfig) config).mZeroPointDrawType = CirclePointConfig.PointDrawType.self;
            ((CirclePointConfig) config).mDistanceBetweenPointAndPoint = PixelPercentUtil.WidthPxxToPercent(50);
            ((CirclePointConfig) config).mLeftMargin = PixelPercentUtil.WidthPxxToPercent(70);
            ((CirclePointConfig) config).mRightMargin = PixelPercentUtil.WidthPxxToPercent(70);
            ((CirclePointConfig) config).mShowValuePlusLogo = true;
            ((CirclePointConfig) config).mShowSelectedValue = true;
            ((CirclePointConfig) config).mValueTextColor = Color.WHITE;
            ((CirclePointConfig) config).mValueTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            ((CirclePointConfig) config).mDistanceBetweenPointAndValue = PixelPercentUtil.HeightPxxToPercent(10);
            mSeekBar.setConfig((CirclePointConfig) config);
        }
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelPercentUtil.HeightPxxToPercent(300));
        parent.addView(mSeekBar, params);
    }
}
