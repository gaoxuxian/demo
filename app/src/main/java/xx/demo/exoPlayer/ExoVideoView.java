package xx.demo.exoPlayer;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;

import xx.demo.util.CameraPercentUtil;
import xx.demo.view.BufferSeekBar;

/**
 * Created by admin on 2018/2/7.
 */

public class ExoVideoView extends FrameLayout implements SimpleExoPlayer.VideoListener, Player.EventListener
{
    private AspectRatioFrameLayout mContentFrame;
    private View mSurfaceView;
    private BufferSeekBar mSeekBar;

    private SimpleExoPlayer mPlayer;
    private Player.EventListener componentListener;
    private String TAG = "ExoVideoView";

    public ExoVideoView(@NonNull Context context)
    {
        super(context);
        initCB();
        init(context);
    }

    private void initCB()
    {

    }

    private void init(Context context)
    {
        mContentFrame = new AspectRatioFrameLayout(context);
        mContentFrame.setAspectRatio(1);
        mContentFrame.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(mContentFrame, params);

        mSeekBar = new BufferSeekBar(context);
        mSeekBar.setColor(ColorUtils.setAlphaComponent(Color.WHITE, (int) (255 * 0.2f)), Color.WHITE, ColorUtils.setAlphaComponent(Color.WHITE, (int) (255 * 0.6f)));
        mSeekBar.setPointParams(CameraPercentUtil.WidthPxToPercent(10), Color.WHITE);
        mSeekBar.setProgressWidth(CameraPercentUtil.WidthPxToPercent(2));
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.WidthPxToPercent(100));
        params.gravity = Gravity.CENTER;
        mContentFrame.addView(mSeekBar, params);
    }

    public void setSurfaceView(View surfaceView)
    {
        mSurfaceView = surfaceView;
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (mContentFrame != null)
        {
            mSurfaceView.setLayoutParams(params);
            mContentFrame.addView(mSurfaceView, 0);
        }
    }

    public void setDuration(long duration)
    {
        if (mSeekBar != null)
        {
            mSeekBar.setDuration(duration);
        }
    }

    public void setPlayer(SimpleExoPlayer player)
    {
        if (mPlayer == player)
        {
            return;
        }
        mPlayer = player;
        if (mPlayer != null)
        {
            player.removeListener(this);
            player.removeVideoListener(this);
            if (mSurfaceView != null)
            {
                if (mSurfaceView instanceof TextureView)
                {
                    mPlayer.clearVideoTextureView((TextureView) mSurfaceView);
                }
                else if (mSurfaceView instanceof SurfaceView)
                {
                    mPlayer.clearVideoSurfaceView((SurfaceView) mSurfaceView);
                }
            }

            if (mSurfaceView instanceof TextureView)
            {
                player.setVideoTextureView((TextureView) mSurfaceView);
            }
            else if (mSurfaceView instanceof SurfaceView)
            {
                player.setVideoSurfaceView((SurfaceView) mSurfaceView);
            }
            player.addVideoListener(this);
            player.addListener(this);

            if (mSeekBar != null)
            {
                mSeekBar.setPlayer(player);
            }
        }
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio)
    {
        Log.d(TAG, "ExoVideoView --> onVideoSizeChanged: ");
        mContentFrame.setAspectRatio(width *1f/ height);
    }

    @Override
    public void onRenderedFirstFrame()
    {
        Log.d(TAG, "ExoVideoView --> onRenderedFirstFrame: ");
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest)
    {
        Log.d(TAG, "ExoVideoView --> onTimelineChanged: ");
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections)
    {
        Log.d(TAG, "ExoVideoView --> onTracksChanged: ");
    }

    @Override
    public void onLoadingChanged(boolean isLoading)
    {
        Log.d(TAG, "ExoVideoView --> onLoadingChanged: ");
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState)
    {
        Log.d(TAG, "ExoVideoView --> onPlayerStateChanged: ");
    }

    @Override
    public void onRepeatModeChanged(int repeatMode)
    {
        Log.d(TAG, "ExoVideoView --> onRepeatModeChanged: ");
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled)
    {
        Log.d(TAG, "ExoVideoView --> onShuffleModeEnabledChanged: ");
    }

    @Override
    public void onPlayerError(ExoPlaybackException error)
    {
        Log.d(TAG, "ExoVideoView --> onPlayerError: ");
    }

    @Override
    public void onPositionDiscontinuity(int reason)
    {
        Log.d(TAG, "ExoVideoView --> onPositionDiscontinuity: ");
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters)
    {
        Log.d(TAG, "ExoVideoView --> onPlaybackParametersChanged: ");
    }

    @Override
    public void onSeekProcessed()
    {
        Log.d(TAG, "ExoVideoView --> onSeekProcessed: ");
    }
}
