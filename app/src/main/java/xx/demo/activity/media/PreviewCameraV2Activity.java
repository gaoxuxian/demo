package xx.demo.activity.media;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import xx.demo.activity.BaseActivity;
import xx.demo.camera.CameraGLView;

/**
 * 值得注意的是，在Android中Camera产生的preview texture是以一种特殊的格式传送的，
 * <p>
 * 因此shader里的纹理类型并不是普通的sampler2D,而是samplerExternalOES, 在shader的头部也必须声明OES 的扩展。
 * <p>
 * 除此之外，external OES的纹理和Sampler2D在使用时没有差别。
 * <p>
 * Android的Camera及Camera2都允许使用SurfaceTexture作为预览载体，
 * <p>
 * 但是它们所使用的SurfaceTexture传入的OpenGL texture object name必须为GLES11Ext.GL_TEXTURE_EXTERNAL_OES。
 * <p>
 * 这种方式，实际上就是两个OpenGL Thread共享一个Texture，不再需要数据导入导出，从Camera采集的数据直接在GPU中完成转换和渲染。
 */

public class PreviewCameraV2Activity extends BaseActivity
{
    private CameraGLView mCameraView;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mCameraView = new CameraGLView(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mCameraView, params);
    }

    @Override
    protected void onPause()
    {
        mCameraView.onStop();
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        mCameraView.onStop();
        super.onDestroy();
    }
}
