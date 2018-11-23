package gpu.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import javax.microedition.khronos.egl.EGLConfig;

import lib.opengles.R;
import util.GLES20Util;
import util.VaryTools;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/20.
 */
public class ImageFilter extends GPUImageFilter
{
    private int[] mTextureArr;
    private MSAAFilter mMSAAFilter;

    public ImageFilter(Context context)
    {
        super(context);
        mTextureArr = new int[2];
        mMSAAFilter = new MSAAFilter(context);
    }

    @Override
    public void onSurfaceCreated(EGLConfig config)
    {
        super.onSurfaceCreated(config);

        mMSAAFilter.onSurfaceCreated(config);
    }

    @Override
    public void onSurfaceChanged(int width, int height)
    {
        super.onSurfaceChanged(width, height);

        mMSAAFilter.onSurfaceChanged(width, height);
    }

    @Override
    protected void onDrawArraysPre()
    {
        if (!GLES20.glIsTexture(mTextureArr[0]))
        {
            GLES20.glGenTextures(mTextureArr.length, mTextureArr, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureArr[0]);
            GLES20Util.sBindTextureParams();
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.open_test_2);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap, 0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureArr[1]);
            GLES20Util.sBindTextureParams();
            bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.open_test_5);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap, 0);
        }
    }

    @Override
    public int onDraw(int textureId)
    {
        onDrawArraysPre();

        VaryTools matrix = getMatrix();
        float scale = (float) getSurfaceH() / getSurfaceW();
        matrix.frustum(-1, 1, -scale, scale, 3, 5);
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        matrix.pushMatrix();
        matrix.rotate(8, 0, 0, 1);
        matrix.scale(0.8f, 0.8f, 1f);
        mMSAAFilter.setMatrix(matrix.getFinalMatrix());
        matrix.popMatrix();

        mMSAAFilter.setPreDrawToClear(true);
        mMSAAFilter.setNeedToCopyRenderBuffer(false);
        mMSAAFilter.onDraw(mTextureArr[0]);

        matrix.pushMatrix();
        matrix.rotate(-8, 0, 0, 1);
        matrix.scale(0.8f, 0.8f, 1f);
        mMSAAFilter.setMatrix(matrix.getFinalMatrix());
        matrix.popMatrix();

        mMSAAFilter.setPreDrawToClear(false);
        mMSAAFilter.setNeedToCopyRenderBuffer(true);
        textureId = mMSAAFilter.onDraw(mTextureArr[1]);

        onDrawArraysAfter();

        return textureId;
    }

    @Override
    public void onClear()
    {
        if (mTextureArr != null)
        {
            GLES20.glDeleteTextures(mTextureArr.length, mTextureArr, 0);
        }
        if (mMSAAFilter != null)
        {
            mMSAAFilter.onClear();
        }
        super.onClear();
    }
}
