package camera;

import android.media.MediaMuxer;

import java.io.File;
import java.io.IOException;

import lib.util.FileUtil;

/**
 * Created by admin on 2018/2/26.
 */

public class MediaMuxerWrapper
{
    private MediaMuxer mMediaMuxer;

    private String mOutputPath;
    private boolean mIsStarted;

    private MediaVideoEncoder mVideoEncoder;

    public MediaMuxerWrapper() throws IOException
    {
        mOutputPath = getCaptureFile();
        mMediaMuxer = new MediaMuxer(mOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
    }

    private String getCaptureFile()
    {
        String out = null;

        File file = new File(FileUtil.getSDPath() + System.currentTimeMillis() + ".mp4");
        if (!file.exists())
        {
            out = file.getAbsolutePath();
        }

        return out;
    }

    public void prepare()
    {
        if (mVideoEncoder != null)
        {
            mVideoEncoder.prepare();
        }
    }

    public void startRecording()
    {
        if (mVideoEncoder != null)
        {
            mVideoEncoder.startRecording();
        }
    }

    public void stopRecording()
    {
        if (mVideoEncoder != null)
        {
            mVideoEncoder.stopRecording();
        }
    }

    public void addEncoder(MediaVideoEncoder videoEncoder)
    {
        mVideoEncoder = videoEncoder;
    }

    synchronized void stop()
    {
        mMediaMuxer.stop();
        mMediaMuxer.release();
        mIsStarted = false;
    }
}
