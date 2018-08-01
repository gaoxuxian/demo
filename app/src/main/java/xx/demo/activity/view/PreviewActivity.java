package xx.demo.activity.view;

import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import xx.demo.R;
import xx.demo.activity.BaseActivity;

public class PreviewActivity extends BaseActivity
{
    private PreviewViewV2 mView;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mView = new PreviewViewV2(parent.getContext());
        mView.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.heighten_test_bmp));
        mView.setWaterMark(BitmapFactory.decodeResource(getResources(), R.drawable.ic_watermark_big_3));
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mView, params);

        Button button = new Button(parent.getContext());
        button.setText("上升");
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mView.setWaterMarkTranslationY(300);
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        parent.addView(button, params);

        button = new Button(parent.getContext());
        button.setText("下降");
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mView.setWaterMarkTranslationY(0);
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        parent.addView(button, params);
    }
}
