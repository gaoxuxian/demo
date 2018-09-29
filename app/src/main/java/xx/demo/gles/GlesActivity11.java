package xx.demo.gles;

import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.PixelCopy;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.List;

import gles.Gles11View;
import util.ShareData;
import util.ThreadUtil;
import xx.demo.activity.BaseActivity;

public class GlesActivity11 extends BaseActivity
{
    private Gles11View mItemView;
    private float mPreviewProportion = (float) ShareData.m_screenRealHeight / ShareData.m_screenRealWidth;

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

    private int time;
    private Bitmap out;
    @Override
    protected void onResume()
    {
        super.onResume();
        mItemView.onResume();

        Log.d("xxx", "onResume: 手机型号 == " + Build.MODEL);

        ThreadUtil.runOnUiThreadDelay(new Runnable()
        {
            @Override
            public void run()
            {
                Handler handler = new Handler(getMainLooper());
                out = Bitmap.createBitmap(360, 720, Bitmap.Config.ARGB_8888);
                if (Build.VERSION.SDK_INT >= 26)
                {
                    long start = System.currentTimeMillis();
                    PixelCopy.request(mItemView, out, new PixelCopy.OnPixelCopyFinishedListener()
                    {
                        @Override
                        public void onPixelCopyFinished(int copyResult)
                        {
                            if (copyResult == PixelCopy.SUCCESS)
                            {
                                Log.d("xxx", "onPixelCopyFinished: 耗时== " + (System.currentTimeMillis() - start));
                                if (out != null)
                                {

                                }
                            }
                        }
                    }, handler);
                }
            }
        }, 2000);
    }
}
