package xx.demo.camera;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import xx.demo.util.CameraPercentUtil;

/**
 * Created by Gxx on 2018/2/23.
 */

public class PopSettingItem extends FrameLayout
{
    private TextView mTitleView;
    private TextView mTipsView;

    public PopSettingItem(@NonNull Context context)
    {
        super(context);
        initUI(context);
    }

    private void initUI(Context context)
    {
        mTitleView = new TextView(context);
        mTitleView.setTextColor(Color.WHITE);
        mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        mTitleView.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mTitleView, params);

        mTipsView = new TextView(context);
        mTipsView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        mTipsView.setTextColor(ColorUtils.setAlphaComponent(Color.GRAY, (int) (255 * 0.5f)));
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        addView(mTipsView, params);
    }

    public void setTitle(String title)
    {
        if (mTitleView != null)
        {
            mTitleView.setText(title);
        }
    }

    public void setTip(String tip)
    {
        if (mTipsView != null)
        {
            mTipsView.setText(tip);
        }
    }
}
