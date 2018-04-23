package xx.demo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public abstract class BaseActivity extends Activity implements BaseActivityInterface
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        onCreateInitData();
        onCreateUI(this);
        onCreateFinal();
    }

    public void onCreateUI(Context context)
    {
        FrameLayout mParent = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mParent.setLayoutParams(params);
        setContentView(mParent);

        createChildren(mParent, params);
    }
}
