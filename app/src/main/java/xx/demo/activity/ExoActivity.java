package xx.demo.activity;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import xx.demo.MyApplication;
import xx.demo.exoPlayer.ExoVideoView;
import xx.demo.util.FileUtil;
import xx.demo.util.VideoUtil;
import xx.demo.videocache.MyFileNameGenerator;

public class ExoActivity extends Activity
{
    FrameLayout mParent;
    ExoVideoView mExoVideoView;
    // 网络视频
    private String TEST_NET_WORK_VIDEO_PATH = "http://biz-zt-oss.adnonstop.com/ar_201802/20180208/22/020180208224311_5723_7811565338.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        initUI(this);
        initPlayer(this);
    }

    private void initPlayer(Context context)
    {
        TrackSelector trackSelector = new DefaultTrackSelector();
        SimpleExoPlayer simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        if (mExoVideoView != null)
        {
            mExoVideoView.setPlayer(simpleExoPlayer);
        }

        DataSource.Factory factory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getApplicationInfo().packageName));

        ExtractorMediaSource.Factory mediaSourceFactory = new ExtractorMediaSource.Factory(factory);
        mediaSourceFactory.setExtractorsFactory(new DefaultExtractorsFactory());

        // 实体机路径
        String path = FileUtil.getSDPath() + File.separator + "DCIM" + File.separator + "CAMERA" + File.separator + "阳光下被风吹拂的绿叶.mp4";
        String path2 = FileUtil.getSDPath() + File.separator + "DCIM" + File.separator + "CAMERA" + File.separator + "201710091709290067.mp4";
        String path3 = FileUtil.getSDPath() + File.separator + "DCIM" + File.separator + "CAMERA" + File.separator + "15071604420170922143714755.mp4";

        ExtractorMediaSource mediaSource = mediaSourceFactory.createMediaSource(Uri.parse(path));
        ExtractorMediaSource mediaSource2 = mediaSourceFactory.createMediaSource(Uri.parse(path2));
        ExtractorMediaSource mediaSource3 = mediaSourceFactory.createMediaSource(Uri.parse(path3));
        long duration = VideoUtil.GetVideoDuration(path);
        duration += VideoUtil.GetVideoDuration(path2);
        duration += VideoUtil.GetVideoDuration(path3);
        if (mExoVideoView != null)
        {
            mExoVideoView.setDuration(duration);
        }
//        Log.d("xxx", "ExoActivity --> initPlayer: 开始 == " + System.currentTimeMillis());
//        Log.d("xxx", "ExoActivity --> initPlayer: path duration == " + VideoUtil.GetVideoDuration(path));
//        Log.d("xxx", "ExoActivity --> initPlayer: path2 duration == " + VideoUtil.GetVideoDuration(path2));
//        Log.d("xxx", "ExoActivity --> initPlayer: path3 duration == " + VideoUtil.GetVideoDuration(path3));
//        Log.d("xxx", "ExoActivity --> initPlayer: 结束 == " + System.currentTimeMillis());
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource(mediaSource, mediaSource2, mediaSource3);
        simpleExoPlayer.prepare(concatenatingMediaSource);
        simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        simpleExoPlayer.setPlayWhenReady(true);
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
        super.onDestroy();

//        HttpProxyCacheServer proxy = MyApplication.getProxy(this);
//        proxy.unregisterCacheListener(this);
//        proxy.shutdown();
    }
}
