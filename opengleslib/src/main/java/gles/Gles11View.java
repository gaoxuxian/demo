package gles;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.opengl.GLSurfaceView;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import filter.camera.CameraPreviewFilter;

import static android.hardware.Camera.Parameters.WHITE_BALANCE_AUTO;

public class Gles11View extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private Camera mCamera;
    private float mPreviewProportion;
    private CameraPreviewFilter mCameraFilter;

    public Gles11View(Context context)
    {
        super(context);
        mCameraFilter = new CameraPreviewFilter(getResources());
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public void setPreviewProportion(float heightWidth)
    {
        mPreviewProportion = heightWidth;
    }

    public void startPreview()
    {
        requestRender();
    }

    public void restartCamera()
    {
        if (mCamera != null)
        {
            mCamera.stopPreview();
            openCamera();
            requestRender();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        mCameraFilter.onSurfaceCreated(gl, config);
        openCamera();
    }

    private void openCamera()
    {
        try
        {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (mCamera != null)
        {
            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            Camera.Size temp = null;
            for (Camera.Size size : supportedPreviewSizes)
            {
                if (size != null && ((float) size.width / size.height) == mPreviewProportion)
                {
                    if (temp == null)
                    {
                        temp = size;
                    }
                    else if (temp.width < size.width || temp.height < size.height)
                    {
                        temp = size;
                    }
                }
            }

            if (temp != null)
            {
                parameters.setPreviewSize(temp.width, temp.height);
                mCameraFilter.setPreviewSize(temp.height, temp.width);
            }
            else
            {
                parameters.setPreviewSize(1920, 1080);
                mCameraFilter.setPreviewSize(1080, 2160);
            }

            parameters.setWhiteBalance(WHITE_BALANCE_AUTO);
            parameters.setAutoExposureLock(false);

            mCamera.setParameters(parameters);
            try
            {
                mCamera.setPreviewTexture(mCameraFilter.getSurfaceTexture());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            mCameraFilter.getSurfaceTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener()
            {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture)
                {
                    requestRender();
                }
            });
            mCamera.startPreview();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mCameraFilter.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        mCameraFilter.onDrawFrame(gl);
    }

    public void onClear()
    {
        if (mCamera != null)
        {
            mCamera.stopPreview();
            mCamera.release();
        }
    }
}
