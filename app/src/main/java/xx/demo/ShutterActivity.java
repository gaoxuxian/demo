package xx.demo;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import xx.demo.util.ShareData;
import xx.demo.util.RingEvaluator;
import xx.demo.view.BaseConfig;
import xx.demo.view.BaseView;
import xx.demo.view.GifShutterConfig;
import xx.demo.view.ShutterView;

public class ShutterActivity extends Activity implements View.OnClickListener
{
    FrameLayout mParent;
    BaseView mShutter;
    Button mTestBtn;
    private GifShutterConfig config;

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
        mParent.setBackgroundColor(Color.RED);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mParent.setLayoutParams(params);
        setContentView(mParent);
        {
            mShutter = new ShutterView(this);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            mParent.addView(mShutter, params);

            mTestBtn = new Button(this);
            mTestBtn.setText("测试");
            mTestBtn.setOnClickListener(this);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mParent.addView(mTestBtn, params);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        config = new GifShutterConfig();
        config.init();
        if (mShutter != null)
        {
            mShutter.setConfig(config);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == mTestBtn)
        {
            ObjectAnimator animator = ObjectAnimator.ofObject(mShutter, "ring", new RingEvaluator(), config.getRing(), config.getDef());
            animator.setDuration(1000);
            animator.start();
        }
    }
}
