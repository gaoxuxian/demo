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

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;

import xx.demo.util.CameraPercentUtil;

/**
 * 基于 ExoPlayer 封装的 VideoView
 * Created by Gxx on 2018/2/7.
 */

public class ExoVideoView extends FrameLayout implements SimpleExoPlayer.VideoListener, Player.EventListener, BufferSeekBar.OnSeekBarChangeListener
{
    private AspectRatioFrameLayout mContentFrame;
    private View mSurfaceView;
    private BufferSeekBar mSeekBar;

    private SimpleExoPlayer mPlayer;
    private Timeline.Window mExoWindow;
    private ExoController mController;

    private long mDuration;
    private String TAG = "ExoVideoView";

    public ExoVideoView(@NonNull Context context)
    {
        super(context);

        init();
        initUI(context);
    }

    private void init()
    {
        mController = new ExoController()
        {
            @Override
            SimpleExoPlayer getExoPlayer()
            {
                return mPlayer;
            }

            @Override
            Timeline.Window getExoWindow()
            {
                return mExoWindow;
            }

            @Override
            public long getDuration()
            {
                return mDuration;
            }
        };
    }

    private void initUI(Context context)
    {
        mContentFrame = new AspectRatioFrameLayout(context);
        mContentFrame.setAspectRatio(1);
        mContentFrame.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(mContentFrame, params);

        mSeekBar = new BufferSeekBar(context);
        mSeekBar.setOnSeekBarChangeListener(this);
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
        mDuration = duration;

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
            if (mExoWindow == null)
            {
                mExoWindow = new Timeline.Window();
            }

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

    public void pause()
    {
        if (mController != null)
        {
            mController.pause();
        }
    }

    public void resume()
    {
        if (mController != null)
        {
            mController.resume();
        }
    }

    public void release()
    {
        if (mPlayer != null)
        {
            mPlayer.release();
        }
    }

    public void start()
    {
        resume();
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio)
    {
        Log.d(TAG, "ExoVideoView --> onVideoSizeChanged: ");
        mContentFrame.setAspectRatio(width * 1f / height);
        if (mDuration == 0)
        {
            Timeline currentTimeline = mPlayer.getCurrentTimeline();
            int window_size = currentTimeline.getWindowCount();
            long duration = 0;
            for (int i = 0; i < window_size; i++)
            {
                currentTimeline.getWindow(i, mExoWindow);
                duration += C.usToMs(mExoWindow.durationUs);
            }
            setDuration(duration);
        }
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
        Log.d(TAG, "ExoVideoView --> onLoadingChanged: isLoading == "+isLoading);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState)
    {
        Log.d(TAG, "ExoVideoView --> onPlayerStateChanged: ");
        switch (playbackState)
        {
            case Player.STATE_IDLE:
            {
                Log.d(TAG, "ExoVideoView --> onPlayerStateChanged: 空闲");
                break;
            }

            case Player.STATE_BUFFERING:
            {
                Log.d(TAG, "ExoVideoView --> onPlayerStateChanged: 正在缓冲");
                break;
            }

            case Player.STATE_READY:
            {
                Log.d(TAG, "ExoVideoView --> onPlayerStateChanged: 准备就绪");
                break;
            }

            case Player.STATE_ENDED:
            {
                Log.d(TAG, "ExoVideoView --> onPlayerStateChanged: 结束");
                break;
            }
        }
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

    @Override
    public void onStartTrackingTouch(float percent)
    {
        pause();
    }

    @Override
    public void onProgressChanged(float percent)
    {

    }

    @Override
    public void onStopTrackingTouch(float percent)
    {
        if (mController != null)
        {
            mController.seekTo(percent);
            mController.resume();
        }
    }
}
