package filter.img;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;

import filter.AFilter;
import util.BufferUtil;
import util.GLES20Util;
import util.VaryTools;

/**
 * 不用 FBO 尝试画水印
 */
public class WaterMarkFilter extends AFilter
{
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureIndexBuffer;
    private ShortBuffer mVertexIndexBuffer;

    private Bitmap mTextureBmp;
    private Bitmap mTextureWatermarkBmp;

    private boolean mRefresh;
    private boolean mRefreshWater;

    private int vPosition;
    private int vCoordinate;
    private int vMatrix;
    private int vTexture;

    private int mWaterMarkProgram;

    private FloatBuffer mWaterVertexBuffer;
    private FloatBuffer mWaterTextureIndexBuffer;
    private ShortBuffer mWaterVertexIndexBuffer;

    private int vPositionWater;
    private int vCoordinateWater;
    private int vMatrixWater;
    private int vTextureWater;
    private int[] textures;

    public WaterMarkFilter(Resources res)
    {
        super(res);
    }

    public void setTextureBitmap(Bitmap bitmap)
    {
        mTextureBmp = bitmap;
    }

    public void setTextureWatermarkBitmap(Bitmap bitmap)
    {
        mTextureWatermarkBmp = bitmap;
    }

    public void setRefreshBmp()
    {
        mRefresh = true;
    }

    public void setRefreshWaterBmp()
    {
        mRefreshWater = true;
    }

    @Override
    protected void onInitBaseData()
    {
        textures = new int[2];

        float[] vertex = new float[]{
                -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f
        };

        mVertexBuffer = BufferUtil.getNativeFloatBuffer(vertex);

        float[] texture_index = new float[]{
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };

        mTextureIndexBuffer = BufferUtil.getNativeFloatBuffer(texture_index);

        short[] vertex_index = new short[]{
                0, 1, 2,
                0, 2, 3
        };

        mVertexIndexBuffer = BufferUtil.getNativeShortBuffer(vertex_index);

        float[] vertex_water = new float[]{
                -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f
        };

        mWaterVertexBuffer = BufferUtil.getNativeFloatBuffer(vertex_water);

        float[] texture_index_water = new float[]{
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };

        mWaterTextureIndexBuffer = BufferUtil.getNativeFloatBuffer(texture_index_water);

        short[] vertex_index_water = new short[]{
                0, 1, 2,
                0, 2, 3
        };

        mWaterVertexIndexBuffer = BufferUtil.getNativeShortBuffer(vertex_index_water);
    }

    @Override
    protected void onSurfaceCreateSet(EGLConfig config)
    {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1f);
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

        vertex_shader = GLES20Util.sGetShader(getResources(), GLES20.GL_VERTEX_SHADER, "shader/simple2D/picture_vertex_shader.glsl");
        fragment_shader = GLES20Util.sGetShader(getResources(), GLES20.GL_FRAGMENT_SHADER, "shader/simple2D/picture_fragment_shader.glsl");

        // 水印
        mWaterMarkProgram = GLES20Util.sCreateAndLinkProgram(vertex_shader, fragment_shader);

        vPositionWater = GLES20.glGetAttribLocation(mWaterMarkProgram, "vPosition");
        vCoordinateWater = GLES20.glGetAttribLocation(mWaterMarkProgram, "vCoordinate");
        vMatrixWater = GLES20.glGetUniformLocation(mWaterMarkProgram, "vMatrix");
        vTextureWater = GLES20.glGetUniformLocation(mWaterMarkProgram, "vTexture");

        textures = new int[2];
        GLES20.glGenTextures(2, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        uploadBmpToGPU();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[1]);
        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        uploadWaterBmpToGPU();

