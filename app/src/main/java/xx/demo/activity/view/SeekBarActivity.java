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
import xx.demo.R;
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

            ((CirclePointConfig) config).mPointDrawType = CirclePointConfig.PointDrawType.resource;
            ((CirclePointConfig) config).mZeroPointDrawType = CirclePointConfig.PointDrawType.resource;
            ((CirclePointConfig) config).mMovableDrawType = CirclePointConfig.PointDrawType.resource;

            ((CirclePointConfig) config).mPointW = PixelPercentUtil.WidthPxxToPercent(22);
            ((CirclePointConfig) config).mMovablePointWH = PixelPercentUtil.WidthPxxToPercent(54);

            ((CirclePointConfig) config).mPointBmpResId = R.drawable.ic_rate_nor;
            ((CirclePointConfig) config).mZeroPointBmpResId = R.drawable.ic_rate_nor_original_double;
            ((CirclePointConfig) config).mMovableBmpResId = R.drawable.ic_rate_sel;

            ((CirclePointConfig) config).mMovablePointColorType = CirclePointConfig.MovablePointColorType.gradient;
            ((CirclePointConfig) config).mMovablePointColor = Color.WHITE;
            ((CirclePointConfig) config).mPointColorArr = new int[]{
            0xffdd1611, 0xffe23a35, 0xffeb706a, 0xfff4a39d, 0xfffbd1cb,  Color.WHITE, 0xfffde4e9, 0xfff5bacc, 0xffee9bb5, 0xffdd4070, 0xffd4114d};

            ((CirclePointConfig) config).mDistanceBetweenPointAndPoint = PixelPercentUtil.WidthPxxToPercent(72);
            ((CirclePointConfig) config).mLeftMargin = PixelPercentUtil.WidthPxxToPercent(59);
            ((CirclePointConfig) config).mRightMargin = PixelPercentUtil.WidthPxxToPercent(59);

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
