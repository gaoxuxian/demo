package xx.demo.activity;

import android.animation.ValueAnimator;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import lib.ui.PictureTestView;
import util.PxUtil;

public class TestActivity extends BaseActivity
{
    private PictureTestView mTestView;
    private SeekBar progressBar;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mTestView = new PictureTestView(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mTestView, params);

        progressBar = new SeekBar(parent.getContext());
        progressBar.setMax(100);
        progressBar.setProgress(0);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sV_1080p(100));
        parent.addView(progressBar, params);

        Button button = new Button(parent.getContext());
        button.setText("测试");
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ValueAnimator animator = ValueAnimator.ofInt(0, 100);
                animator.setDuration(3000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        Object value = animation.getAnimatedValue();
                        if (value instanceof Integer)
                        {
                            progressBar.setProgress((int) value);

                            if (mTestView != null)
                            {
                                mTestView.requireRender((int) value * 2);
                            }
                        }
                    }
                });
                animator.start();
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        parent.addView(button, params);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }
}
