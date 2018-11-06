package xx.demo.activity.life;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStore;
import xx.demo.R;
import xx.demo.activity.BaseActivity;

public class LifecycleActivity extends BaseActivity
{
    private MyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ViewModelStore viewModelStore = getViewModelStore();
        viewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        getLifecycle().addObserver(new LifecycleModel(viewModel));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        new ClassInofModel();
        Button btn = new Button(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        if (viewModel != null)
        {
            viewModel.getData().observe(this, new Observer<ArrayList<String>>()
            {
                @Override
                public void onChanged(ArrayList<String> strings)
                {
                    if (strings != null && strings.size() > 1)
                    {
                        btn.setText(strings.get(1));
                    }
                }
            });
        }
        parent.addView(btn, params);
    }
}
