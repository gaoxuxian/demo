package gpu.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.provider.Telephony;

import java.nio.FloatBuffer;

import lib.opengles.R;
import util.ByteBufferUtil;
import util.GLES20Util;
import util.GLUtil;
import util.VaryTools;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/30.
 */
public class MaskTestFilter extends GPUImageFilter
{
    private int[] mTextureIdArr;
    private int mBmpW;
    private int mBmpH;

    private int mBmpW2;
    private int mBmpH2;

    private int mProgram2;
    protected int vPositionHandle2;
    protected int vCoordinateHandle2;
    protected int vMatrixHandle2;
    protected int vTextureHandle2;

    public MaskTestFilter(Context context)
    {
        super(context);

        mTextureIdArr = new int[2];
    }

    @Override
    protected void onInitProgramHandle()
    {
        super.onInitProgramHandle();

        mProgram2 = GLES20Util.sCreateAndLinkProgram(GLES20Util.sGetShader(GLES20.GL_VERTEX_SHADER, DEFAULT_VERTEX_SHADER), GLES20Util.sGetShader(GLES20.GL_FRAGMENT_SHADER, DEFAULT_FRAGMENT_SHADER));

        vPositionHandle2 = GLES20.glGetAttribLocation(mProgram2, "vPosition");
        vCoordinateHandle2 = GLES20.glGetAttribLocation(mProgram2, "vCoordinate");

        vMatrixHandle2 = GLES20.glGetUniformLocation(mProgram2, "vMatrix");
        vTextureHandle2 = GLES20.glGetUniformLocation(mProgram2, "vTexture");
    }

    @Override
    protected void onDrawArraysPre()
    {
        if (!GLES30.glIsTexture(mTextureIdArr[0]))
        {
            GLES30.glGenTextures(2, mTextureIdArr, 0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIdArr[0]);
            GLES20Util.sBindTextureParams();

            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.opengl_test_2);
            mBmpW = bitmap.getWidth();
            mBmpH = bitmap.getHeight();
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
            GLES30.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIdArr[1]);
            GLES20Util.sBindTextureParams();
            bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.opengl_test_5);
            mBmpW2 = bitmap.getWidth();
            mBmpH2 = bitmap.getHeight();
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
            GLES30.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height)
    {
        super.onSurfaceChanged(width, height);

        VaryTools matrix = getMatrix();
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        matrix.frustum(-1, 1, -(float)height / width, (float) height/width, 3, 7);
    }

    @Override
    public int onDraw(int textureId)
    {
        GLES20.glViewport(0, 0, getSurfaceW(), getSurfaceH());
        GLES20.glUseProgram(getProgram());

        // float[] temp = new float[]{
        //         -1.0f, 0.2f, 0.0f,
        //         1.0f, 0.2f, 0.0f,
        //         1.0f, -0.2f, 0.0f,
        //         -1.0f, -0.2f, 0.0f
        // };
        // FloatBuffer tempb = ByteBufferUtil.getNativeFloatBuffer(temp);

        // 绑定顶点坐标缓冲
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPositionHandle);

        // 绑定纹理坐标缓冲
        mTextureIndexBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureIndexBuffer);
        GLES20.glEnableVertexAttribArray(vCoordinateHandle);


        VaryTools matrix = getMatrix();
        matrix.pushMatrix();
        matrix.scale(1f, mBmpH / (float) mBmpW, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();


        onDrawArraysPre();

        // 第一次绘制

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIdArr[0]);
        GLES30.glUniform1i(vTextureHandle, 0);

        GLES30.glClearStencil(0);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_STENCIL_BUFFER_BIT);
        GLES30.glEnable(GLES30.GL_STENCIL_TEST);
        // GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        GLES30.glStencilMask(0xFF);
        GLES30.glStencilFunc(GLES30.GL_ALWAYS, 1, 0xFF);
        GLES30.glStencilOp(GLES30.GL_KEEP, GLES30.GL_KEEP, GLES30.GL_REPLACE);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

        GLES20.glDisableVertexAttribArray(vPositionHandle);
        GLES20.glDisableVertexAttribArray(vTextureHandle);
        // onDrawArraysAfter();


        GLES20.glViewport(0, 0, getSurfaceW(), getSurfaceH());
        GLES20.glUseProgram(mProgram2);

        // 第二次绘制

        // 绑定顶点坐标缓冲
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPositionHandle2, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPositionHandle2);

        // 绑定纹理坐标缓冲
        mTextureIndexBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoordinateHandle2, 2, GLES20.GL_FLOAT, false, 0, mTextureIndexBuffer);
        GLES20.glEnableVertexAttribArray(vCoordinateHandle2);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIdArr[1]);
        GLES30.glUniform1i(vTextureHandle2, 1);

        matrix.pushMatrix();
        matrix.scale(0.8f, 0.8f, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle2, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();

        // GLES30.glEnable(GLES30.GL_STENCIL_TEST);
        // GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glStencilFunc(GLES30.GL_EQUAL, 1, 0xFF);
        GLES30.glStencilOp(GLES30.GL_KEEP, GLES30.GL_KEEP, GLES30.GL_KEEP);

        mVertexIndexBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

        GLES20.glDisableVertexAttribArray(vPositionHandle2);
        GLES20.glDisableVertexAttribArray(vTextureHandle2);
        onDrawArraysAfter();

        GLES20.glBindTexture(getTextureType(), 0);

        return textureId;
    }

    @Override
    protected void onDrawArraysAfter()
    {
        super.onDrawArraysAfter();
        GLES30.glDisable(GLES30.GL_STENCIL_TEST);
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
    }
}
