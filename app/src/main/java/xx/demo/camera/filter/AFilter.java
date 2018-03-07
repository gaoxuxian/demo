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

    /**
     * 顶点坐标
     */
    protected float[] vertex_pos_arr;

    /**
     * 纹理坐标
     */
    protected float[] texture_pos_arr;

    /**
     * 索引坐标
     */
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

        /**
         * glTexParameteri(int target, int pname, int param)
         * target: 当前绑定的纹理类型，1D、2D、3D 等
         * pname: 滤波方法名
         * param: 滤波方式
         *
         * PS:纹理坐标系用S-T来表示，S为横轴，T为纵轴。
         *
         * glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
         * GL_TEXTURE_2D: 操作2D纹理.
         * GL_TEXTURE_WRAP_S: S方向上的贴图模式.
         * GL_CLAMP: 将纹理坐标限制在0.0,1.0的范围之内.如果超出了会如何呢.不会错误,只是会边缘拉伸填充.

         * glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
         * 这里同上,只是它是T方向

         * glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
         * 这是纹理过滤
         * GL_TEXTURE_MAG_FILTER: 放大过滤
         * GL_LINEAR: 线性过滤, 使用距离当前渲染像素中心最近的4个纹素加权平均值.

         * glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
         * GL_TEXTURE_MIN_FILTER: 缩小过滤
         * GL_LINEAR_MIPMAP_NEAREST: 使用GL_NEAREST对最接近当前多边形的解析度的两个层级贴图进行采样,然后用这两个值进行线性插值.
         */
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

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
        // 使某句柄可用
        GLES20.glEnableVertexAttribArray(mHVertexPos);
        // 将顶点坐标赋值到 顶点着色器 的顶点句柄
        GLES20.glVertexAttribPointer(mHVertexPos, 2, GLES20.GL_FLOAT, false, 0, mVerBuffer);

        // 使某句柄可用
        GLES20.glEnableVertexAttribArray(mHTexturePos);
        // 将纹理坐标赋值到 片段着色器 的纹理句柄
        GLES20.glVertexAttribPointer(mHTexturePos, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);

        // 根据 顶点坐标index 顺序 绘制 顶点坐标
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
     * <p>
     * 解释 纹理单元、纹理对象、多纹理处理 的情况
     * <p>
     * http://blog.csdn.net/jackyqiziheng/article/details/77294363
     */
    protected void onBindTexture()
    {
        // 单纹理
        /**
         * glActiveTexture可以采取的实际范围受制于GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS。这是实现允许的同时多纹理的最大数量。
         * 正确的调用方式glActiveTexture如下：
         * glActiveTexture(GL_TEXTURE0 + i); where i is a number between 0 and GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS.
         */
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureType); // 设置当前活跃的纹理单元，默认纹理单元 0 == GLES20.GL_TEXTURE0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, getTextureId()); // 将纹理 id 绑定到 当前活跃的纹理单元上
        GLES20.glUniform1i(mHTexture, textureType); // 告诉 fragment shader 需要使用纹理单元0,所以传了一个0参数进去给 sampler2D ，这个采样器就会到纹理单元0中去采样

        // 多纹理
        /**
         * // 创建两个纹理对象
         * int[] textureHandle = new int[2];
         * GLES20.glGenTextures(2, textureHandle, 0); // 第一个参数: 纹理对象的总数量，第三个参数: 从数组的哪个位置开始生成纹理id

         * // 激活纹理单元0
         * glActiveTexture(GL_TEXTURE0);
         * // 绑定纹理对象和纹理单元0
         * glBindTexture(GL_TEXTURE2D,textureHandle[0]);
         * // 把bitmap存放在纹理对象对应的显存中
         * GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap1, 0);

         * // 激活纹理单元1
         * glActiveTexture(GL_TEXTURE1);
         * // 绑定纹理对象和纹理单元1
         * glBindTexture(GL_TEXTURE2D,textureHandle[1]);
         * // 把bitmap存放在纹理对象对应的显存中
         * GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap2, 0);

         * // 设置纹理采样器到纹理单元0中采样
         * GLES20.glUniform1i(mTextureHandler1, 0);
         * GLES20.glUniform1i(mTextureHandler2, 1);
         */
    }
}
