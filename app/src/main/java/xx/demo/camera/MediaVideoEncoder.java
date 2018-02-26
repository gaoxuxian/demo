package xx.demo.camera;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * Created by admin on 2018/2/26.
 */

public class MediaVideoEncoder implements Runnable
{
    public final Object mSync = new Object();

    private static final String MIME_TYPE = "video/avc";
    private static final int FRAME_RATE = 30;
    private static final int I_FRAME_INTERVAL = 10;
    private static final float BPP = 0.25f;
    private final MediaCodec.BufferInfo mBufferInfo;

    private int mVideoWidth;
    private int mVideoHeight;

    private MediaCodec mEncoder;
    private WeakReference<MediaMuxerWrapper> mWeakMuxer;
    private Surface mSurface;

    private volatile boolean mRequestStop;
    private volatile boolean mIsCapturing;
    private volatile boolean mMuxerStarted;
    private int mRequestDrain;

    private static int[] recognizedFormats;

    static
    {
        recognizedFormats = new int[]{
//        	MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar,
//        	MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar,
//        	MediaCodecInfo.CodecCapabilities.COLOR_QCOM_FormatYUV420SemiPlanar,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface,
        };
    }

    private String TAG = "xxx";

    public MediaVideoEncoder(MediaMuxerWrapper muxer, int width, int height)
    {
        mVideoWidth = width;
        mVideoHeight = height;

        mWeakMuxer = new WeakReference<>(muxer);
        muxer.addEncoder(this);
        synchronized (mSync)
        {
            // create BufferInfo here for effectiveness(to reduce GC)
            mBufferInfo = new MediaCodec.BufferInfo();
            new Thread(this, getClass().getSimpleName()).start();

            try
            {
                mSync.wait();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private int calcBitRate()
    {
        final int bitrate = (int) (BPP * FRAME_RATE * mVideoWidth * mVideoHeight);
        Log.i(TAG, String.format("bitrate=%5.2f[Mbps]", bitrate / 1024f / 1024f));
        return bitrate;
    }

    public void prepare()
    {
        MediaCodecInfo mediaCodecInfo = selectVideoCodec(MIME_TYPE);
        if (mediaCodecInfo == null)
        {
            Log.d(TAG, "MediaVideoEncoder --> prepare: mediaCodecInfo == null");
            return;
        }

        MediaFormat videoFormat = MediaFormat.createVideoFormat(MIME_TYPE, mVideoWidth, mVideoHeight);
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface); // 颜色格式
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, calcBitRate()); // 码率
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE); // 帧率
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL);

        try
        {
            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
            mEncoder.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mSurface = mEncoder.createInputSurface();
            mEncoder.start();
            Log.i(TAG, "video encoder prepare finishing");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private MediaCodecInfo selectVideoCodec(String mimeType)
    {
        // 找到设备上已经注册的 编码器
        int codecCount = MediaCodecList.getCodecCount();
        for (int i = 0; i < codecCount; i++)
        {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder())
            {
                continue;
            }

            String[] supportedTypes = codecInfo.getSupportedTypes();
            for (String type : supportedTypes)
            {
                if (type.equals(mimeType))
                {
                    if (selectColorFormat(codecInfo, mimeType) > 0)
                    {
                        return codecInfo;
                    }
                }
            }
        }
        return null;
    }

    private int selectColorFormat(MediaCodecInfo mediaCodecInfo, String mimeType)
    {
        int result = 0;

        MediaCodecInfo.CodecCapabilities cap = mediaCodecInfo.getCapabilitiesForType(mimeType);
        for (int colorFormat : cap.colorFormats)
        {
            if (isRecognizedViewFormat(colorFormat))
            {
                result = colorFormat;
                break;
            }
        }

        return result;
    }

    private boolean isRecognizedViewFormat(final int colorFormat)
    {
        final int n = recognizedFormats != null ? recognizedFormats.length : 0;
        for (int i = 0; i < n; i++)
        {
            if (recognizedFormats[i] == colorFormat)
            {
                return true;
            }
        }
        return false;
    }

    public void startRecording()
    {
        synchronized (mSync)
        {
            mIsCapturing = true;
            mRequestStop = false;
            mSync.notifyAll();
        }
    }

    public void stopRecording()
    {
        synchronized (mSync)
        {
            if (!mIsCapturing || mRequestStop)
            {
                return;
            }

            mRequestStop = true;
            mSync.notifyAll();
        }
    }

    public void release()
    {
        if (mEncoder != null)
        {
            try
            {
                mEncoder.stop();
                mEncoder.release();
                mEncoder = null;
            }
            catch (Exception e)
            {
                Log.e(TAG, "failed releasing MediaCodec", e);
            }
        }

        if (mMuxerStarted)
        {
            final MediaMuxerWrapper muxer = mWeakMuxer != null ? mWeakMuxer.get() : null;
            if (muxer != null)
            {
                try
                {
                    muxer.stop();
                }
                catch (final Exception e)
                {
                    Log.e(TAG, "failed stopping muxer", e);
                }
            }
        }
        mIsCapturing = false;
    }

    private void drain()
    {
        if (mEncoder == null) return;

        ByteBuffer[] outputBuffers = mEncoder.getOutputBuffers();
        int encoderStatus, count = 0;
        final MediaMuxerWrapper muxer = mWeakMuxer.get();
        if (muxer == null)
        {
            Log.w(TAG, "muxer is unexpectedly null");
            return;
        }

LOOP:   while (mIsCapturing)
        {

        }
    }

    @Override
    public void run()
    {
        synchronized (mSync)
        {
            mRequestStop = false;
            mRequestDrain = 0;
            mSync.notify();
        }

        boolean localRequestStop;
        boolean localRequestDrain;

        while (true)
        {
            synchronized (mSync)
            {
                localRequestStop = mRequestStop;
                localRequestDrain = (mRequestDrain > 0);
                if (localRequestDrain)
                {
                    mRequestDrain--;
                }
            }

            if (localRequestStop)
            {
                // 停止录制



                release();
            }
            else if (localRequestDrain)
            {
                // 录制过程, 有数据
            }
            else
            {
                // 录制过程，没数据 or 等待录制
                synchronized (mSync)
                {
                    try
                    {
                        mSync.wait();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            synchronized (mSync)
            {
                mRequestStop = true;
                mIsCapturing = false;
            }
        }
    }
}
