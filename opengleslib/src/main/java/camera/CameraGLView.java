package camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.io.IOException;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraGLView extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private Camera mCamera;
    private CameraDrawer mCameraDrawer;

    public CameraGLView(Context context)
    {
        super(context);

        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        mCameraDrawer = new CameraDrawer(getResources());
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        mCameraDrawer.onSurfaceCreated(gl,config);

        mCamera = Camera.open(1);

        if (mCamera != null)
        {
            try
            {
                mCameraDrawer.setCameraId(1);

                Camera.Parameters parameters = mCamera.getParameters();
                List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
                if (sizeList != null)
                {
                    for (Camera.Size size : sizeList)
                    {
                        if (size.height == 1080 && size.width == 1440)
                        {
                            parameters.setPreviewSize(size.width, size.height);
                            break;
                        }
                    }
                }
                mCamera.setParameters(parameters);
                mCameraDrawer.setDataSize(parameters.getPreviewSize().height, parameters.getPreviewSize().width);

                mCamera.setPreviewTexture(mCameraDrawer.getSurfaceTexture());

                mCameraDrawer.getSurfaceTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener()
                {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture)
                    {
                        requestRender();
                    }
                });

                mCamera.startPreview();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mCameraDrawer.onSurfaceChanged(gl, width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        mCameraDrawer.onDrawFrame(gl);
    }

    public void onStop()
    {
        if (mCamera != null)
        {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
