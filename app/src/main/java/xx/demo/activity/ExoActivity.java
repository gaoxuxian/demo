package xx.demo.activity;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import xx.demo.exoPlayer.ExoVideoView;
import xx.demo.util.FileUtil;

public class ExoActivity extends Activity
{
    FrameLayout mParent;
    ExoVideoView mExoVideoView;

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

        String path = FileUtil.getSDPath() + File.separator + "DCIM" + File.separator + "CAMERA" + File.separator + "12345test.mp4";
        File file = new File(path);
        if (file.exists())
        {
            MediaSource mediaSource = mediaSourceFactory.createMediaSource(Uri.fromFile(file));
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(true);
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
}
