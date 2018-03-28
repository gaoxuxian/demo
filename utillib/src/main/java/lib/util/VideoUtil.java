package lib.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import java.io.File;

/**
 * 本地视频 工具类
 * Created by Gxx on 2018/2/9.
 */

public class VideoUtil
{
    /**
     * 获取本地视频的时长
     * @param path
     * @return
     */
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

                mediaMetadataRetriever.release();
            }
        }

        return out;
    }

    /**
     * 获取本地视频的第一帧
     *
     * @param filePath
     * @return
     */
    public static Bitmap getLocalVideoThumbnail(String filePath, boolean ARGB_8888)
    {
        if (TextUtils.isEmpty(filePath)) return null;
        Bitmap bitmap = null;
        //MediaMetadataRetriever 是android中定义好的一个类，提供了统一
        //的接口，用于从输入的媒体文件中取得帧和元数据；
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try
        {
            //根据文件路径获取缩略图
            retriever.setDataSource(filePath);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            retriever.release();
            retriever = null;
        }
        if (ARGB_8888 && bitmap != null && !bitmap.isRecycled())
        {
            if (bitmap.getConfig() != Bitmap.Config.ARGB_8888)
            {
                Bitmap temp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                bitmap.recycle();
                bitmap = temp;
                temp = null;
            }
        }
        return bitmap;
    }

    public static Bitmap getLocalVideoThumbnail(String filePath)
    {
        return getLocalVideoThumbnail(filePath, false);
    }
}
