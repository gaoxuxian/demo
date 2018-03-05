package xx.demo.activity.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import xx.demo.R;
import xx.demo.activity.BaseActivity;
import xx.demo.util.ShareData;

/**
 * 用 gl surface view 简单绘制矩形、一张图片纹理
 * <p>
 * 基础知识请查看 {@link GLES20Activity}
 * <p>
 * http://www.cnblogs.com/ghj1976/archive/2012/04/27/2473624.html
 * <p>
 * 小码哥_WS 大神(很多 ffmpeg、OpenGL 资料)
 * <p>
 * http://blog.csdn.net/king1425
 * <p>
 * 松阳 大神 (很多 c代码写的 着色器)
 * <p>
 * http://blog.csdn.net/fansongy/article/list?viewmode=contents
 */

public class GLES20ActivityV2 extends BaseActivity
{
    private GLSurfaceView mGlSurfaceView;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mGlSurfaceView = new GLSurfaceView(parent.getContext());
        mGlSurfaceView.setEGLContextClientVersion(2);
        mGlSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGlSurfaceView.setRenderer(new MyImageRender(this)); // 画图片
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        params = new FrameLayout.LayoutParams(ShareData.m_screenRealWidth, (int) (ShareData.m_screenRealWidth * 0.5625f));
        parent.addView(mGlSurfaceView, params);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mGlSurfaceView.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mGlSurfaceView.onResume();
    }

    private static class MyImageRender implements GLSurfaceView.Renderer
    {
        private final Bitmap mBitmap;
        private int mTextureId;

        private static final String VERTEX_SHADER =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 vPosition;" +
                        "attribute vec2 a_texCoord;" +
                        "varying vec2 v_texCoord;" +
                        "void main() {" +
                        " gl_Position = uMVPMatrix * vPosition;" +
                        " v_texCoord = a_texCoord;" +
                        "}";
        private static final String FRAGMENT_SHADER =
                "precision mediump float;" +
                        "varying vec2 v_texCoord;" +
                        "uniform sampler2D s_texture;" +
                        "void main() {" +
                        " gl_FragColor = texture2D(s_texture, v_texCoord);" +
                        "}";

        // 顶点坐标 规定了一个 可视区域的范围，不一定非得是正方形
        private static final float[] VERTEX = {
                -1.0f, 1.0f, 0f,
                -1.0f, -1.0f, 0f,
                1.0f, 1.0f, 0f,
                1.0f, -1.0f, 0f,
        };

        private static final short[] VERTEX_INDEX = {0, 1, 2, 2, 1, 3};

        // 纹理顶点坐标 指定了截取纹理区域的坐标，手机预览时，以图片 左上角为远点，y轴向下 为正，x轴向右 为正
        private static final float[] TEX_VERTEX = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
        };

        private FloatBuffer mVertexBuffer;
        private ShortBuffer mVertexIndexBuffer;
        private FloatBuffer mTexVertexBuffer;
        private int mProgram;
        private int mUMVPMatrix;
        private float[] mMVPMatrix = new float[16];
        private float[] mProjectMatrix = new float[16];
        private float[] mViewMatrix = new float[16];

        public MyImageRender(Context context)
        {
            mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.gles20_test_img);

            mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(VERTEX);
            mVertexBuffer.position(0);

            mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer().put(VERTEX_INDEX);
            mVertexIndexBuffer.position(0);

            mTexVertexBuffer = ByteBuffer.allocateDirect(TEX_VERTEX.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(TEX_VERTEX);
            mTexVertexBuffer.position(0);
        }

        static int loadShader(int type, String shader_code)
        {
            int shader = GLES20.glCreateShader(type);
            GLES20.glShaderSource(shader, shader_code);
            GLES20.glCompileShader(shader);
            return shader;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config)
        {
            GLES20.glClearColor(0, 0, 0, 0);
            GLES20.glEnable(GLES20.GL_TEXTURE_2D);

            mProgram = GLES20.glCreateProgram();

            int vertex_shader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);

            int fragment_shader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);

            GLES20.glAttachShader(mProgram, vertex_shader);
            GLES20.glAttachShader(mProgram, fragment_shader);
            GLES20.glLinkProgram(mProgram);
            GLES20.glUseProgram(mProgram);

            int vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");

            int a_texCoord = GLES20.glGetAttribLocation(mProgram, "a_texCoord");

            mUMVPMatrix = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

            GLES20.glEnableVertexAttribArray(vPosition);
            GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);

            GLES20.glEnableVertexAttribArray(a_texCoord);
            GLES20.glVertexAttribPointer(a_texCoord, 2, GLES20.GL_FLOAT, false, 8, mTexVertexBuffer);

            int[] texNames = new int[1];
            GLES20.glGenTextures(1, texNames, 0);
            mTextureId = texNames[0];

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_REPEAT);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height)
        {
            GLES20.glViewport(0, 0, width, height);

            int w = mBitmap.getWidth();
            int h = mBitmap.getHeight();
            float sWH = w / (float) h;
            float sWidthHeight = width / (float) height;

            if (width > height)
            {
                if (sWH > sWidthHeight)
                {
                    /**
                     * Matrix.frustumM(float[] m, int offset,
                     *                 float left, float right, float bottom, float top,
                     *                 float near, float far)
                     *
                     * 关于 透视投影 的 params 个人理解:
                     * float[] m: 接收矩阵数据的数组
                     * int offset: 从哪个下标开始写入数据
                     * float left、right、bottom、top:
                     */
                    Matrix.frustumM(mProjectMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 5);
                }
                else
                {
                    Matrix.frustumM(mProjectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 5);
                }
            }
            else
            {
                if (sWH > sWidthHeight)
                {
                    Matrix.frustumM(mProjectMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 5);
                }
                else
                {
                    Matrix.frustumM(mProjectMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 5);
                }
            }
            //设置相机位置
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            //计算变换矩阵
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl)
        {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            GLES20.glUniformMatrix4fv(mUMVPMatrix, 1, false, mMVPMatrix, 0);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
        }
    }
}
