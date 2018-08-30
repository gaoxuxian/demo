package xx.demo.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import lib.ui.DrawerLayout;

public class TestActivity extends BaseActivity
{

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        DrawerLayout layout = new DrawerLayout(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        parent.addView(layout, params);
    }
}
