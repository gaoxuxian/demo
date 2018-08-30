package filter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;

import util.ByteBufferUtil;
import util.GLES20Util;
import util.VaryTools;

public class EGLFilter extends AFilter
{
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureIndexBuffer;
    private ShortBuffer mVertexIndexBuffer;

    private volatile Bitmap mTextureBmp;

    private int vPosition;
    private int vCoordinate;
    private int vMatrix;
    private int vTexture;

    private int[] mFrameBuffers;
    private int[] mTextures;

    public EGLFilter(Resources res)
    {
        super(res);
    }

    @Override
    public void onInitBaseData()
    {
        float[] vertex = new float[]{
                -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f
        };

        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(vertex);

        float[] texture = new float[]{
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };

        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(texture);

        short[] vertex_index = new short[]{
                0, 1, 2,
                0, 2, 3
        };

        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(vertex_index);

        mFrameBuffers = new int[1];

        mTextures = new int[1];
    }

    @Override
    protected void onSurfaceCreateSet(EGLConfig config)
    {

    }

    @Override
    public int onCreateProgram()
    {
        int vertex_shader = GLES20Util.sGetShader(getResources(), GLES20.GL_VERTEX_SHADER, "shader/egl/egl_back_vertex_shader.glsl");
        int fragment_shader = GLES20Util.sGetShader(getResources(), GLES20.GL_FRAGMENT_SHADER, "shader/egl/egl_back_fragment_shader.glsl");
        int program = GLES20Util.sCreateAndLinkProgram(vertex_shader, fragment_shader);

        vPosition = GLES20.glGetAttribLocation(program, "vPosition");
        vCoordinate = GLES20.glGetAttribLocation(program, "vCoordinate");
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        vTexture = GLES20.glGetUniformLocation(program, "vTexture");

        return program;
    }

    @Override
    public void onSurfaceChangeSet(int width, int height)
    {
//        GLES20.glViewport(0, 0, width, height);
//        VaryTools tools = getMatrixTools();
//        float sWidthHeight = (float) width / height;
//        tools.frustum(-1, 1, -1 / sWidthHeight, 1/sWidthHeight, 3, 5);
//        tools.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
    }

    @Override
    public void onBe4DrawSet()
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void onDrawSelf()
    {
        if (isTextureBmpAvailable())
        {
            GLES20.glGenTextures(mTextures.length, mTextures, 0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mTextureBmp, 0);

            GLES20.glUseProgram(getGLProgram());

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
            GLES20.glUniform1i(vTexture, 0);

            VaryTools tools = getMatrixTools();

            tools.pushMatrix();

            float scale = mTextureBmp.getHeight() / (float) mTextureBmp.getWidth();

            tools.scale(1f, scale, 1f);

            GLES20.glUniformMatrix4fv(vMatrix, 1, false, tools.getOpenGLUnitMatrix(), 0);

            tools.popMatrix();

            GLES20.glEnableVertexAttribArray(vPosition);
            GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

            GLES20.glEnableVertexAttribArray(vCoordinate);
            GLES20.glVertexAttribPointer(vCoordinate, 2, GLES20.GL_FLOAT, false, 0, mTextureIndexBuffer);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

            GLES20.glDisableVertexAttribArray(vPosition);
            GLES20.glDisableVertexAttribArray(vCoordinate);

//            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//            GLES20.glDeleteTextures(mTextures.length, mTextures, 0);
//            GLES20.glDeleteFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);
//            GLES20.glUseProgram(0);
        }
    }

    private boolean isTextureBmpAvailable()
    {
        return mTextureBmp != null && !mTextureBmp.isRecycled();
    }

    public void setTextureBmp(Bitmap bitmap)
    {
        mTextureBmp = bitmap;
    }
}
