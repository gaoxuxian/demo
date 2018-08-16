package util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

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

    /**
     * 获得最接近比例
     *
     * @param srcScale
     *            目标比例
     * @param dstScaleArr
     * @return
     */
    public static int GetScale(float srcScale, ArrayList<Float> dstScaleArr)
    {
        int index = -1;

        int len = dstScaleArr.size();
        if(len > 0)
        {
            float min = Math.abs(srcScale - dstScaleArr.get(0));
            index = 0;
            float temp;
            for(int i = 1; i < len; i++)
            {
                temp = Math.abs(srcScale - dstScaleArr.get(i));
                if(temp < min)
                {
                    min = temp;
                    index = i;
                }
            }
        }

        return index;
    }

    /**
     * 获取2点间的距离
     *
     * @param dx
     * @param dy
     * @return
     */
    public static float Spacing(float dx, float dy)
    {
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 生成圆角bmp
     *
     * @param bmp
     * @param px
     * @return
     */
    public static Bitmap MakeRoundBmp(Bitmap bmp, float px)
    {
        Bitmap out = bmp;

        if(bmp != null && px > 0)
        {
            out = MakeRoundBmp(bmp, bmp.getWidth(), bmp.getHeight(), px);
        }

        return out;
    }

    public static Bitmap MakeRoundBmp(Bitmap bmp, int w, int h, float px)
    {
        Bitmap out = null;

        if(bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0 && w > 0 && h > 0)
        {
            out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(out);
            Paint pt = new Paint();
            pt.setColor(0xFFFFFFFF);
            pt.setAntiAlias(true);
            pt.setFilterBitmap(true);
            pt.setStyle(Paint.Style.FILL);
            if(px > 0)
            {
                canvas.drawRoundRect(new RectF(0, 0, w, h), px, px, pt);
            }
            else
            {
                canvas.drawRect(0, 0, w, h, pt);
            }

            pt.reset();
            pt.setAntiAlias(true);
            pt.setFilterBitmap(true);
            pt.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            Matrix m = new Matrix();
            float s;
            {
                float s1 = (float)w / (float)bmp.getWidth();
                float s2 = (float)h / (float)bmp.getHeight();
                s = s1 > s2 ? s1 : s2;
            }
            m.postTranslate((w - bmp.getWidth()) / 2f, (h - bmp.getHeight()) / 2f);
            m.postScale(s, s, w / 2f, h / 2f);
            canvas.drawBitmap(bmp, m, pt);
        }

        return out;
    }

    public static Bitmap MakeResRoundBmp(Context context, int res, int w, int h, float px)
    {
        Bitmap out = null;

        if(context != null && w > 0 && h > 0)
        {
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), res);
            if(temp != null)
            {
                out = MakeRoundBmp(temp, w, h, px);
                if(temp != out)
                {
                    temp.recycle();
                    temp = null;
                }
            }
        }

        return out;
    }

    public static Bitmap MakeColorRoundBmp(int color, int w, int h, float px)
    {
        Bitmap out = null;

        if(w > 0 && h > 0)
        {
            out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(out);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Paint pt = new Paint();
            pt.setAntiAlias(true);
            pt.setFilterBitmap(true);
            pt.setColor(color);
            pt.setStyle(Paint.Style.FILL);
            if(px > 0)
            {
                canvas.drawRoundRect(new RectF(0, 0, w, h), px, px, pt);
            }
            else
            {
                canvas.drawRect(0, 0, w, h, pt);
            }
        }

        return out;
    }

    /**
     * 判断是不是图片文件
     *
     * @param path
     * @return
     */
    public static boolean IsImageFile(String path)
    {
        boolean out = false;

        if(new File(path).exists())
        {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            if(opts.outWidth > 0 && opts.outHeight > 0)
            {
                out = true;
            }
        }

        return out;
    }
}
