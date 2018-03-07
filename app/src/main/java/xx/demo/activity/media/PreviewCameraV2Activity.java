package xx.demo.activity.media;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import xx.demo.activity.BaseActivity;
import xx.demo.camera.CameraGLView;

/**
 * 解释 使用 GLSurface view 时，为何绑定纹理id 之后，使用 OpenGL 渲染，可以直接渲染到 GLSurface view 的 surface 上 ( 关键点：eglMakeCurrent() )
 * <p>
 *     <该博客上还附有其他 Android 游戏开发的大神博客>
 * <p>
 * http://blog.csdn.net/happy19850920
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
