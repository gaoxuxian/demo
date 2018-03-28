package xx.demo.activity.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import lib.ui.ShutterView;

public class ShutterActivity extends Activity implements View.OnClickListener
{
    FrameLayout mParent;
    ShutterView mShutter;
    Button mTestBtn;
    Button mTestBtn1;

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
    }

    @Override
    public void onClick(View v)
    {

    }
}
