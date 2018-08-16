package xx.demo.gles;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import gles.Gles11View;
import util.ShareData;
import xx.demo.activity.BaseActivity;

public class GlesActivity11 extends BaseActivity
{
    private Gles11View mItemView;
    private float mPreviewProportion = (float) 16/9;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mItemView = new Gles11View(parent.getContext());
        mItemView.setPreviewProportion(mPreviewProportion);
        mItemView.startPreview();
        params = new FrameLayout.LayoutParams(ShareData.m_screenRealWidth, ShareData.m_screenRealHeight);
        parent.addView(mItemView, params);

        Button btn = new Button(parent.getContext());
        btn.setText("切换预览比例");
        btn.setOnClickListener(v -> {
            if (mPreviewProportion == (float) ShareData.m_screenRealHeight / ShareData.m_screenRealWidth)
            {
                mPreviewProportion = (float) 16/9;
            }
            else if (mPreviewProportion == (float) 16/9)
            {
                mPreviewProportion = (float) 4/3;
            }
            else if (mPreviewProportion == (float) 4/3)
            {
                mPreviewProportion = 1;
            }
            else if (mPreviewProportion == 1)
            {
                mPreviewProportion = (float) ShareData.m_screenRealHeight / ShareData.m_screenRealWidth;
            }
            mItemView.setPreviewProportion(mPreviewProportion);
            mItemView.restartCamera();
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(btn, params);
    }

    @Override
    protected void onPause()
    {
        mItemView.onPause();
        mItemView.onClear();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mItemView.onResume();
    }
}
