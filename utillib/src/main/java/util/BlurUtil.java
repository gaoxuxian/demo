package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.Short4;
import android.support.v8.renderscript.Type;
import android.text.TextUtils;

import xx.demo.rs.ScriptC_tint;

/**
 * @author Gxx
 * Created by Gxx on 2018/9/14.
 * <p>
 * 使用前，要确保添加了以下参数
 * <p>
 * build.gradle
 * android {
 *      ...
 *      ...
 *      defaultConfig {
 *          renderscriptTargetApi 18
 *          renderscriptSupportModeEnabled true
 *      }
 * }
 */
public class BlurUtil
{
    /**
     * 毛玻璃图片尺寸可订制
     *
     * @param context    上下文
     * @param src        原图
     * @param blurRadius 虚化半径
     * @param maskColor  虚化后颜色蒙版, null or "" 相当于不叠加
     * @param maxBorder  毛玻璃图片最大边长
     * @return 毛玻璃图片
     */
    public static Bitmap sBlurEffectOrder(Context context, Bitmap src,
                                          @FloatRange(from = 0, fromInclusive = false, to = 25) float blurRadius,
                                          @Nullable String maskColor,
                                          @IntRange(from = 100) int maxBorder)
    {
        if (src == null || src.isRecycled() || blurRadius <= 0)
        {
            return src;
        }

        if (blurRadius > 25)
        {
            blurRadius = 25; // google 限定的虚化半径[0 < x <= 25], 如果要更虚化的效果，可以将图片压缩更小
        }

        int srcImgWidth = src.getWidth();
        int srcImgHeight = src.getHeight();

        Bitmap dst = src;
        int max = Math.max(srcImgWidth, srcImgHeight);
        float scale = 1f;
        int dstImgWidth = (int) (srcImgWidth * scale);
        int dstImgHeight = (int) (srcImgHeight * scale);

        if (max > maxBorder)
        {
            scale = (float) maxBorder / (float) max;
            dstImgWidth = (int) (srcImgWidth * scale);
            dstImgHeight = (int) (srcImgHeight * scale);
            dst = Bitmap.createBitmap(dstImgWidth, dstImgHeight, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(dst);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            canvas.drawBitmap(src, matrix, paint);
        }

        RenderScript rs = RenderScript.create(context);
        Allocation in = Allocation.createFromBitmap(rs, dst);
        Type type = Type.createXY(rs, in.getElement(), dstImgWidth, dstImgHeight);
        Allocation temp = Allocation.createTyped(rs, type);

        // 虚化
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, in.getElement());
        blur.setInput(in);
        blur.setRadius(blurRadius);
        blur.forEach(temp);

        if (!TextUtils.isEmpty(maskColor))
        {
            // 叠加颜色蒙版
            ScriptC_tint tint = new ScriptC_tint(rs);
            tint.set_maskColor(convertColor2Short4(maskColor));
            tint.forEach_mask(temp, temp);
            tint.destroy();
        }

        temp.copyTo(dst);

        blur.destroy();
        in.destroy();
        temp.destroy();

        return dst;
    }


