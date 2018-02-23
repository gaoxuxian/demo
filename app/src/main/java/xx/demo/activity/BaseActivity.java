package xx.demo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public abstract class BaseActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initData();
        onCreateUI(this);
    }

    protected void initData()
    {

    }

    public void onCreateUI(Context context)
    {
        FrameLayout mParent = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mParent.setLayoutParams(params);
        setContentView(mParent);

        createChildren(mParent, params);
    }

    public abstract void createChildren(FrameLayout parent, FrameLayout.LayoutParams params);

}
