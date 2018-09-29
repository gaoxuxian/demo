package xx.demo.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v8.renderscript.Short4;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import seekbar.CirclePointConfig;
import seekbar.CirclePointSeekBar;
import seekbar.IConfig;
import seekbar.SeekBarConfigFactory;
import util.BlurUtil;
import util.PixelPercentUtil;
import xx.demo.R;

public class RenderScriptTestActivity extends BaseActivity
{
    ImageView imageView;
    ImageView seekBarBgView;
    CirclePointSeekBar seekBar;
    Bitmap testBmp;
    boolean close;
    private final String TAG = RenderScriptTestActivity.class.getName();
    TextView textView;

    ImageView centerImageView;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        testBmp = BitmapFactory.decodeResource(getResources(), R.drawable.opengl_test_3);

        imageView = new ImageView(parent.getContext());
        imageView.setImageResource(R.drawable.opengl_test_3);
        params = new FrameLayout.LayoutParams(1080, 2160);
        // params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        parent.addView(imageView, params);

        seekBarBgView = new ImageView(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelPercentUtil.HeightPxxToPercent(300));
        // params = new FrameLayout.LayoutParams(600, 600);
        params.gravity = Gravity.BOTTOM;
        params.bottomMargin = PixelPercentUtil.HeightPxxToPercent(200);
        parent.addView(seekBarBgView, params);

        seekBar = new CirclePointSeekBar(parent.getContext());
        IConfig config = SeekBarConfigFactory.createConfig(SeekBarConfigFactory.ConfigType.circle_point);
        if (config instanceof CirclePointConfig)
        {
            ((CirclePointConfig) config).mBgColor = Color.TRANSPARENT;

            ((CirclePointConfig) config).mPointSum = 11;
            ((CirclePointConfig) config).mZeroIndex = 0;
            ((CirclePointConfig) config).mSelectedValue = 0;
            ((CirclePointConfig) config).mDataType = CirclePointConfig.DataType.type_float;

            ((CirclePointConfig) config).mPointDrawType = CirclePointConfig.PointDrawType.resource;
            ((CirclePointConfig) config).mZeroPointDrawType = CirclePointConfig.PointDrawType.resource;
            ((CirclePointConfig) config).mMovableDrawType = CirclePointConfig.PointDrawType.resource;

            ((CirclePointConfig) config).mPointW = PixelPercentUtil.WidthPxxToPercent(22);
            ((CirclePointConfig) config).mMovablePointWH = PixelPercentUtil.WidthPxxToPercent(54);

            ((CirclePointConfig) config).mPointBmpResId = R.drawable.ic_rate_nor;
            ((CirclePointConfig) config).mZeroPointBmpResId = R.drawable.ic_rate_nor;
            ((CirclePointConfig) config).mMovableBmpResId = R.drawable.ic_rate_sel;

            ((CirclePointConfig) config).mMovablePointColorType = CirclePointConfig.MovablePointColorType.fixed_one_color;
            ((CirclePointConfig) config).mMovablePointColor = Color.WHITE;

            ((CirclePointConfig) config).mDistanceBetweenPointAndPoint = PixelPercentUtil.WidthPxxToPercent(72);
            ((CirclePointConfig) config).mLeftMargin = PixelPercentUtil.WidthPxxToPercent(59);
            ((CirclePointConfig) config).mRightMargin = PixelPercentUtil.WidthPxxToPercent(59);

            ((CirclePointConfig) config).mShowValuePlusLogo = true;
            ((CirclePointConfig) config).mShowSelectedValue = true;
            ((CirclePointConfig) config).mValueTextColor = Color.WHITE;
            ((CirclePointConfig) config).mValueTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            ((CirclePointConfig) config).mDistanceBetweenPointAndValue = PixelPercentUtil.HeightPxxToPercent(10);
            seekBar.setConfig((CirclePointConfig) config);
        }

        seekBar.setValueChangeListener(new CirclePointSeekBar.SeekBarValueChangeListener()
        {
            @Override
            public void onValueChange(CirclePointSeekBar seekBar, float value, float lastValue, MotionEvent event)
            {
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE)
                {
                    long start = System.currentTimeMillis();
                    Bitmap blur = blur(seekBar.getContext(), testBmp, value * 2f, "#40000000");
                    Log.d(TAG, "onStopTrackingTouch: 毛玻璃效果耗时 (/ms) == " + (System.currentTimeMillis() - start));
                    seekBarBgView.setImageBitmap(value != 0 ? blur : null);
                    // imageView.setImageBitmap(blur);
                }
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelPercentUtil.HeightPxxToPercent(300));
        params.gravity = Gravity.BOTTOM;
        params.bottomMargin = PixelPercentUtil.HeightPxxToPercent(200);
        parent.addView(seekBar, params);

        textView = new TextView(parent.getContext());
        textView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                close = !close;
                long start = System.currentTimeMillis();
                Bitmap blur = blur(seekBar.getContext(), testBmp, seekBar.getCurrentValue() * 2f, "#40000000");
                Log.d(TAG, "onStopTrackingTouch: 毛玻璃效果耗时 (/ms) == " + (System.currentTimeMillis() - start));
                // PointF center = new PointF(0.5f, 0.5f);
                // Bitmap blur = BlurUtil.sBlurEffectCirclePartV2(seekBar.getContext(), testBmp, seekBar.getCurrentValue() * 2f, close ? null : "#40000000", 1/8f, center, 300);
                seekBarBgView.setImageBitmap(seekBar.getCurrentValue() != 0 ? blur : null);
                // imageView.setImageBitmap(blur);

                textView.setText(close ? "关闭蒙版" : "打开蒙版");
            }
        });
        textView.setText("打开蒙版");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        textView.setTextColor(Color.RED);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(textView, params);

        centerImageView = new ImageView(parent.getContext());
        params = new FrameLayout.LayoutParams(600, 600);
        params.gravity = Gravity.CENTER;
        parent.addView(centerImageView, params);
    }

    public Bitmap blur(Context context, Bitmap src, float radius, String color)
    {
        PointF center = new PointF(0.5f, 0.5f);

        // RectF rect = new RectF(0, (2160 - 500) / (float)2160, 1, (2160 - 200) / (float) 2160);

        return BlurUtil.sBlurEffectOrder(context, src, radius, close ? null : color, 1/8f);
    }
}
