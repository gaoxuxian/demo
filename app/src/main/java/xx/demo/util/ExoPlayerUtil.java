package xx.demo.util;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import xx.demo.exoPlayer.CacheDataSourceFactory;

/**
 * Created by Gxx on 2018/2/10.
 */

public class ExoPlayerUtil
{
    public static SimpleExoPlayer createSimplePlayer(Context context)
    {
        TrackSelector trackSelector = new DefaultTrackSelector();
        return ExoPlayerFactory.newSimpleInstance(context, trackSelector);
    }

    public static MediaSource[] createMediaSource(Context context, String...video_paths)
    {
        DataSource.Factory factory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getApplicationInfo().packageName));

        ExtractorMediaSource.Factory mediaSourceFactory = new ExtractorMediaSource.Factory(factory);
        mediaSourceFactory.setExtractorsFactory(new DefaultExtractorsFactory());

        int length = video_paths.length;
        MediaSource[] out = new MediaSource[length];
        for (int i = 0; i < length; i++)
        {
            out[i] = mediaSourceFactory.createMediaSource(Uri.parse(video_paths[i]));
        }

        return out;
    }

    /**
     * 带缓存的
     *
     * @param context
     * @param cache_dir 缓存目录
     * @param video_paths
     * @return
     */
    public static MediaSource[] createMediaSourceInCache(Context context, String cache_dir, String...video_paths)
    {
        CacheDataSourceFactory factory = new CacheDataSourceFactory(context, cache_dir, 100 * 1024 * 1024, 5 * 1024 * 1024);

        ExtractorMediaSource.Factory mediaSourceFactory = new ExtractorMediaSource.Factory(factory);
        mediaSourceFactory.setExtractorsFactory(new DefaultExtractorsFactory());

        int length = video_paths.length;
        MediaSource[] out = new MediaSource[length];
        for (int i = 0; i < length; i++)
        {
            out[i] = mediaSourceFactory.createMediaSource(Uri.parse(video_paths[i]));
        }

        return out;
    }
}
