package filter.fbo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;

import filter.AFilter;
import lib.opengles.R;
import util.BufferUtil;
import util.GLES20Util;
import util.VaryTools;

public class ImgFBOFilter extends AFilter
{
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureIndexBuffer;
    private ShortBuffer mVertexIndexBuffer;

    private int[] mTextureArr;

    private int vPosition;
    private int vCoordinate;
    private int vMatrix;
    private int vTexture;

    private int[] mFrameBufferArr;

    private Bitmap mTextureBmp;
    private int[] fRender;

    public ImgFBOFilter(Resources res)
    {
        super(res);
    }

    @Override
    protected void onInitBaseData()
    {
        float[] vertex = new float[]{
                -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f
        };

        mVertexBuffer = BufferUtil.getNativeFloatBuffer(vertex);

        float[] texture_index = new float[]{
                0, 0,
                1, 0,
                1, 1,
                0, 1
        };

        mTextureIndexBuffer = BufferUtil.getNativeFloatBuffer(texture_index);

        short[] vertex_index = new short[]{
                0, 1, 2,
                0, 2, 3
        };

        mVertexIndexBuffer = BufferUtil.getNativeShortBuffer(vertex_index);

        mTextureArr = new int[2];
    }

    @Override
    protected void onSurfaceCreateSet(EGLConfig config)
    {
        mTextureBmp = BitmapFactory.decodeResource(getResources(), R.drawable.opengl_test_2);
    }

    @Override
    protected int onCreateProgram()
    {
        int vertex_shader = GLES20Util.sGetShader(getResources(), GLES20.GL_VERTEX_SHADER, "shader/simple2D/picture_vertex_shader.glsl");
        int fragment_shader = GLES20Util.sGetShader(getResources(), GLES20.GL_FRAGMENT_SHADER, "shader/simple2D/picture_fragment_shader.glsl");

        int program = GLES20Util.sCreateAndLinkProgram(vertex_shader, fragment_shader);

        vPosition = GLES20.glGetAttribLocation(program, "vPosition");
        vCoordinate = GLES20.glGetAttribLocation(program, "vCoordinate");
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        vTexture = GLES20.glGetUniformLocation(program, "vTexture");

        return program;
    }

    @Override
    protected void onSurfaceChangeSet(int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
        VaryTools tools = getMatrixTools();
        float sWidthHeight = (float) width / height;
        tools.frustum(-1, 1, -1 / sWidthHeight, 1/sWidthHeight, 3, 5);
        tools.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
    }

    @Override
    protected void onBe4DrawSet()
    {

    }

    @Override
    protected void onDrawSelf()
    {
        if (isTextureBmpAvailable())
        {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

            createFrameBuffer();

            // 绑定我们构建的 FBO
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferArr[0]);
            // 给我们构建的 FBO 挂载一个 texture，用来记录接下来绘制的颜色数据
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mTextureArr[1], 0);
            // 为了提高绘制速度，将视图压缩一半
            GLES20.glViewport(0, 0, getSurfaceWidth()/2, getSurfaceHeight()/2);

            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glUseProgram(getGLProgram());

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureArr[0]);
            GLES20.glUniform1i(vTexture, 0);

            VaryTools tools = getMatrixTools();
            tools.pushMatrix();
            float y = mTextureBmp.getHeight() / (float) mTextureBmp.getWidth();
            tools.scale(1f, -y, 1f);
            GLES20.glUniformMatrix4fv(vMatrix, 1, false, tools.getFinalMatrix(), 0);

            GLES20.glEnableVertexAttribArray(vPosition);
            GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

            GLES20.glEnableVertexAttribArray(vCoordinate);
            GLES20.glVertexAttribPointer(vCoordinate, 2, GLES20.GL_FLOAT, false, 0, mTextureIndexBuffer);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

            tools.popMatrix();
            GLES20.glDisableVertexAttribArray(vPosition);
            GLES20.glDisableVertexAttribArray(vCoordinate);

            // 将 FBO 切换成默认
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
            // 恢复视图大小
            GLES20.glViewport(0, 0, getSurfaceWidth(), getSurfaceHeight());
            GLES20.glUniformMatrix4fv(vMatrix, 1, false, getMatrixTools().getOpenGLUnitMatrix(), 0);

            GLES20.glEnableVertexAttribArray(vPosition);
            GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

            GLES20.glEnableVertexAttribArray(vCoordinate);
            GLES20.glVertexAttribPointer(vCoordinate, 2, GLES20.GL_FLOAT, false, 0, mTextureIndexBuffer);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureArr[1]);
            GLES20.glUniform1i(vTexture, 1);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

            GLES20.glDisableVertexAttribArray(vPosition);
            GLES20.glDisableVertexAttribArray(vCoordinate);

            deleteEnvi();
        }
    }

    private boolean isTextureBmpAvailable()
    {
        return mTextureBmp != null && !mTextureBmp.isRecycled();
    }

    private void createFrameBuffer()
    {
        if (mFrameBufferArr == null)
        {
            mFrameBufferArr = new int[1];
        }
        if (fRender == null)
        {
            fRender = new int[1];
        }

        GLES20.glGenFramebuffers(1, mFrameBufferArr, 0);

        GLES20.glGenTextures(2, mTextureArr, 0);
        for (int i = 0;i<mTextureArr.length;i++)
        {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureArr[i]);
            if (i == 0)
            {
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mTextureBmp, 0);
            }
            else
            {
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, getSurfaceWidth()/2, getSurfaceHeight()/2, 0,
                        GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            }

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }
    }

    private void deleteEnvi() {
        GLES20.glDeleteTextures(2, mTextureArr, 0);
        GLES20.glDeleteFramebuffers(1, mFrameBufferArr, 0);
    }
}
