package gpu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import lib.opengles.R;
import util.GLES20Util;
import util.VaryTools;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/20.
 */
public class FBOFilter extends GPUImageFilter
{
    private int[] mTextureArr;

    public FBOFilter(Context context)
    {
        super(context);
        mTextureArr = new int[1];
    }

    @Override
    protected void onDrawArraysPre()
    {
        if (!GLES20.glIsTexture(mTextureArr[0]))
        {
            GLES20.glGenTextures(mTextureArr.length, mTextureArr, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureArr[0]);
            GLES20Util.sBindTextureParams();
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.open_test_5);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap, 0);
            GLES20.glUniform1i(vTextureHandle, 0);
        }
        else
        {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureArr[0]);
            GLES20.glUniform1i(vTextureHandle, 0);
        }

        GLES20.glViewport(0, 0, getSurfaceW(), getSurfaceH());
        VaryTools matrix = getMatrix();
        float scale = (float) getSurfaceH() / getSurfaceW();
        matrix.frustum(-1, 1, -scale, scale, 3, 9);
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        matrix.pushMatrix();
        matrix.rotate(8, 0, 0, 1);
        matrix.scale(0.2f, 0.2f, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    public void onDraw(int textureId)
    {
        if (!GLES20.glIsProgram(getProgram()))
        {
            return;
        }

        GLES20.glUseProgram(getProgram());

        GLES20.glClearColor(1f, 1f, 1f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        mTaskWrapper.runTask();

        // 绑定顶点坐标缓冲
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPositionHandle);

        // 绑定纹理坐标缓冲
        mTextureIndexBuffer.position(0);
        GLES20.glVertexAttribPointer(vTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureIndexBuffer);
        GLES20.glEnableVertexAttribArray(vTextureHandle);

        onDrawArraysPre();
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

        GLES20.glDisableVertexAttribArray(vPositionHandle);
        GLES20.glDisableVertexAttribArray(vTextureHandle);
        onDrawArraysAfter();
    }

    @Override
    protected void onDrawArraysAfter()
    {

    }
}
