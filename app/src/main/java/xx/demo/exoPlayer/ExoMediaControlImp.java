package xx.demo.exoPlayer;

/**
 * Created by admin on 2018/2/10.
 */

public interface ExoMediaControlImp
{
    void resume();

    void pause();

    long getDuration();

    void seekTo(float percent);

    void seekTo(int window_index, long positionMs);

    int getBufferPercentage();

    boolean isPlaying();

    boolean canPause();
}
