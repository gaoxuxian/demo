package xx.demo.activity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import xx.demo.R;
import xx.demo.util.ShareData;
import xx.demo.view.ARWishView;
import xx.demo.view.ARWishViewV3;

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
        mParent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("xxx", "ARActivity --> onClick: parent");
            }
        });
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mParent.setLayoutParams(params);
        setContentView(mParent);
        {
            final ARWishViewV3 view = new ARWishViewV3(this);
            view.setBitmap(R.drawable.ar_circle);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mParent.addView(view, params);
        }
    }
}
