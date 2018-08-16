package xx.demo.activity.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import heighten.HeightenView;
import util.PixelPercentUtil;
import xx.demo.R;
import xx.demo.activity.BaseActivity;

public class HeightenActivity extends BaseActivity
{
    private HeightenView mItemView;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.opengl_test_6);

        mItemView = new HeightenView(parent.getContext());
        mItemView.setBitmap(bitmap);
        mItemView.setBackgroundColor(Color.GRAY);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelPercentUtil.HeightPxxToPercent(1440));
        params.topMargin = PixelPercentUtil.HeightPxxToPercent(100);
        parent.addView(mItemView, params);

        Button btn = new Button(parent.getContext());
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ViewGroup.LayoutParams layoutParams = mItemView.getLayoutParams();
                layoutParams.height += 100;
                mItemView.requestLayout();
            }
        });
        btn.setText("增加 100 高度");
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        parent.addView(btn, params);

        btn = new Button(parent.getContext());
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ViewGroup.LayoutParams layoutParams = mItemView.getLayoutParams();
                layoutParams.height -= 100;
                mItemView.requestLayout();
            }
        });
        btn.setText("减少 100 高度");
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        parent.addView(btn, params);

        btn = new Button(parent.getContext());
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mItemView.showHeightChanged(true);
                mItemView.setIncreasedHeight(100);
                mItemView.update();
            }
        });
        btn.setText("拉伸 100");
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        parent.addView(btn, params);

        btn = new Button(parent.getContext());
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mItemView.showHeightChanged(true);
                mItemView.setIncreasedHeight(-100);
                mItemView.update();
            }
        });
        btn.setText("缩短 100");
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        parent.addView(btn, params);
    }
}
