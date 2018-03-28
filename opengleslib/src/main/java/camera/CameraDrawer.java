package camera;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import lib.opengles.CameraFilter;
import lib.opengles.GLUtil;

public class CameraDrawer implements GLSurfaceView.Renderer
{
    private SurfaceTexture mSurfaceTexture;
    private CameraFilter mFilter;

    private int dataWidth;
    private int dataHeight;
    private int cameraId;
    private int width;
    private int height;
    private float[] matrix = new float[16];

    public CameraDrawer(Resources resources)
    {
        mFilter = new CameraFilter(resources);
    }

    public void setDataSize(int dataWidth, int dataHeight)
    {
        this.dataWidth = dataWidth;
        this.dataHeight = dataHeight;
        calculateMatrix();
    }

    public void setViewSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        calculateMatrix();
    }

    public void setCameraId(int id)
    {
        this.cameraId = id;
        calculateMatrix();
    }

    private void calculateMatrix()
    {
        GLUtil.getShowMatrix(matrix, this.dataWidth, this.dataHeight, this.width, this.height);
        if (cameraId == 1)
        {
            GLUtil.flip(matrix, true, false);
            GLUtil.rotate(matrix, 90);
        }
        else
        {
            GLUtil.rotate(matrix, 270);
        }
        mFilter.setMatrix(matrix);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        int textureID = mFilter.createTextureID();
        mSurfaceTexture = new SurfaceTexture(textureID);
        mFilter.create();
        mFilter.setTextureId(textureID);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        setViewSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (mSurfaceTexture != null)
        {
            mSurfaceTexture.updateTexImage();
        }
        mFilter.draw();
    }

    public SurfaceTexture getSurfaceTexture()
    {
        return mSurfaceTexture;
    }
}
