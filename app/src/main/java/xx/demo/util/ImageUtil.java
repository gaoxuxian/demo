package xx.demo.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by admin on 2018/2/23.
 */

public class ImageUtil
{
    /**
     * 旋转、裁剪图片
     *
     * @param data
     * @param hMirror 水平镜像
     * @param degree
     * @param ratio
     * @param maxSize 最大不能超过maxSize
     * @return
     */
    public static Bitmap rotateAndCropPicture(byte[] data, boolean hMirror, int degree, float ratio, int maxSize)
    {
        float maxMem = 0.25f;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeByteArray(data, 0, data.length, opt);
        opt.inJustDecodeBounds = false;

        int bigOne = opt.outWidth > opt.outHeight ? opt.outWidth : opt.outHeight;

        float srcRatio = opt.outHeight * 1.0f / opt.outWidth;
        if(srcRatio < 1)
        {
            srcRatio = 1 / srcRatio;
        }
        if(ratio > srcRatio)
        {
            bigOne = opt.outWidth > opt.outHeight ? opt.outWidth : opt.outHeight;

            int sampleSize = bigOne / maxSize;
            if(sampleSize <= 0)
            {
                sampleSize = 1;
            }
            int cw = opt.outWidth / sampleSize;
            int ch = opt.outHeight / sampleSize;
            int memUse = cw * ch * 4;
            if(memUse > Runtime.getRuntime().maxMemory() * maxMem)
            {
                bigOne = opt.outWidth > opt.outHeight ? opt.outWidth : opt.outHeight;
            }
        }
        if(bigOne > maxSize)
        {
            opt.inSampleSize = bigOne / maxSize;
        }
        if(opt.inSampleSize < 1)
        {
            opt.inSampleSize = 1;
        }

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
        if(bitmap == null || bitmap.isRecycled())
        {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        if(hMirror)
        {
            matrix.postScale(-1, 1);
        }
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if(bitmap == null || bitmap.isRecycled())
        {
            return null;
        }

        return bitmap;
    }

    public static String getOutPutDirectoryPath()
    {
        String path = FileUtil.getSDPath() + "demo_test_img" + File.separator;
        File file = new File(path);
        if (!file.exists())
        {
            file.mkdirs();
        }

        return path;
    }

    public static boolean saveImage(Context context, Bitmap img, String directory_path, boolean scan_to_album)
    {
        if (img == null || img.isRecycled() || TextUtils.isEmpty(directory_path)) return false;

        String pic_name = "demo_camera_test_" + System.currentTimeMillis() + ".jpg";

        File file = new File(directory_path + pic_name);
        if (file.exists())
        {
            FileUtil.deleteFile(file, false);
        }
        FileOutputStream fileOutputStream = null;
        try
        {
            fileOutputStream = new FileOutputStream(file);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }

        boolean succeed = img.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

        if (succeed && scan_to_album)
        {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        }

        return succeed;
    }
}
