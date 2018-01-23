package xx.demo.activity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import xx.demo.R;
import xx.demo.util.ShareData;
import xx.demo.view.ARWishView;

public class ARActivity extends Activity
{

    private FrameLayout mParent;

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
            final ARWishView view = new ARWishView(this);
            view.setParams(BitmapFactory.decodeResource(getResources(),R.drawable.ar_circle),
                    BitmapFactory.decodeResource(getResources(), R.drawable.ar_null_touxiang), "", "");
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mParent.addView(view, params);
        }
    }
}
