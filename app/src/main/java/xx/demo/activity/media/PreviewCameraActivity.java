package xx.demo.activity.media;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.List;

import util.PxUtil;
import xx.demo.activity.BaseActivity;

/**
 * 用 surface view 做镜头预览
 */
public class PreviewCameraActivity extends BaseActivity implements SurfaceHolder.Callback, Camera.PreviewCallback
{
    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private byte[] mPreBuffer;
    private int mBufferSize;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mSurfaceView = new SurfaceView(parent.getContext());
        // surface view 生命周期监听
        mSurfaceView.getHolder().addCallback(this);
        params = new FrameLayout.LayoutParams(PxUtil.sScreenRealWidth, PxUtil.sScreenRealWidth);
        parent.addView(mSurfaceView, params);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        if (mCamera != null)
        {
            mCamera.setDisplayOrientation(90);
            try
            {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_EDOF);
                List<String> supportedWhiteBalance = parameters.getSupportedWhiteBalance();
                for (String str : supportedWhiteBalance)
                {
                    Log.d("xxx", "PreviewCameraActivity --> surfaceCreated: " + str);
                }
                parameters.setPreviewSize(1088, 1088);
                mCamera.setParameters(parameters);
                /**
                 * api 翻译：
                 * 1.可以多次添加buffer到queue，如果preview帧到达时该buffer可用就从queue取出来；如果不可用就丢弃该帧；
                 * 2.每次preview并且buffer可用的时候callback会被重复调用；
                 * 3.通过对buffer内存的重用可以提高preview效率和帧率；
                 * 至于适用场景，应该是需要实时预览，并且对质量和帧率要求很高的时候。
                 */
                float bitsPerPixel = ImageFormat.getBitsPerPixel(parameters.getPreviewFormat()) / 8f;
                int preW = parameters.getPreviewSize().width;
                int preH = parameters.getPreviewSize().height;
                mBufferSize = (int) (preW * preH * bitsPerPixel);
                if (mPreBuffer == null)
                {
                    mPreBuffer = new byte[mBufferSize];
                }
                mCamera.addCallbackBuffer(mPreBuffer);
                mCamera.setPreviewCallbackWithBuffer(this);
                mCamera.setPreviewDisplay(mSurfaceView.getHolder());
                mCamera.startPreview();
            }
            catch (IOException e)
            {
                e.printStackTrace();
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
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onPause()
    {
        // 更改 surface view 可见性，生命周期方法会被调用
        if (mSurfaceView != null)
        {
            mSurfaceView.setVisibility(View.GONE);
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
    public void onPreviewFrame(byte[] data, Camera camera)
    {
        if (mPreBuffer == null && mBufferSize != 0)
        {
            mPreBuffer = new byte[mBufferSize];
        }

        mCamera.addCallbackBuffer(mPreBuffer);
    }
}
