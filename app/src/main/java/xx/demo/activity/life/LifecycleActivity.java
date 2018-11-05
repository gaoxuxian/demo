package xx.demo.activity.life;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.lifecycle.ViewModelProviders;
import xx.demo.R;
import xx.demo.activity.BaseActivity;

public class LifecycleActivity extends BaseActivity
{
    private MyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        viewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        getLifecycle().addObserver(new LifecycleModel(viewModel));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        Button btn = new Button(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        if (viewModel != null)
        {
            viewModel.getData().observe(this, btn::setText);
        }
        parent.addView(btn, params);
    }
}