        return program;
    }

    @Override
    protected void onSurfaceChangeSet(int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        float sWidthHeight = (float) width / height;

        VaryTools tools = getMatrixTools();
        tools.frustum(-1, 1, -1/sWidthHeight, 1/sWidthHeight, 3, 5);
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
        if (isTextureBmpAvailable())
        {
            GLES20.glDepthMask(false);
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
            GLES20.glBlendFuncSeparate(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

            GLES20.glUseProgram(getGLProgram());

            // 绘制底图前，重新激活纹理单元、绑定底图纹理id
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

            if (mRefresh)
            {
                mRefresh = false;
                uploadBmpToGPU();
            }
            GLES20.glUniform1i(vTexture, 0);

            VaryTools tools = getMatrixTools();
            tools.pushMatrix();

            float scaleX = (float) getSurfaceWidth()/ mTextureBmp.getWidth();
            float scaleY = (float) getSurfaceHeight() / mTextureBmp.getHeight();
            float scale = Math.min(scaleX, scaleY);

            if (scale == scaleX)
            {
                scale = (float) mTextureBmp.getHeight() / mTextureBmp.getWidth();
                tools.scale(1f, scale, 1f);
            }
            else
            {
                scale = (float) mTextureBmp.getWidth() / mTextureBmp.getHeight();
                tools.scale(scale, 1f, 1f);
            }

            GLES20.glUniformMatrix4fv(vMatrix, 1, false, tools.getFinalMatrix(), 0);

            GLES20.glEnableVertexAttribArray(vPosition);
            GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

            GLES20.glEnableVertexAttribArray(vCoordinate);
            GLES20.glVertexAttribPointer(vCoordinate, 2, GLES20.GL_FLOAT, false, 0, mTextureIndexBuffer);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

            GLES20.glDisableVertexAttribArray(vPosition);
            GLES20.glDisableVertexAttribArray(vCoordinate);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            tools.popMatrix();

            if (isTextureWaterBmpAvailable())
            {
                GLES20.glUseProgram(mWaterMarkProgram);

                // 绘制水印前，重新激活纹理单元、绑定水印纹理id
                GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[1]);

                if (mRefreshWater)
                {
                    mRefreshWater = false;
                    uploadWaterBmpToGPU();
                }
                GLES20.glUniform1i(vTextureWater, 1);
                tools = getMatrixTools();

                tools.pushMatrix();

                boolean bmpIsScaleX = scaleX == scale;

                scaleX = (float) 235 / getSurfaceWidth();
                scaleY = (float) 128 / getSurfaceHeight() * (float) mTextureWatermarkBmp.getWidth() / mTextureWatermarkBmp.getHeight();

                tools.scale(scaleX, scaleY, 1f);

                if (bmpIsScaleX)
                {
                    float s = (float) mTextureBmp.getHeight() / mTextureBmp.getWidth();

                    float p = ((float) getSurfaceWidth() * s / 2 - 128 / 2f) * 2 / (float) getSurfaceHeight();

                    float y = p * getSurfaceHeight() / (float) getSurfaceWidth();

                    p = ((float) getSurfaceWidth() / 2 - 235 / 2f) * 2 / (float) getSurfaceWidth();
                    float x = p;

                    tools.translate(-x / scaleX, -y / scaleY, 0);
                }

                GLES20.glUniformMatrix4fv(vMatrixWater, 1, false, tools.getFinalMatrix(), 0);

                GLES20.glEnableVertexAttribArray(vPositionWater);
                GLES20.glVertexAttribPointer(vPositionWater, 3, GLES20.GL_FLOAT, false, 0, mWaterVertexBuffer);

                GLES20.glEnableVertexAttribArray(vCoordinateWater);
                GLES20.glVertexAttribPointer(vCoordinateWater, 2, GLES20.GL_FLOAT, false, 0, mWaterTextureIndexBuffer);

                GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mWaterVertexIndexBuffer);

                GLES20.glDisableVertexAttribArray(vPositionWater);
                GLES20.glDisableVertexAttribArray(vCoordinateWater);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                tools.popMatrix();
            }
        }
    }

    private boolean isTextureBmpAvailable()
    {
        return mTextureBmp != null && !mTextureBmp.isRecycled();
    }

    private void uploadBmpToGPU()
    {
        if (isTextureBmpAvailable())
        {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mTextureBmp, 0);
        }
    }

    private boolean isTextureWaterBmpAvailable()
    {
        return mTextureWatermarkBmp != null && !mTextureWatermarkBmp.isRecycled();
    }

    private void uploadWaterBmpToGPU()
    {
        if (isTextureWaterBmpAvailable())
        {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mTextureWatermarkBmp, 0);
        }
    }
}
