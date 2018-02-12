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
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheEvictor;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

//import xx.demo.exoPlayer.CacheDataSourceFactory;

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
        CacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024);
        SimpleCache cache = new SimpleCache(new File(cache_dir), evictor);
        CacheDataSourceFactory factory = new CacheDataSourceFactory(cache,
                new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getApplicationInfo().packageName)),
                CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

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
