package xx.demo;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import xx.demo.util.CameraPercentUtil;
import xx.demo.util.ShareData;
import xx.demo.view.BaseView;

public class ShutterActivity extends Activity implements View.OnClickListener
{
    FrameLayout mParent;
    BaseView mShutter;
    Button mTestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ShareData.InitData(this);
        initView();
    }

    private void initView()
    {
        mParent = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mParent.setLayoutParams(params);
        setContentView(mParent);
        {
            mTestBtn = new Button(this);
            mTestBtn.setText("测试动态改变控件宽高");
            mTestBtn.setOnClickListener(this);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mParent.addView(mTestBtn, params);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == mTestBtn)
        {
            ValueAnimator animator = ValueAnimator.ofInt(CameraPercentUtil.WidthPxToPercent(100), CameraPercentUtil.WidthPxToPercent(500));
            animator.setDuration(1000);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    int value = (int)animation.getAnimatedValue();
                    if (mShutter != null)
                    {
                        mShutter.ReLayout(value, value);
                    }
                }
            });
            animator.start();
        }
    }
}
