package filter.camera;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;

import filter.AFilter;
import util.ByteBufferUtil;
import util.GLES20Util;
import util.VaryTools;

public class CameraPreviewFilter extends AFilter
{
    private int mPreviewW; // --> 对应 Camera.Size.height
    private int mPreviewH; // --> 对应 Camera.Size.width

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureIndexBuffer;
    private ShortBuffer mVertexIndexBuffer;

    private int vPosition;
    private int vMatrix;
    private int vCoord;
    private int vTexture;

    private SurfaceTexture mSurfaceTexture;
    private int[] mFrameBufferArr;
    private int[] mTextureArr;
    private int[] surfaceTextureID;

    public CameraPreviewFilter(Resources res)
    {
        super(res);
    }

    public void setPreviewSize(int width, int height)
    {
        mPreviewH = height;
        mPreviewW = width;
    }

    public SurfaceTexture getSurfaceTexture()
    {
        return mSurfaceTexture;
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
        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(vertex);

        short[] vertex_index = new short[]{
                0, 1, 2,
                0, 2, 3
        };
        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(vertex_index);

        float[] texture_index = new float[]{
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };
        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(texture_index);

        if (mFrameBufferArr == null)
        {
            mFrameBufferArr = new int[1];
        }

        if (mTextureArr == null)
        {
            mTextureArr = new int[1];
        }
    }

    @Override
    protected void onSurfaceCreateSet(EGLConfig config)
    {

    }

    @Override
    protected int onCreateProgram()
    {
        int vertex_shader = GLES20Util.sGetShader(getResources(), GLES20.GL_VERTEX_SHADER, "shader/camera/vertex_shader.glsl");
        int fragment_shader = GLES20Util.sGetShader(getResources(), GLES20.GL_FRAGMENT_SHADER, "shader/camera/fragment_shader.glsl");

        int program = GLES20Util.sCreateAndLinkProgram(vertex_shader, fragment_shader);

        vPosition = GLES20.glGetAttribLocation(program, "vPosition");
        vCoord = GLES20.glGetAttribLocation(program, "vCoord");
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        vTexture = GLES20.glGetUniformLocation(program, "vTexture");

        surfaceTextureID = new int[1];
        GLES20.glGenTextures(surfaceTextureID.length, surfaceTextureID, 0);
        mSurfaceTexture = new SurfaceTexture(surfaceTextureID[0]);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, surfaceTextureID[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        return program;
    }

    @Override
    protected void onSurfaceChangeSet(int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
        float sWidthHeight = (float) width / height;
        VaryTools tools = getMatrixTools();
        tools.frustum(-1, 1, -1 / sWidthHeight, 1 / sWidthHeight, 3, 5);
        tools.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
    }

    @Override
    protected void onBe4DrawSet()
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    protected void onDrawSelf()
    {
        // 更新最新的纹理
        if (mSurfaceTexture != null)
        {
            mSurfaceTexture.updateTexImage();

            GLES20.glUseProgram(getGLProgram());

            boolean b = GLES20.glIsFramebuffer(mFrameBufferArr[0]);
            if (!b)
            {
                GLES20.glGenFramebuffers(1, mFrameBufferArr, 0);

                GLES20.glGenTextures(1, mTextureArr, 0);
                for (int i = 0;i<mTextureArr.length;i++)
                {
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureArr[i]);
                    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, getSurfaceWidth()/2, getSurfaceHeight()/2, 0,
                            GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

                    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                }
            }

            // 绑定我们构建的 FBO
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferArr[0]);
            // 给我们构建的 FBO 挂载一个 texture，用来记录接下来绘制的颜色数据
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mTextureArr[0], 0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, surfaceTextureID[0]);
            GLES20.glUniform1i(vTexture, 0);

            VaryTools tools = getMatrixTools();

//            for (int i = 0; i<4;i++)
//            {
                tools.pushMatrix();

                float scale = (float) mPreviewH / mPreviewW;

//                if (i == 0)
//                {
//                    GLES20.glViewport(0, 0, getSurfaceWidth()/2, getSurfaceHeight()/2);
//                }
//                else if (i == 1)
//                {
//                    GLES20.glViewport(getSurfaceWidth()/2, 0, getSurfaceWidth()/2, getSurfaceHeight()/2);
//                }
//                else if (i == 2)
//                {
//                    GLES20.glViewport(0, getSurfaceHeight()/2, getSurfaceWidth()/2, getSurfaceHeight()/2);
//                }
//                else
//                {
//                    GLES20.glViewport(getSurfaceWidth()/2, getSurfaceHeight()/2, getSurfaceWidth()/2, getSurfaceHeight()/2);
//                }

                if (scale == 2)
                {
                    float scaleY = 2160f /1920f * 16f/9;
                    float scaleX = 2160f/1920f;
                    tools.scale(-scaleX, scaleY, 1f);
                }
                else
                {
                    tools.scale(-1f, scale, 1f);

                    // float dy = (getSurfaceHeight() - ((float) getSurfaceWidth() * scale)) / 2f;
                    // float p = dy * 2/ getSurfaceHeight();
                    // float y = p * (float) getSurfaceHeight() / getSurfaceWidth() / scale;
                    //
                    // tools.translate(0, y, 0);
                }

                tools.rotate(90, 0, 0, 1);

                GLES20.glUniformMatrix4fv(vMatrix, 1, false, tools.getFinalMatrix(), 0);

                tools.popMatrix();

                GLES20.glEnableVertexAttribArray(vPosition);
                GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

                GLES20.glEnableVertexAttribArray(vCoord);
                GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mTextureIndexBuffer);

                GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

                GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureArr[0]);
                GLES20.glUniform1i(vTexture, 1);

                GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

                GLES20.glDisableVertexAttribArray(vPosition);
                GLES20.glDisableVertexAttribArray(vCoord);
            }
//        }
    }
}
