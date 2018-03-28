package xx.demo.activity.media;

import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.IOException;

import xx.demo.activity.BaseActivity;
import camera.MediaMuxerWrapper;
import camera.MediaVideoEncoder;
import lib.util.ShareData;

public class RecordActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener
{
    private SurfaceView mSurfaceView;
    private Button mRecordBtn;

    private Camera mCamera;
    private MediaMuxerWrapper mMediaMuxer;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mSurfaceView = new SurfaceView(parent.getContext());
        mSurfaceView.getHolder().addCallback(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mSurfaceView, params);

        mRecordBtn = new Button(parent.getContext());
        mRecordBtn.setText("开始录制");
        mRecordBtn.setOnClickListener(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(mRecordBtn, params);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (mCamera == null)
        {
            mCamera = Camera.open();

            if (mCamera != null)
            {
                Camera.Parameters parameters = mCamera.getParameters();
                int previewW = ShareData.m_screenRealHeight;
                int previewH = ShareData.m_screenRealWidth;
                parameters.setPreviewSize(previewW, previewH);
                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(90);
                try
                {
                    mCamera.setPreviewDisplay(mSurfaceView.getHolder());
                    mCamera.startPreview();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        if (mCamera != null)
        {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onPause()
    {
        if (mSurfaceView != null)
        {
            mSurfaceView.setVisibility(View.INVISIBLE);
        }
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mSurfaceView != null)
        {
            mSurfaceView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == mRecordBtn)
        {
            if (mMediaMuxer == null)
                startRecording();
            else
                stopRecording();
        }
    }

    private void startRecording()
    {
        try
        {
            if (mRecordBtn != null)
            {
                mRecordBtn.setText("停止录制");
            }

            mMediaMuxer = new MediaMuxerWrapper();
            new MediaVideoEncoder(mMediaMuxer, ShareData.m_screenRealHeight, ShareData.m_screenRealWidth);

            mMediaMuxer.prepare();
            mMediaMuxer.startRecording();
        }
        catch (IOException e)
        {
            if (mRecordBtn != null)
            {
                mRecordBtn.setText("开始录制");
            }
        }
    }

    private void stopRecording()
    {
        if (mMediaMuxer != null)
        {
            mMediaMuxer.stopRecording();
            mMediaMuxer = null;
        }
    }
}
