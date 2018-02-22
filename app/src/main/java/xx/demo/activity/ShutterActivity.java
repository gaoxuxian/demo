package xx.demo.activity;

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
import xx.demo.view.GifShutterConfig;
import xx.demo.view.ShutterView;

public class ShutterActivity extends Activity implements View.OnClickListener
{
    FrameLayout mParent;
    ShutterView mShutter;
    Button mTestBtn;
    Button mTestBtn1;
    private GifShutterConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
            mTestBtn.setText("def --> small");
            mTestBtn.setOnClickListener(this);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mParent.addView(mTestBtn, params);

            mTestBtn1 = new Button(this);
            mTestBtn1.setText("small --> def");
            mTestBtn1.setOnClickListener(this);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            mParent.addView(mTestBtn1, params);
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
            ObjectAnimator animator = ObjectAnimator.ofObject(mShutter, "ring", new RingEvaluator(), config.getDef(), config.getSmall());
            animator.setDuration(1000);
            animator.start();
        }
        else if (v == mTestBtn1)
        {
            ObjectAnimator animator = ObjectAnimator.ofObject(mShutter, "ring", new RingEvaluator(), config.getSmall(), config.getDef());
            animator.setDuration(1000);
            animator.start();
        }
    }
}
