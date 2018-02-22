package xx.demo.activity;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;

public class CameraActivity extends BaseActivity implements TextureView.SurfaceTextureListener, SurfaceHolder.Callback
{
    private TextureView mTextureView; // 感觉比 surface view 有延迟
    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private String TAG = "xxx";

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
//        mTextureView = new TextureView(parent.getContext());
//        mTextureView.setSurfaceTextureListener(this);
//        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        params.gravity = Gravity.CENTER;
//        parent.addView(mTextureView, params);

        mSurfaceView = new SurfaceView(parent.getContext());
        mSurfaceView.getHolder().addCallback(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        parent.addView(mSurfaceView, params);
    }

    // texture view

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
    {
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);

        if (mCamera != null)
        {
            try
            {
                mCamera.setPreviewTexture(surface);
                mCamera.startPreview();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
    {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
    {
        if (mCamera != null)
        {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface)
    {

    }

    // surface view

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.d(TAG, "CameraActivity --> surfaceCreated: ");

        mCamera = Camera.open();

        if (mCamera != null)
        {
            mCamera.setDisplayOrientation(90);
            try
            {
                mCamera.setPreviewDisplay(holder);
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
        Log.d(TAG, "CameraActivity --> surfaceChanged: ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.d(TAG, "CameraActivity --> surfaceDestroyed: ");

        if (mSurfaceView != null)
        {
            mSurfaceView.getHolder().removeCallback(this);
        }

        if (mCamera != null)
        {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
