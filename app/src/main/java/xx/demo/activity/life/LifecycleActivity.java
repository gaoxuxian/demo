package xx.demo.activity.life;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.FrameLayout;

import xx.demo.R;
import xx.demo.activity.BaseActivity;

public class LifecycleActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new LifecycleModel());
        getLifecycle().addObserver(new LifecycleModel());
    }

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {

    }
}