    /**
     * 毛玻璃图片尺寸可订制
     *
     * @param context    上下文
     * @param src        原图
     * @param blurRadius 虚化半径
     * @param maskColor  虚化后颜色蒙版, null or "" 相当于不叠加
     * @param scale      压缩比例
     * @return 毛玻璃图片
     */
    public static Bitmap sBlurEffectOrder(Context context, Bitmap src,
                                          @FloatRange(from = 0, fromInclusive = false, to = 25) float blurRadius,
                                          @Nullable String maskColor,
                                          @FloatRange(from = 0, fromInclusive = false, to = 1) float scale)
    {
        if (src == null || src.isRecycled() || blurRadius <= 0)
        {
            return src;
        }

        if (blurRadius > 25)
        {
            blurRadius = 25; // google 限定的虚化半径[0 < x <= 25], 如果要更虚化的效果，可以将图片压缩更小
        }

        int srcImgWidth = src.getWidth();
        int srcImgHeight = src.getHeight();

        int dstImgWidth = (int) (srcImgWidth * scale);
        int dstImgHeight = (int) (srcImgHeight * scale);

        Bitmap dst = src;
        if (scale != 1)
        {
            dst = Bitmap.createBitmap(dstImgWidth, dstImgHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(dst);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            canvas.drawBitmap(src, matrix, paint);
        }

        RenderScript rs = RenderScript.create(context);
        Allocation in = Allocation.createFromBitmap(rs, dst);
        Type type = Type.createXY(rs, in.getElement(), dstImgWidth, dstImgHeight);
        Allocation temp = Allocation.createTyped(rs, type);

        // 虚化
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, in.getElement());
        blur.setInput(in);
        blur.setRadius(blurRadius);
        blur.forEach(temp);

        if (!TextUtils.isEmpty(maskColor))
        {
            // 叠加颜色蒙版
            ScriptC_tint tint = new ScriptC_tint(rs);
            tint.set_maskColor(convertColor2Short4(maskColor));
            tint.forEach_mask(temp, temp);
            tint.destroy();
        }

        temp.copyTo(dst);

        blur.destroy();
        in.destroy();
        temp.destroy();

        return dst;
    }


    /**
     * 在原图上扣一个圆, 做毛玻璃
     * @param context 上下文
     * @param src 原图
     * @param blurRadius 虚化半径
     * @param maskColor 虚化后颜色蒙版, null or "" 相当于不叠加
     * @param scale 压缩比例
     * @param centerPercent 圆中心，百分比
     * @param circleRadius 圆半径，具体数值
     * @return 毛玻璃圆图
     */
    public static Bitmap sBlurEffectCirclePart(Context context, Bitmap src,
                                           @FloatRange(from = 0, fromInclusive = false, to = 25) float blurRadius,
                                           @Nullable String maskColor,
                                           @FloatRange(from = 0, fromInclusive = false, to = 1) float scale,
                                           @NonNull PointF centerPercent,
                                           @FloatRange(from = 0, fromInclusive = false) float circleRadius)
    {
        if (src == null || src.isRecycled() || blurRadius <= 0)
        {
            return src;
        }

        if (blurRadius > 25)
        {
            blurRadius = 25; // google 限定的虚化半径[0 < x <= 25], 如果要更虚化的效果，可以将图片压缩更小
        }

        int srcImgWidth = src.getWidth();
        int srcImgHeight = src.getHeight();

        int tempWidth = (int) (srcImgWidth * scale);
        int tempHeight = (int) (srcImgHeight * scale);
        Bitmap tempImg = Bitmap.createBitmap(tempWidth, tempHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tempImg);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        canvas.drawBitmap(src, matrix, paint);
        canvas.setBitmap(null);

        RenderScript rs = RenderScript.create(context);

        Allocation in = Allocation.createFromBitmap(rs, tempImg);

        Allocation temp = Allocation.createTyped(rs, in.getType());

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, in.getElement());
        blur.setInput(in);
        blur.setRadius(blurRadius);
        blur.forEach(temp);

        if (!TextUtils.isEmpty(maskColor))
        {
            // 叠加颜色蒙版
            ScriptC_tint tint = new ScriptC_tint(rs);
            tint.set_maskColor(convertColor2Short4(maskColor));
            tint.forEach_mask(temp, temp);
            tint.destroy();
        }

        temp.copyTo(tempImg);

        blur.destroy();
        in.destroy();
        temp.destroy();

        int circleImgWH = (int) (circleRadius * 2);
        Bitmap circleImg = Bitmap.createBitmap(circleImgWH, circleImgWH, Bitmap.Config.ARGB_8888);

