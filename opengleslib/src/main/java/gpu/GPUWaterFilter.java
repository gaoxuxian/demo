package gpu;

import android.content.res.Resources;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import util.ByteBufferUtil;
import util.GLES20Util;

public class GPUWaterFilter extends GPUFilter
{
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureIndexBuffer;
    private ShortBuffer mVertexIndexBuffer;

    private int mWaterProgram;

    private int vPosition;
    private int vCoordinate;
    private int vMatrix;
    private int vTexture;
    private WaterEntry mWaterEntry;
    private ImageEntry mImageEntry;

    public GPUWaterFilter(Resources resources)
    {
        super(resources);
    }

    public void setWaterID(Object id)
    {
        mWaterEntry = new WaterEntry(getResource());
        mWaterEntry.setWaterID(id);
    }

    public void setImage(Object image)
    {
        mImageEntry = new ImageEntry(getResource());
        mImageEntry.setImage(image);
    }

    @Override
    public void onSurfaceCreated()
    {
        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(VertexConstant.CUBE);
        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(VertexConstant.TEXTURE);
        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(VertexConstant.CUBE_INDEX);

        int vertex_shader = GLES20Util.sGetShader(getResource(), GLES20.GL_VERTEX_SHADER, "shader/simple2D/picture_vertex_shader.glsl");
        int fragment_shader = GLES20Util.sGetShader(getResource(), GLES20.GL_VERTEX_SHADER, "shader/simple2D/picture_fragment_shader.glsl");

        mWaterProgram = GLES20Util.sCreateAndLinkProgram(vertex_shader, fragment_shader);

        vPosition = GLES20.glGetAttribLocation(mWaterProgram, "vPosition");
        vCoordinate = GLES20.glGetAttribLocation(mWaterProgram, "vCoordinate");
        vMatrix = GLES20.glGetUniformLocation(mWaterProgram, "vMatrix");
        vTexture = GLES20.glGetUniformLocation(mWaterProgram, "vTexture");
    }

    @Override
    public void onSurfaceChanged(int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrameAsRender()
    {
        GLES20.glUseProgram(mWaterProgram);

        FrameBufferMgr frameBufferMgr = getFrameBufferMgr();
        if (frameBufferMgr == null)
        {
            frameBufferMgr = new FrameBufferMgr(getSurfaceWidth(), getSurfaceHeight(), 1);
        }
        frameBufferMgr.bindNext(true, 0);


    }

    @Override
    public void onDrawFrame(int textureID)
    {

    }

    @Override
    public void onDrawFrameBuffer(int textureID)
    {

    }
}
