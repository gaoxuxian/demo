package xx.demo.activity.media;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import xx.demo.activity.BaseActivity;

public class GLESBaseV3Activity extends BaseActivity
{
    private GLSurfaceView mGlSurfaceView;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mGlSurfaceView = new GLSurfaceView(parent.getContext());
        mGlSurfaceView.setEGLContextClientVersion(2);
        mGlSurfaceView.setRenderer(new MyRender());
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mGlSurfaceView, params);
    }

    private static class MyRender implements GLSurfaceView.Renderer
    {

        private static final String VERTEX_SHADER =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 vPosition;" +
                        "void main() {" +
                        " gl_Position = vPosition;" +
                        "}";

        private static final String FRAGMENT_SHADER =
                "precision mediump float;" +
                        "uniform vec4 vColor;" +
                        "void main() {" +
                        " gl_FragColor = vColor;" +
                        "}";

        // 顶点坐标 规定了一个 可视区域的范围，不一定非得是正方形
        private static final float[] VERTEX = {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
                0.5f, -0.5f, 0f,
        };

        private static final int[] VERTEX_INDEX = new int[]{0, 1, 2, 2, 1, 3};

        private static final float[] color = new float[]{0.5f, 0f, 0f, 1f};

        private int mProgram;
        private int mHVertex;
        private int mHFragmentColor;

        private FloatBuffer mVertexBuffer;
        private IntBuffer mVertexIndexBuffer;
        private FloatBuffer mFragmentColorBuffer;

        public MyRender()
        {
            mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(VERTEX);
            mVertexBuffer.position(0);

            mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer().put(VERTEX_INDEX);
            mVertexIndexBuffer.position(0);

            mFragmentColorBuffer = ByteBuffer.allocateDirect(color.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(color);
            mFragmentColorBuffer.position(0);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config)
        {
            mProgram = GLES20.glCreateProgram();
            int vertex_shader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
            GLES20.glShaderSource(vertex_shader, VERTEX_SHADER);
            GLES20.glCompileShader(vertex_shader);
            GLES20.glAttachShader(mProgram, vertex_shader);

            int fragment_shader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
            GLES20.glShaderSource(fragment_shader, FRAGMENT_SHADER);
            GLES20.glCompileShader(fragment_shader);
            GLES20.glAttachShader(mProgram, fragment_shader);

            GLES20.glLinkProgram(mProgram);

            mHVertex = GLES20.glGetAttribLocation(mProgram, "vPosition");
            mHFragmentColor = GLES20.glGetUniformLocation(mProgram, "vColor");
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height)
        {
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl)
        {
            // 清除 GL 的帧缓冲区(大概意思是：上一帧的数据?)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            GLES20.glUseProgram(mProgram);

            GLES20.glEnableVertexAttribArray(mHVertex);
            GLES20.glVertexAttribPointer(mHVertex, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);

            GLES20.glUniform4fv(mHFragmentColor, 1, mFragmentColorBuffer);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length, GLES20.GL_UNSIGNED_INT, mVertexIndexBuffer);

            GLES20.glDisableVertexAttribArray(mHVertex);
        }
    }
}
