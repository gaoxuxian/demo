package lib.exoplayer;

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
