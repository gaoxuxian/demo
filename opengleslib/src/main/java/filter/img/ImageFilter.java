package filter.img;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;

import filter.AFilter;
import gpu.GLConstant;
import util.ByteBufferUtil;
import util.GLES20Util;
import util.VaryTools;

/**
 * 只画一个 Simple2D 的底图
 */
public class ImageFilter extends AFilter
{
    private Bitmap mTextureBmp;

    private FloatBuffer mVertexBuffer;
    private ShortBuffer mVertexIndexBuffer;

    private FloatBuffer mTextureIndexBuffer;

    // 句柄
    private int vPosition;
    private int vCoordinate;
    private int vMatrix;

    private boolean mRefreshBmp;
    private int vTexture;

    public ImageFilter(Resources res)
    {
        super(res);
    }

    @Override
    protected void onInitBaseData()
    {
        // float[] vertex = new float[]{
        //         -1.0f, 1.0f, 0.0f,
        //         1.0f, 1.0f, 0.0f,
        //         1.0f, -1.0f, 0.0f,
        //         -1.0f, -1.0f, 0.0f
        // };

        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.VERTEX_CUBE);

        short[] vertex_index = new short[]{
                0, 1, 2,
                0, 2, 3
        };

        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(vertex_index);

        // float[] texture_index = new float[]{
        //         0.0f, 0.0f,
        //         1.0f, 0.0f,
        //         1.0f, 1.0f,
        //         0.0f, 1.0f
        // };

        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.TEXTURE_INDEX);
    }

    @Override
    protected void onSurfaceCreateSet(EGLConfig config)
    {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
    }

    @Override
    protected int onCreateProgram()
    {
        int vertex_shader = GLES20Util.sGetShader(getResources(), GLES20.GL_VERTEX_SHADER, "shader/simple2D/picture_vertex_shader.glsl");
        int fragment_shader = GLES20Util.sGetShader(getResources(), GLES20.GL_FRAGMENT_SHADER, "shader/simple2D/picture_fragment_shader.glsl");

        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertex_shader);
        GLES20.glAttachShader(program, fragment_shader);
        GLES20.glLinkProgram(program);

        // 获取句柄
        vPosition = GLES20.glGetAttribLocation(program, "vPosition");
        vCoordinate = GLES20.glGetAttribLocation(program, "vCoordinate");
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        vTexture = GLES20.glGetUniformLocation(program, "vTexture");

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
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

        return program;
    }

    @Override
    public void onSurfaceChangeSet(int width, int height)
    {
        mRefreshBmp = false;
        GLES20.glViewport(0, 0, width, height);
        VaryTools tools = getMatrixTools();
        float sViewWH = (float) width / height;
        if (isTextureBmpAvailable())
        {
            float sImgWH = (float) mTextureBmp.getWidth() / mTextureBmp.getHeight();

            float scale = Math.min((float) width / mTextureBmp.getWidth(), (float) height / mTextureBmp.getHeight());
            float scaleX = (float) width / mTextureBmp.getWidth();
            float scaleY = (float) height / mTextureBmp.getHeight();

            if (scale == scaleX)
            {
                tools.frustum(-1, 1, -sImgWH / sViewWH, sImgWH / sViewWH, 3, 9);
            }
            else if (scale == scaleY)
            {
                tools.frustum(-sViewWH / sImgWH, sViewWH / sImgWH, -1, 1, 3, 9);
            }
        }
        else if (width <= height)
        {
            tools.frustum(-1, 1, -1 / sViewWH, 1 / sViewWH, 3, 9);
        }
        else if (height > width)
        {
            tools.frustum(-sViewWH, sViewWH, -1, 1, 3, 9);
        }
        tools.setCamera(0, 0, 6, 0, 0, 0, 0, 1, 0);
        setMatrix(tools.getFinalMatrix());
    }

    @Override
    protected void onBe4DrawSet()
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if (mRefreshBmp && getSurfaceWidth() != 0 && getSurfaceHeight() != 0)
        {
            uploadBmpToGPU();
            onSurfaceChangeSet(getSurfaceWidth(), getSurfaceHeight());
        }

    }

    @Override
    public void onDrawSelf()
    {
        if (isTextureBmpAvailable())
        {
            GLES20.glUseProgram(getGLProgram());

            GLES20.glUniform1i(vTexture, 0);

            VaryTools tools = getMatrixTools();
            tools.pushMatrix();
            // tools.rotate(8, 0, 0, 1);

            GLES20.glUniformMatrix4fv(vMatrix, 1, false, tools.getFinalMatrix(), 0);
            tools.popMatrix();

            GLES20.glEnableVertexAttribArray(vPosition);
            GLES20.glEnableVertexAttribArray(vCoordinate);

            GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
            GLES20.glVertexAttribPointer(vCoordinate, 2, GLES20.GL_FLOAT, false, 0, mTextureIndexBuffer);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

            GLES20.glDisableVertexAttribArray(vPosition);
            GLES20.glDisableVertexAttribArray(vCoordinate);
        }
    }

    @Override
    public void onClear()
    {
        super.onClear();

        mTextureBmp = null;
    }

    public void setTextureBitmap(Bitmap bitmap)
    {
        mTextureBmp = bitmap;
    }

    public void setRefresh()
    {
        mRefreshBmp = true;
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
}
