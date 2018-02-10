package xx.demo.activity;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;

import java.io.File;

import xx.demo.exoPlayer.CacheDataSourceFactory;
import xx.demo.exoPlayer.ExoVideoView;
import xx.demo.util.ExoPlayerUtil;
import xx.demo.util.FileUtil;
import xx.demo.util.VideoUtil;

public class ExoActivity extends Activity
{
    FrameLayout mParent;
    ExoVideoView mExoVideoView;
    // 网络视频
    private String TEST_NET_WORK_VIDEO_PATH = "http://biz-zt-oss.adnonstop.com/ar_201802/20180208/22/020180208224311_5723_7811565338.mp4";
    private String cache_path = FileUtil.getSDPath() + File.separator + "exo_cache" + File.separator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FileUtil.deleteFile(new File(cache_path), false);
        initUI(this);
        initVideo();
    }

    private void initVideo()
    {
        File file = new File(cache_path);
        if (!file.exists())
        {
            file.mkdirs();
        }

        // 实体机路径
        String path = FileUtil.getSDPath() + File.separator + "DCIM" + File.separator + "CAMERA" + File.separator + "阳光下被风吹拂的绿叶.mp4";
        String path2 = FileUtil.getSDPath() + File.separator + "DCIM" + File.separator + "CAMERA" + File.separator + "201710091709290067.mp4";
        String path3 = FileUtil.getSDPath() + File.separator + "DCIM" + File.separator + "CAMERA" + File.separator + "15071604420170922143714755.mp4";

        long duration = 0;

        duration += VideoUtil.GetVideoDuration(TEST_NET_WORK_VIDEO_PATH);
//        duration += VideoUtil.GetVideoDuration(path);
//        duration += VideoUtil.GetVideoDuration(path2);
//        duration += VideoUtil.GetVideoDuration(path3);

        SimpleExoPlayer simplePlayer = ExoPlayerUtil.createSimplePlayer(this);

//        MediaSource[] mediaSource = ExoPlayerUtil.createMediaSourceInCache(this, cache_path, path, path2, path3);
        MediaSource[] mediaSource = ExoPlayerUtil.createMediaSourceInCache(this, cache_path, TEST_NET_WORK_VIDEO_PATH);

        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource(mediaSource);
        concatenatingMediaSource.releaseSource();
        simplePlayer.prepare(concatenatingMediaSource);
        simplePlayer.setRepeatMode(Player.REPEAT_MODE_ALL);

        if (mExoVideoView != null)
        {
            mExoVideoView.setPlayer(simplePlayer);
            mExoVideoView.setDuration(duration);
            mExoVideoView.start();
        }
    }

    private void initUI(Context context)
    {
        mParent = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mParent.setLayoutParams(params);
        setContentView(mParent);
        {
            mExoVideoView = new ExoVideoView(context);
            mExoVideoView.setSurfaceView(new TextureView(context));
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            mParent.addView(mExoVideoView, params);
        }
    }

    @Override
    protected void onDestroy()
    {
        if (mExoVideoView != null)
        {
            mExoVideoView.release();
        }

        super.onDestroy();
    }
}
