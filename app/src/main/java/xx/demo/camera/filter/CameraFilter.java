package xx.demo.camera.filter;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import javax.microedition.khronos.opengles.GL10;

/**
 * surface texture 渲染镜头数据
 * Created by admin on 2018/3/5.
 */

public class CameraFilter extends AFilter
{
    public CameraFilter(Resources resources)
    {
        super(resources);
    }

    @Override
    protected void onCreate()
    {
        CreateProgramByAssetsFile("shader/camera_vertex.txt", "shader/camera_fragment.txt");
    }

    @Override
    protected void bindHandleInProgram(int program)
    {
        mHMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        mHVertexPos = GLES20.glGetAttribLocation(program, "vPosition");
        mHTexturePos = GLES20.glGetAttribLocation(program, "vCoord");
        mHTexture = GLES20.glGetUniformLocation(program, "vTexture");
    }

    @Override
    protected void onSizeChanged(int width, int height)
    {

    }

    @Override
    protected void onBindTexture()
    {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + getTextureType());
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, getTextureId());
        GLES20.glUniform1i(mHTexture, getTextureType());
    }

    @Override
    public int createTextureID()
    {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }
}
