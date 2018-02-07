package xx.demo.exoPlayer;

import android.content.Context;
import android.support.annotation.NonNull;
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

/**
 * Created by admin on 2018/2/7.
 */

public class ExoVideoView extends FrameLayout implements SimpleExoPlayer.VideoListener
{
    private AspectRatioFrameLayout mContentFrame;
    private View mSurfaceView;
    private SimpleExoPlayer mPlayer;
    private Player.EventListener componentListener;

    public ExoVideoView(@NonNull Context context)
    {
        super(context);
        initCB();
        init(context);
    }

    private void initCB()
    {
        componentListener = new Player.EventListener()
        {

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest)
            {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections)
            {

            }

            @Override
            public void onLoadingChanged(boolean isLoading)
            {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState)
            {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode)
            {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled)
            {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error)
            {

            }

            @Override
            public void onPositionDiscontinuity(int reason)
            {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters)
            {

            }

            @Override
            public void onSeekProcessed()
            {

            }
        };
    }

    private void init(Context context)
    {
        mContentFrame = new AspectRatioFrameLayout(context);
        mContentFrame.setAspectRatio(1);
        mContentFrame.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(mContentFrame, params);
    }

    public void setSurfaceView(View surfaceView)
    {
        mSurfaceView = surfaceView;
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (mContentFrame != null)
        {
            mContentFrame.addView(mSurfaceView, params);
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
            player.removeListener(componentListener);
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
            player.addListener(componentListener);
        }
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio)
    {
        mContentFrame.setAspectRatio(width *1f/ height);
    }

    @Override
    public void onRenderedFirstFrame()
    {

    }
}
