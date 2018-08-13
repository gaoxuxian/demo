package gles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import lib.opengles.ByteBufferUtil;
import lib.opengles.GL20ShaderUtil;
import lib.opengles.GLUtil;
import lib.opengles.R;

public class Gles7View extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private float[] picture_vertex_arr;
    private short[] picture_vertex_index_arr;
    private float[] picture_texture_index_arr;

    private FloatBuffer mPictureVertexBuffer;
    private ShortBuffer mPictureVertexIndexBuffer;
    private FloatBuffer mPictureTextureIndexBuffer;

    private int mProgram;
    private Bitmap mTextureBmp;

    public Gles7View(Context context)
    {
        super(context);

        picture_vertex_arr = new float[]{
                -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f
        };

        picture_vertex_index_arr = new short[]{
                0, 1, 2,
                0, 2, 3
        };

        picture_texture_index_arr = new float[]{
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };

        mTextureBmp = BitmapFactory.decodeResource(getResources(), R.drawable.opengl_test_4);

        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        mPictureVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(picture_vertex_arr);
        if (mPictureVertexBuffer != null)
        {
            mPictureVertexBuffer.position(0);
        }

        mPictureVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(picture_vertex_index_arr);
        if (mPictureVertexIndexBuffer != null)
        {
            mPictureVertexIndexBuffer.position(0);
        }

        mPictureTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(picture_texture_index_arr);
        if (mPictureTextureIndexBuffer != null)
        {
            mPictureTextureIndexBuffer.position(0);
        }

        int vertex_shader = GL20ShaderUtil.getShader(getContext(), GLES20.GL_VERTEX_SHADER, "gles/shader/texture2d_picture_vertex_shader");
        int fragment_shader = GL20ShaderUtil.getShader(getContext(), GLES20.GL_FRAGMENT_SHADER, "gles/shader/texture2d_picture_fragment_shader");

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertex_shader);
        GLES20.glAttachShader(mProgram, fragment_shader);
        GLES20.glLinkProgram(mProgram);
        GLES20.glUseProgram(mProgram);

        int vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, mPictureVertexBuffer);

        int vCoordinate = GLES20.glGetAttribLocation(mProgram, "vCoordinate");
        GLES20.glEnableVertexAttribArray(vCoordinate);
        GLES20.glVertexAttribPointer(vCoordinate, 2, GLES20.GL_FLOAT, false, 0, mPictureTextureIndexBuffer);

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
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mTextureBmp, 0);

        int vTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
        GLES20.glUniform1i(vTexture, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
        // 以下三种方式都可以达到图片不被拉伸、压缩的效果

        // 第一种方式: 直接在 透视矩阵 中 设置近平面的宽高, 达到压缩效果
        if (mTextureBmp != null)
        {
            float[] matrix = new float[16];
            GLUtil.getFrustumM(matrix, mTextureBmp.getWidth(), mTextureBmp.getHeight(), width, height);
            int vMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
            GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrix, 0);
        }

        // 以下两种方式，需要将 顶点坐标 范围，定义为[-1, 1], 因为计算的时候，在三维世界中，是基于某一个点，到原点的距离是 1

        // 第二种方式: 直接写死 近平面 的宽高, 后续通过 矩阵, 对图片做二次压缩
//        if (mTextureBmp != null)
//        {
//            float sWH = (float) width / height;
//
//            float[] matrix = GLUtil.getOpenGLUnitMatrix();
//
//            float[] frustumM = new float[16];
//
//            float[] viewPortM = new float[16];
//
//            float[] temp = new float[16];
//
//            Matrix.frustumM(frustumM, 0, -1f, 1f, -1f / sWH, 1f / sWH, 3, 5);
//
//            Matrix.setLookAtM(viewPortM, 0, 0, 0, 3.0f, 0, 0, 0, 0, 1, 0);
//
//            Matrix.scaleM(matrix, 0, 1f, mTextureBmp.getHeight() / (float) mTextureBmp.getWidth(), 1f);
//
//            Matrix.multiplyMM(temp, 0, viewPortM, 0, matrix, 0);
//
//            Matrix.multiplyMM(matrix, 0, frustumM, 0, temp, 0);
//
//            int vMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
//            GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrix, 0);
//        }

        // 第三种方式: 直接写死 近平面 的宽高, 后续通过 矩阵, 对图片做二次压缩 (只是对 第二种方式 矩阵融合顺序的调整)
//        if (mTextureBmp != null)
//        {
//            float sWH = (float) width / height;
//
//            float[] matrix = new float[16];
//
//            float[] frustumM = new float[16];
//
//            float[] viewPortM = new float[16];
//
//            float[] scale = GLUtil.getOpenGLUnitMatrix();
//
//            float[] temp = new float[16];
//
//            Matrix.frustumM(frustumM, 0, -1f, 1f, -1f / sWH, 1f / sWH, 3, 5);
//
//            Matrix.setLookAtM(viewPortM, 0, 0, 0, 3.0f, 0, 0, 0, 0, 1, 0);
//
//            Matrix.scaleM(scale, 0, 1f, mTextureBmp.getHeight() / (float) mTextureBmp.getWidth(), 1f);
//
//            Matrix.multiplyMM(temp, 0, frustumM, 0, viewPortM, 0);
//
//            Matrix.multiplyMM(matrix, 0, scale, 0, temp, 0);
//
//            int vMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
//            GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrix, 0);
//        }
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mPictureVertexIndexBuffer);
    }
}
