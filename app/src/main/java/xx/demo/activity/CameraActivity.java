package xx.demo.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import xx.demo.R;
import xx.demo.camera.PopSettingItem;
import xx.demo.exoPlayer.WaitProgressView;
import xx.demo.util.CameraPercentUtil;
import xx.demo.util.ImageUtil;

public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback, Camera.PreviewCallback, View.OnClickListener
{
    private SurfaceView mSurfaceView;
    private ImageView mSettingView;
    private RecyclerView mSettingPopView;
    private TextView mTakePicBtn;
    private TextView mRecordBtn;
    private WaitProgressView mProgressView;
    private Camera mCamera;
    private String TAG = "xxx";

    private String[] mPopItemTitleArr;
    private boolean showFlash;
    private int cameraDegree = 90;
    private boolean isFront;
    private boolean canStartRecord;

    @Override
    protected void initData()
    {
        mPopItemTitleArr = new String[]{
                "闪光灯", "前置镜头", "调整镜头角度"
        };
    }

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mSurfaceView = new SurfaceView(parent.getContext());
        mSurfaceView.getHolder().addCallback(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        parent.addView(mSurfaceView, params);

        mSettingView = new ImageView(parent.getContext());
        mSettingView.setOnClickListener(this);
        mSettingView.setImageResource(R.drawable.camera_setting);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = CameraPercentUtil.WidthPxToPercent(25);
        parent.addView(mSettingView, params);

        mTakePicBtn = new TextView(parent.getContext());
        mTakePicBtn.setBackgroundColor(Color.WHITE);
        mTakePicBtn.setOnClickListener(this);
        mTakePicBtn.setText("开始拍照");
        mTakePicBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        mTakePicBtn.setTextColor(Color.BLACK);
        mTakePicBtn.setGravity(Gravity.CENTER);
        mTakePicBtn.setPadding(CameraPercentUtil.WidthPxToPercent(20), 0,CameraPercentUtil.WidthPxToPercent(20),0);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, CameraPercentUtil.WidthPxToPercent(100));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.bottomMargin = CameraPercentUtil.WidthPxToPercent(100);
        params.rightMargin = CameraPercentUtil.WidthPxToPercent(200);
        parent.addView(mTakePicBtn, params);

        mRecordBtn = new TextView(parent.getContext());
        mRecordBtn.setBackgroundColor(Color.WHITE);
        mRecordBtn.setOnClickListener(this);
        mRecordBtn.setText("准备录制");
        mRecordBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        mRecordBtn.setTextColor(Color.BLACK);
        mRecordBtn.setGravity(Gravity.CENTER);
        mRecordBtn.setPadding(CameraPercentUtil.WidthPxToPercent(20), 0,CameraPercentUtil.WidthPxToPercent(20),0);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, CameraPercentUtil.WidthPxToPercent(100));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.bottomMargin = CameraPercentUtil.WidthPxToPercent(100);
        params.leftMargin = CameraPercentUtil.WidthPxToPercent(200);
        parent.addView(mRecordBtn, params);

        mProgressView = new WaitProgressView(parent.getContext());
        mProgressView.setProgressColor(Color.RED);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        parent.addView(mProgressView, params);

        mSettingPopView = new RecyclerView(parent.getContext());
        mSettingPopView.setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLACK, (int) (255 * 0.4f)));
        mSettingPopView.setLayoutManager(new LinearLayoutManager(parent.getContext(), LinearLayoutManager.VERTICAL, false));
        mSettingPopView.setVisibility(View.GONE);
        params = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(620), CameraPercentUtil.WidthPxToPercent(800));
        params.gravity = Gravity.CENTER;
        parent.addView(mSettingPopView, params);

        initPopAdapter();
    }

    private void initPopAdapter()
    {
        SimpleRcAdapter adapter = new SimpleRcAdapter(new Source()
        {
            @Override
            public Object getSource(Object key)
            {
                Object out = null;

                if (mPopItemTitleArr != null)
                {
                    out = mPopItemTitleArr[(int) key];
                }
                return out;
            }

            @Override
            public int getSourceSize()
            {
                return mPopItemTitleArr != null ? mPopItemTitleArr.length : 0;
            }

            @Override
            public void onSourceClick(Object source_key)
            {
                if (mCamera != null && source_key instanceof Integer)
                {
                    switch ((int) source_key)
                    {
                        // 闪光灯
                        case 0:
                            showFlash = !showFlash;
                            Camera.Parameters parameters = mCamera.getParameters();
                            parameters.setFlashMode(showFlash ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
                            mCamera.setParameters(parameters);
                            break;
                        // 前置镜头
                        case 1:
                            isFront = !isFront;
                            mCamera.stopPreview();
                            mCamera.release();
                            try
                            {
                                mCamera = Camera.open(isFront ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK);
                            }
                            catch (Exception e)
                            {

                            }

                            if (mCamera != null)
                            {
                                mCamera.setDisplayOrientation(cameraDegree);
                                try
                                {
                                    mCamera.setPreviewDisplay(mSurfaceView.getHolder());
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                                mCamera.startPreview();
                            }
                            break;
                        // 调整镜头角度
                        case 2:
                            cameraDegree += 90;
                            cameraDegree %= 360;
                            mCamera.setDisplayOrientation(cameraDegree);
                            break;
                    }
                }
            }
        });
        mSettingPopView.setAdapter(adapter);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.d(TAG, "CameraActivity --> surfaceCreated: ");

        if (Build.VERSION.SDK_INT >= 21)
        {
            CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            if (cameraManager != null)
            {
                try
                {
                    CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics("0");
                    int supported_level = cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    Log.d(TAG, "CameraActivity --> surfaceCreated ---> supported level == " + supported_level);
                }
                catch (CameraAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }

        mCamera = Camera.open();

        if (mCamera != null)
        {
            mCamera.setDisplayOrientation(90);

            try
            {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        Log.d(TAG, "CameraActivity --> surfaceChanged: ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.d(TAG, "CameraActivity --> surfaceDestroyed: ");

        if (mSurfaceView != null)
        {
            mSurfaceView.getHolder().removeCallback(this);
        }

        if (mCamera != null)
        {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera)
    {

    }

    @Override
    public void onClick(View v)
    {
        if (v == mSettingView)
        {
            if (mSettingPopView.getVisibility() == View.VISIBLE)
            {
                mSettingPopView.setVisibility(View.GONE);
            }
            else
            {
                mSettingPopView.setVisibility(View.VISIBLE);
            }
        }
        else if (v == mTakePicBtn)
        {
            if (mCamera != null)
            {
                canStartRecord = false;
                mRecordBtn.setText("准备录制");

                mProgressView.show(true);
                mCamera.takePicture(null, null, null, new Camera.PictureCallback()
                {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera)
                    {
                        final byte[] pic = data;
                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Bitmap bitmap = ImageUtil.rotateAndCropPicture(pic, isFront, 90, 9f / 16f, 1024);
                                final boolean succeed = ImageUtil.saveImage(CameraActivity.this, bitmap, ImageUtil.getOutPutDirectoryPath(), true);
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if (succeed && mCamera != null)
                                        {
                                            mProgressView.show(false);

                                            if (mCamera != null)
                                            {
                                                mCamera.startPreview();
                                            }
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                });
            }
        }
        else if (v == mRecordBtn)
        {
            if (!canStartRecord)
            {

                canStartRecord = true;
                mRecordBtn.setText("开始录制");
                return;
            }


        }
    }

    private static class SimpleRcAdapter extends RecyclerView.Adapter implements View.OnClickListener
    {
        private Source mSourceListener;

        public SimpleRcAdapter(Source source)
        {
            this.mSourceListener = source;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            PopSettingItem itemView = new PopSettingItem(parent.getContext());
            itemView.setPadding(CameraPercentUtil.WidthPxToPercent(20), 0, CameraPercentUtil.WidthPxToPercent(20), 0);
            itemView.setOnClickListener(this);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.WidthPxToPercent(100));
            itemView.setLayoutParams(params);
            return new RecyclerView.ViewHolder(itemView)
            {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            if (mSourceListener == null) return;

            Object source = mSourceListener.getSource(position);

            if (source == null) return;

            if (source instanceof String)
            {
                View itemView = holder.itemView;

                if (itemView == null) return;

                itemView.setTag(position);

                if (itemView instanceof PopSettingItem)
                {
                    ((PopSettingItem) itemView).setTitle((String) source);
                }
            }
        }

        @Override
        public int getItemCount()
        {
            return mSourceListener != null ? mSourceListener.getSourceSize() : 0;
        }

        @Override
        public void onClick(View v)
        {
            if (mSourceListener == null) return;

            int position = (int) v.getTag();

            mSourceListener.onSourceClick(position);
        }
    }

    public interface Source
    {
        /**
         * 根据 key 找到 source
         *
         * @param key 一般是 source 下标
         * @return source
         */
        Object getSource(Object key);

        int getSourceSize();

        /**
         * source 点击事件
         *
         * @param source_key 一般是 source 下标
         */
        void onSourceClick(Object source_key);
    }
}
