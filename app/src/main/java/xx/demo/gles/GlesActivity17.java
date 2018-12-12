package xx.demo.gles;

import androidx.appcompat.app.AppCompatActivity;
import gles.GLes17View;
import xx.demo.activity.BaseActivity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class GlesActivity17 extends BaseActivity
{

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        GLes17View view = new GLes17View(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(view, params);
    }

    @Override
    public void onCreateInitData()
    {

    }
}