        canvas.setBitmap(circleImg);
        int i = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        paint.reset();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        BitmapShader shader = new BitmapShader(tempImg, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        matrix.reset();
        float x = srcImgWidth * centerPercent.x - circleRadius;
        float y = srcImgHeight * centerPercent.y - circleRadius;
        matrix.postScale(1/scale, 1/scale);
        matrix.postTranslate(-x, -y);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        canvas.drawCircle(circleImgWH / 2f, circleImgWH / 2f, circleRadius, paint);
        canvas.restoreToCount(i);
        canvas.setBitmap(null);

        return circleImg;
    }


    /**
     * 在原图上扣一个矩形范围, 做毛玻璃
     * @param context 上下文
     * @param src 原图
     * @param blurRadius 虚化半径
     * @param maskColor 虚化后颜色蒙版, null or "" 相当于不叠加
     * @param scale 压缩比例
     * @param area 矩形范围，百分比
     * @return 毛玻璃图
     */
    public static Bitmap sBlurEffectRectFPart(Context context, Bitmap src,
                                              @FloatRange(from = 0, fromInclusive = false, to = 25) float blurRadius,
                                              @Nullable String maskColor,
                                              @FloatRange(from = 0, fromInclusive = false, to = 1) float scale,
                                              @NonNull RectF area)
    {
        if (src == null || src.isRecycled() || blurRadius <= 0)
        {
            return src;
        }

        if (blurRadius > 25)
        {
            blurRadius = 25; // google 限定的虚化半径[0 < x <= 25], 如果要更虚化的效果，可以将图片压缩更小
        }

        int srcImgWidth = src.getWidth();
        int srcImgHeight = src.getHeight();

        int tempWidth = (int) (srcImgWidth * scale);
        int tempHeight = (int) (srcImgHeight * scale);
        Bitmap tempImg = Bitmap.createBitmap(tempWidth, tempHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tempImg);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        canvas.drawBitmap(src, matrix, paint);
        canvas.setBitmap(null);

        RenderScript rs = RenderScript.create(context);

        Allocation in = Allocation.createFromBitmap(rs, tempImg);

        Allocation temp = Allocation.createTyped(rs, in.getType());

        // 虚化
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, in.getElement());
        blur.setInput(in);
        blur.setRadius(blurRadius);
        blur.forEach(temp);

        if (!TextUtils.isEmpty(maskColor))
        {
            // 叠加颜色蒙版
            ScriptC_tint tint = new ScriptC_tint(rs);
            tint.set_maskColor(convertColor2Short4(maskColor));
            tint.forEach_mask(temp, temp);
            tint.destroy();
        }

        temp.copyTo(tempImg);

        blur.destroy();
        in.destroy();
        temp.destroy();

        int outImgW = (int) (srcImgWidth * area.width());
        int outImgH = (int) (srcImgHeight * area.height());
        Bitmap out = Bitmap.createBitmap(outImgW, outImgH, Bitmap.Config.ARGB_8888);

        canvas.setBitmap(out);
        paint.reset();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        BitmapShader shader = new BitmapShader(tempImg, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        matrix.reset();
        float x = srcImgWidth * area.left;
        float y = srcImgHeight * area.top;
        matrix.postScale(1/scale, 1/scale);
        matrix.postTranslate(-x, -y);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        canvas.drawRect(0, 0, outImgW, outImgH, paint);

        return out;
    }

    /**
     * 将颜色转换成 RenderScript Short4 格式
     *
     * @param color 颜色格式，例如 "#FFFFFFFF"
     * @return Short4 格式
     */
    private static Short4 convertColor2Short4(String color)
    {
        int c = Color.parseColor(color);
        short b = (short) (c & 0xFF);
        short g = (short) ((c >> 8) & 0xFF);
        short r = (short) ((c >> 16) & 0xFF);
        short a = (short) ((c >> 24) & 0xFF);
        return new Short4(r, g, b, a);
    }
}
