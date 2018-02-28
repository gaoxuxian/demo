package xx.demo.activity.media;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import xx.demo.activity.BaseActivity;

/**
 * OpenGL ES20 基本使用 一个简单的三角形
 * <p>
 * 在 OpenGL 里有两个最基本的概念：Vertex 和 Fragment。
 * <p>
 * 一切图形都从 Vertix (顶点) 开始，Vertix 序列围成了一个图形。
 * <p>
 * 那什么是 Fragment 呢？为此我们需要了解一下光栅化 (Rasterization)：
 * <p>
 * 光栅化是把点、线、三角形映射到屏幕上的像素点的过程(每个映射区域叫一个 Fragment)，也就是生成 Fragment 的过程。
 * <p>
 * 通常一个 Fragment 对应于屏幕上的一个像素，但高分辨率的屏幕可能会用多个像素点映射到一个 Fragment，以减少 GPU 的工作。
 * <p>
 * 接下来介绍 Shader (着色器程序)
 * <p>
 * Shader 用来描述如何绘制(渲染)，GLSL 是 OpenGL 的编程语言，全称 OpenGL Shader Language，它的语法类似于 C 语言。
 * <p>
 * OpenGL 渲染需要两种 Shader：Vertex Shader 和 Fragment Shader。
 * <p>
 * 参考资料:
 * <p>
 * https://blog.piasy.com/2016/06/07/Open-gl-es-android-2-part-1/
 * <p>
 * https://blog.piasy.com/2016/06/14/Open-gl-es-android-2-part-2/
 * <p>
 * https://blog.piasy.com/2017/10/06/Open-gl-es-android-2-part-3/
 * <p>
 * OpenGL ES20 在线文档
 * <p>
 * http://www.khronos.org/registry/OpenGL-Refpages/es2.0/
 * <p>
 * http://blog.db-in.com/all-about-opengl-es-2-x-part-1/
 */
public class GLES20Activity extends BaseActivity
{
    /**
     * 基础知识
     * <p>
     * 每个 Vertex 都会执行一遍 Vertex Shader，以确定 Vertex 的最终位置，
     * <p>
     * 其 main 函数中必须设置 gl_Position 全局变量，它将作为该 Vertex 的最终位置，进而把 Vertex 组合（assemble）成点、线、三角形。
     * <p>
     * 光栅化之后，每个 Fragment 都会执行一次 Fragment Shader，以确定每个 Fragment 的颜色，其 main 函数中必须设置 gl_FragColor 全局变量，它将作为该 Fragment 的最终颜色。
     */
    private GLSurfaceView mGlSurfaceView;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mGlSurfaceView = new GLSurfaceView(parent.getContext());
        // gl surface view 基本设置
        mGlSurfaceView.setEGLContextClientVersion(2);// 设置 GL ES 版本
        mGlSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);// 系统默认会使用 RGB_888, a depth buffer depth of at least 16 bits 配置
        mGlSurfaceView.setRenderer(new MyRender());// 将 GLSurfaceView 和 Renderer 连接起来
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY); // 设置渲染方式
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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

    private static class MyRender implements GLSurfaceView.Renderer
    {
        private int mShaderProgram;

        private static final String VERTEX_SHADER =
                "attribute vec4 vPosition;\n"
                        + "uniform mat4 uMVPMatrix;\n"
                        + "void main() {\n"
                        + " gl_Position = uMVPMatrix * vPosition;\n"
                        + "}";

        private static final String FRAGMENT_SHADER =
                "precision mediump float;\n"
                        + "void main() {\n"
                        + " gl_FragColor = vec4(0.5, 0, 0, 1);\n"
                        + "}";

        private static final float[] VERTEX = {
                0, 1, 0,
                -0.5f, -1, 0,
                1, -1, 0,
        };

        private FloatBuffer mVertexBuffer;
        private float[] mMVPMatrix = new float[16]; // 数组大小 请看系统 api ( android.opengl.Matrix.perspectiveM() ) 实现
        private int mUMVPMatrix;

        public MyRender()
        {
            mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(VERTEX);
            mVertexBuffer.position(0);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config)
        {
            // FIXME: 2018/2/27 MyRender --> onSurfaceCreated 这一句暂时不理解原理或者意义
            GLES20.glClearColor(0f, 0f, 0f, 0f);

            /**
             * 创建 GLSL 程序：glCreateProgram
             * 加载 shader 代码：glShaderSource 和 glCompileShader
             * attatch shader 代码：glAttachShader
             * 链接 GLSL 程序：glLinkProgram
             * 使用 GLSL 程序：glUseProgram
             * 获取 shader 代码中的变量索引：glGetAttribLocation
             * 启用 vertex：glEnableVertexAttribArray
             * 绑定 vertex 坐标值：glVertexAttribPointer
             */

            mShaderProgram = GLES20.glCreateProgram();
            int vertex_shader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
            int fragment_shader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
            GLES20.glAttachShader(mShaderProgram, vertex_shader);
            GLES20.glAttachShader(mShaderProgram, fragment_shader);
            GLES20.glLinkProgram(mShaderProgram);
            GLES20.glUseProgram(mShaderProgram);

            int vPosition = GLES20.glGetAttribLocation(mShaderProgram, "vPosition");
            mUMVPMatrix = GLES20.glGetUniformLocation(mShaderProgram, "uMVPMatrix");
            GLES20.glEnableVertexAttribArray(vPosition); // 我的理解：使索引对应的 c 对象 可被修改
            // 给画笔指定顶点位置数据
            /*
            params 理解：
            (1) int index: 与 c语言中 顶点着色器 需要被修改的位置 对应的索引 (大概意思：给定一个索引，让 openGl 找到具体要修改 c 里面的哪个对象)
            (2) int size: 顶点是 几维坐标 (x,y,z) == 3
            (3) int type: 顶点坐标，每一个值x, y, z的数据类型
            (4) boolean normalized:
            (5) int stride: 一个顶点坐标包含的字节数 float --> 4 byte, int --> 4 byte, short --> 2 byte, long --> 8 byte, char --> 2 byte
            (6) Buffer: java 层记录所有顶点数据的 缓冲区
             */
            GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height)
        {
            // FIXME: 2018/2/27 MyRender --> onSurfaceChanged 这一句暂时不理解做法或意义
            GLES20.glViewport(0, 0, width, height);

            Matrix.perspectiveM(mMVPMatrix, 0, 45, width * 1f / height, 0.1f, 100f);
            Matrix.translateM(mMVPMatrix, 0, 0, 0, -5f);
        }

        @Override
        public void onDrawFrame(GL10 gl)
        {
            // FIXME: 2018/2/27 MyRender --> onDrawFrame 这一句暂时不理解做法或意义
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            // 修改矩阵状态
            GLES20.glUniformMatrix4fv(mUMVPMatrix, 1, false, mMVPMatrix, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        }

        static int loadShader(int type, String shader_code)
        {
            int shader = GLES20.glCreateShader(type);
            GLES20.glShaderSource(shader, shader_code);
            GLES20.glCompileShader(shader);

            return shader;
        }
    }
}
