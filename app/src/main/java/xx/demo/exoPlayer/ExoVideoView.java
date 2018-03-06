package xx.demo.exoPlayer;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;

import xx.demo.util.PixelPercentUtil;

/**
 * 基于 ExoPlayer 封装的 VideoView
 * Created by Gxx on 2018/2/7.
 */

public class ExoVideoView extends FrameLayout implements SimpleExoPlayer.VideoListener, BufferSeekBar.OnSeekBarChangeListener
{
    private AspectRatioFrameLayout mContentFrame;
    private View mSurfaceView;
    private BufferSeekBar mSeekBar;
    private WaitProgressView mWaitProgress;

    private SimpleExoPlayer mPlayer;
    private Timeline.Window mExoWindow;
    private ExoController mController;

    private long mDuration;
    private String TAG = "ExoVideoView";
    private Player.DefaultEventListener mCompatListener;

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

        mCompatListener = new Player.DefaultEventListener()
        {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest)
            {
                Log.d(TAG, "ExoVideoView --> onTimelineChanged: ");
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState)
            {
                checkPlayerState(playbackState);
            }

            @Override
            public void onPlayerError(ExoPlaybackException error)
            {
                Log.d(TAG, "ExoVideoView --> onPlayerError: ");
            }
        };
    }

    private void checkPlayerState(int playbackState)
    {
        switch (playbackState)
        {
            case Player.STATE_IDLE:
            {
                if (mWaitProgress != null)
                {
                    mWaitProgress.show(false);
                }
                Log.d(TAG, "ExoVideoView --> onPlayerStateChanged: 空闲");
                break;
            }

            case Player.STATE_BUFFERING:
            {
                if (mWaitProgress != null)
                {
                    mWaitProgress.show(true);
                }
                Log.d(TAG, "ExoVideoView --> onPlayerStateChanged: 正在缓冲");
                break;
            }

            case Player.STATE_READY:
            {
                if (mWaitProgress != null)
                {
                    mWaitProgress.show(false);
                }
                Log.d(TAG, "ExoVideoView --> onPlayerStateChanged: 准备就绪");
                break;
            }

            default:
            {
                Log.d(TAG, "ExoVideoView --> onPlayerStateChanged: 结束");
                break;
            }
        }
    }

    private void initUI(Context context)
    {
        mContentFrame = new AspectRatioFrameLayout(context);
        mContentFrame.setAspectRatio(1);
        mContentFrame.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(mContentFrame, params);
        {
            mSeekBar = new BufferSeekBar(context);
            mSeekBar.setOnSeekBarChangeListener(this);
            mSeekBar.setColor(ColorUtils.setAlphaComponent(Color.WHITE, (int) (255 * 0.2f)), Color.WHITE, ColorUtils.setAlphaComponent(Color.WHITE, (int) (255 * 0.6f)));
            mSeekBar.setPointParams(PixelPercentUtil.WidthPxToPercent(10), Color.WHITE);
            mSeekBar.setProgressWidth(PixelPercentUtil.WidthPxToPercent(2));
            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelPercentUtil.WidthPxToPercent(100));
            params.gravity = Gravity.CENTER;
            mContentFrame.addView(mSeekBar, params);

            mWaitProgress = new WaitProgressView(context);
            mWaitProgress.setPeriodDuration(1000);
            mWaitProgress.setProgressColor(ColorUtils.setAlphaComponent(Color.RED, (int) (255 * 0.5f)));
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            mContentFrame.addView(mWaitProgress, params);
        }

//        SpannableStringBuilder sbb = new SpannableStringBuilder(mTextProgress.getText());
//        buildWavingSpans(sbb,mTextProgress);
//        mTextProgress.setText(sbb);
//        mTextProgress.show(true);
    }

    private void buildWavingSpans(SpannableStringBuilder sbb, TextView tv)
    {
        int loopDuration = 1500;
        int startPos = 0;//textview字体的开始位置
        int endPos = tv.getText().length();//结束位置
        int waveCharDelay = loopDuration / (4 * (endPos - startPos));//每个字体延迟的时间

        for (int pos = startPos; pos < endPos; pos++)
        {
            //设置每个字体的jumpingspan
            JumpingSpan jumpingBean = new JumpingSpan(tv, loopDuration, pos - startPos, waveCharDelay, 0.65f);
            sbb.setSpan(jumpingBean, pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
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

            player.removeListener(mCompatListener);
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
            player.addListener(mCompatListener);

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
    public void onRenderedFirstFrame() {}

    @Override
    public void onStartTrackingTouch(float percent)
    {
        pause();
    }

    @Override
    public void onProgressChanged(float percent) {}

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
