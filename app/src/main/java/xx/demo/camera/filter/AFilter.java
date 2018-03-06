package xx.demo.camera.filter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import xx.demo.BuildConfig;
import xx.demo.util.MatrixUtil;

public abstract class AFilter
{
    private static boolean DEBUG = BuildConfig.DEBUG;
    private static String TAG = AFilter.class.getSimpleName();

    /**
     * 单位矩阵
     */
    public static final float[] OM = MatrixUtil.getOpenGLUnitMatrix();
    /**
     * 程序句柄
     */
    protected int mProgram;
    /**
     * 顶点坐标句柄
     */
    protected int mHVertexPos;
    /**
     * 纹理坐标句柄
     */
    protected int mHTexturePos;
    /**
     * 总变换矩阵句柄
     */
    protected int mHMatrix;
    /**
     * 默认纹理贴图句柄
     */
    protected int mHTexture;

    protected Resources mRes;

    /**
     * 顶点坐标Buffer
     */
    protected FloatBuffer mVerBuffer;

    /**
     * 纹理坐标Buffer
     */
    protected FloatBuffer mTexBuffer;

    /**
     * 索引坐标Buffer
     */
    protected IntBuffer mIndexBuffer;

    //顶点坐标
    protected float[] vertex_pos_arr;

    //纹理坐标
    protected float[] texture_pos_arr;

    //索引坐标
    protected int[] index_arr;

    private float[] temp_matrix = new float[16];

    private int textureType = 0;      //默认使用Texture2D0
    private int textureId = 0;

    public AFilter(Resources resources)
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
        mRes = resources;
        initArr();
        initBuffer();
    }

    public void create()
    {
        onCreate();
    }

    public final void setSize(int width, int height)
    {
        onSizeChanged(width, height);
    }

    public void setMatrix(float[] matrix)
    {
        temp_matrix = matrix;
    }

    /**
     * 实现此方法，完成程序的创建，可直接调用createProgram来实现
     */
    protected abstract void onCreate();

    /**
     * 需要手动绑定不同着色器定义的句柄
     */
    protected abstract void bindHandleInProgram(int program);

    protected abstract void onSizeChanged(int width, int height);

    protected void initArr()
    {
        vertex_pos_arr = new float[]{
                -1.0f, 1.0f,
                -1.0f, -1.0f,
                1.0f, 1.0f,
                1.0f, -1.0f,
        };

        texture_pos_arr = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
        };

        index_arr = new int[]{0, 1, 2, 2, 1, 3};
    }

    protected void initBuffer()
    {
        mVerBuffer = ByteBuffer.allocateDirect(vertex_pos_arr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertex_pos_arr);
        mVerBuffer.position(0);

        mTexBuffer = ByteBuffer.allocateDirect(texture_pos_arr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(texture_pos_arr);
        mTexBuffer.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(index_arr.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer().put(index_arr);
        mIndexBuffer.position(0);
    }

    public final void setTextureType(int type)
    {
        this.textureType = type;
    }

    public final int getTextureType()
    {
        return textureType;
    }

    public final int getTextureId()
    {
        return textureId;
    }

    public final void setTextureId(int textureId)
    {
        this.textureId = textureId;
    }

    public static void glError(Object msg)
    {
        if (DEBUG)
        {
            Log.e(TAG, "glError: ---> " + msg);
        }
    }

    public int createBmpTextureID(Bitmap bitmap)
    {
        int textureID = createTextureID();

        if (bitmap != null && !bitmap.isRecycled())
        {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }

        return textureID;
    }

    public int createTextureID()
    {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);
        return texture[0];
    }

    protected final void CreateProgramByAssetsFile(String vertex, String fragment)
    {
        CreateProgram(uRes(mRes, vertex), uRes(mRes, fragment));
    }

    protected final void CreateProgram(String vertex, String fragment)
    {
        mProgram = sCreateGlProgram(vertex, fragment);
        bindHandleInProgram(mProgram);
    }

    //通过路径加载Assets中的文本内容
    public static String uRes(Resources mRes, String path)
    {
        StringBuilder result = new StringBuilder();
        try
        {
            InputStream is = mRes.getAssets().open(path);
            int ch;
            byte[] buffer = new byte[1024];
            while (-1 != (ch = is.read(buffer)))
            {
                result.append(new String(buffer, 0, ch));
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return result.toString().replaceAll("\\r\\n", "\n");
    }

    //创建GL程序
    public static int sCreateGlProgram(String vertexSource, String fragmentSource)
    {
        /**
         * 流程:
         * 创建 GLSL 程序：glCreateProgram
         * 加载 shader 代码：glShaderSource 和 glCompileShader
         * attatch shader 代码：glAttachShader
         * 链接 GLSL 程序：glLinkProgram
         * 使用 GLSL 程序：glUseProgram
         * 获取 shader 代码中的变量索引：glGetAttribLocation
         * 启用 vertex：glEnableVertexAttribArray
         * 绑定 vertex 坐标值：glVertexAttribPointer
         */
        int vertex = sLoadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertex == 0) return 0;
        int fragment = sLoadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragment == 0) return 0;
        int program = GLES20.glCreateProgram();
        if (program != 0)
        {
            GLES20.glAttachShader(program, vertex);
            GLES20.glAttachShader(program, fragment);
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE)
            {
                glError("Could not link program:" + GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    //加载shader
    public static int sLoadShader(int shaderType, String source)
    {
        int shader = GLES20.glCreateShader(shaderType);
        if (0 != shader)
        {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0)
            {
                glError("Could not compile shader:" + shaderType);
                glError("GLES20 Error:" + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public void draw()
    {
        onClear();
        onUseProgram();
        onSetExpandData();
        onBindTexture();
        onDraw();
    }

    protected void onUseProgram()
    {
        GLES20.glUseProgram(mProgram);
    }

    /**
     * 启用顶点坐标和纹理坐标进行绘制
     */
    protected void onDraw()
    {
        GLES20.glEnableVertexAttribArray(mHVertexPos);
        GLES20.glVertexAttribPointer(mHVertexPos, 2, GLES20.GL_FLOAT, false, 0, mVerBuffer);

        GLES20.glEnableVertexAttribArray(mHTexturePos);
        GLES20.glVertexAttribPointer(mHTexturePos, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, vertex_pos_arr.length, GLES20.GL_UNSIGNED_INT, mIndexBuffer);

        GLES20.glDisableVertexAttribArray(mHVertexPos);
        GLES20.glDisableVertexAttribArray(mHTexturePos);
    }

    /**
     * 清除画布
     */
    protected void onClear()
    {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * 设置其他扩展数据
     */
    protected void onSetExpandData()
    {
        GLES20.glUniformMatrix4fv(mHMatrix, 1, false, temp_matrix, 0);
    }

    /**
     * 绑定默认纹理
     */
    protected void onBindTexture()
    {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureType);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, getTextureId());
        GLES20.glUniform1i(mHTexture, textureType);
    }
}
