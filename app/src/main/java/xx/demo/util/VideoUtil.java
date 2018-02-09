package xx.demo.util;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by admin on 2018/2/9.
 */

public class VideoUtil
{
    public static long GetVideoDuration(String path)
    {
        long out = 0;
        if (!TextUtils.isEmpty(path))
        {
            File file = new File(path);
            if (file.exists())
            {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(path);
                String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                out = Long.parseLong(duration);
            }
        }

        return out;
    }
}
