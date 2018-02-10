package xx.demo.exoPlayer;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;

/**
 * Created by Gxx on 2018/2/10.
 */

public abstract class ExoController implements ExoMediaControlImp
{
    @Override
    public void resume()
    {
        SimpleExoPlayer exoPlayer = getExoPlayer();
        if (exoPlayer != null)
        {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void pause()
    {
        SimpleExoPlayer exoPlayer = getExoPlayer();
        if (exoPlayer != null)
        {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void seekTo(float percent)
    {
        SimpleExoPlayer exoPlayer = getExoPlayer();
        if (exoPlayer != null)
        {
            Timeline currentTimeline = exoPlayer.getCurrentTimeline();
            int window_size = currentTimeline.getWindowCount();

            long duration = 0;
            long target = (long)(getDuration() * percent);

            Timeline.Window window = getExoWindow();

            if (window == null)
            {
                window = new Timeline.Window();
            }

            for (int i = 0; i < window_size; i++)
            {
                currentTimeline.getWindow(i, window);
                duration += C.usToMs(window.durationUs);
                if (duration >= target)
                {
                    duration -= C.usToMs(window.durationUs);
                    duration = target - duration;
                    exoPlayer.seekTo(i, duration);
                    break;
                }
            }
        }
    }

    @Override
    public void seekTo(int window_index, long positionMs)
    {
        SimpleExoPlayer exoPlayer = getExoPlayer();
        if (exoPlayer != null)
        {
            exoPlayer.seekTo(window_index, positionMs);
        }
    }

    @Override
    public int getBufferPercentage()
    {
        return 0;
    }

    @Override
    public boolean isPlaying()
    {
        SimpleExoPlayer exoPlayer = getExoPlayer();
        return exoPlayer != null && exoPlayer.getPlayWhenReady();
    }

    @Override
    public boolean canPause()
    {
        return true;
    }

    abstract SimpleExoPlayer getExoPlayer();

    abstract Timeline.Window getExoWindow();
}
