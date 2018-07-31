package xx.demo.activity.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Raining on 2017/3/27.
 * 图片日期水印位置
 */

public class PhotoMark
{
	/**
	 * 获取图片的参照size
	 */
	private static int getReferenceSize(Bitmap bmp)
	{
		return Math.min(bmp.getWidth(), bmp.getHeight());
	}

	/**
	 * 获取水印的宽度
	 */
	public static float getLogoW(float s)
	{
		return s * 470f / 2048f;
	}

	/**
	 * 获取水印的底部间距
	 */
	public static float getLogoBottom(float s, boolean hasDate)
	{
		if(hasDate)
		{
			return getLogoRight(s) + getTextSize(s) + s * 26f / 2048f;
		}
		else
		{
			return getLogoRight(s);
		}
	}

	/**
	 * 获取水印的右边间距
	 */
	public static float getLogoRight(float s)
	{
		return s * 67f / 2048f;
	}

	/**
	 * 获取日期的文子size
	 */
	private static int getTextSize(float s)
	{
		int out = (int)(s * 50f / 2048f);
		if(out < 10)
		{
			out = 10;
		}
		out = ((out + 1) / 2) * 2;
		//System.out.println(out);
		return out;
	}

	public static void drawDate(Bitmap bmp)
	{
		if(bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0)
		{
			int rs = getReferenceSize(bmp);
			float ls = getLogoW(rs);
			float lr = getLogoRight(rs);
			float ts = getTextSize(rs);
			float cx = bmp.getWidth() - lr - 48 / 2048f * rs;
			float cy = bmp.getHeight() - lr - ts * 0.1f;

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			Paint paint = new Paint();
			paint.setTextSize(ts);
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			paint.setColor(0xFFFFFFFF);
			paint.setTextAlign(Paint.Align.RIGHT);
			paint.setShadowLayer(0.1f * ts, 0.1f * ts, 0.05f * ts, 0x30000000);

			Canvas canvas = new Canvas(bmp);
			/*Paint paint2 = new Paint();
			paint2.setColor(0xff0000ff);
			paint2.setStyle(Paint.Style.FILL);
			canvas.drawRect(bmp.getWidth() - lr - ls, bmp.getHeight() - lr - ts, bmp.getWidth() - lr, bmp.getHeight() - lr, paint2);*/
			canvas.drawText(dateFormat.format(new Date()), cx, cy, paint);
		}
	}

	public static void drawDataLeft(Bitmap bmp)
	{
		if(bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0)
		{
			int rs = getReferenceSize(bmp);
			float ls = getLogoW(rs);
			float lr = getLogoRight(rs);
			float ts = getTextSize(rs);
			float cx = lr + 48 / 2048f * rs;
			float cy = bmp.getHeight() - lr - ts * 0.1f;

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			Paint paint = new Paint();
			paint.setTextSize(ts);
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			paint.setColor(0xFFFFFFFF);
			paint.setTextAlign(Paint.Align.LEFT);
			paint.setShadowLayer(0.1f * ts, 0.1f * ts, 0.05f * ts, 0x30000000);

			Canvas canvas = new Canvas(bmp);
			canvas.drawText(dateFormat.format(new Date()), cx, cy, paint);
		}
	}

	public static Bitmap drawWaterMark(Bitmap bmp, Bitmap markBmp, boolean hasDate)
	{
        return drawWaterMark(bmp,markBmp,hasDate,true);
	}

	/**
	 *
	 * @param bmp 需要加上水印的bitmap
	 * @param markBmp 水印 bitmap
	 * @param hasDate
	 * @param isLeft  true,水印在左边; false,水印在右边
	 * @return
	 */
	public static Bitmap drawWaterMark(Bitmap bmp, Bitmap markBmp, boolean hasDate, boolean isLeft){

		if(bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0 && markBmp !=  null)
		{
			int rs = getReferenceSize(bmp);
			//获取水印的宽度
			float waterMarkWidth = getLogoW(rs);

			//获取水印的水平方向间距
			float waterMarkHorizontalMargin = getLogoRight(rs);
			//获取水印的底部间距
			float waterMarkBottomMargin = getLogoBottom(rs, hasDate);

			//水印位置中心点的x坐标
			float waterMarkCenterX = 0f;

			if (isLeft){
				waterMarkCenterX = waterMarkHorizontalMargin + waterMarkWidth / 2f;
			} else {
				 waterMarkCenterX = bmp.getWidth() - waterMarkHorizontalMargin - waterMarkWidth / 2f;

			}
			//水印的底边位置
			float waterMarkBottomY = bmp.getHeight() - waterMarkBottomMargin;

			float sx = waterMarkWidth / markBmp.getWidth();

			float sy = waterMarkWidth / markBmp.getHeight();

			float minScale = sx > sy ? sy : sx;

			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);

			Matrix matrix = new Matrix();

			matrix.postTranslate(waterMarkCenterX - markBmp.getWidth() / 2f, waterMarkBottomY - markBmp.getHeight());
			matrix.postScale(minScale, minScale, waterMarkCenterX, waterMarkBottomY);

			Canvas canvas = new Canvas(bmp);
			canvas.drawBitmap(markBmp, matrix, paint);

			return bmp;
		}
		return null;

	}

	/**
	 * 获取水印的边距
	 * @param rectF
	 * @return
	 */
	public static int getWaterMarkMargin(RectF rectF){

		float margin = 0;
		if(!rectF.isEmpty() ){

			float rs = getMinimum(rectF);
			margin = getLogoRight(rs);

		}
		return (int)margin;
	}

	private static float getMinimum(RectF rectF){

		return rectF.width() > rectF.height()? rectF.height():rectF.width();
	}

	/**
	 * 获取已经缩放完毕的水印bitmap
	 *
	 * @param waterMkBmp
	 * @return
	 */
	public static Bitmap getScaleBitmap(RectF rectF, Bitmap waterMkBmp){

		Bitmap scaleBmp = null;
		if(!rectF.isEmpty() && waterMkBmp != null){

			int rs = (int) getMinimum(rectF);
			float waterMarkWidth = 0;
			waterMarkWidth = getLogoW(rs);
			float sx = waterMarkWidth / waterMkBmp.getWidth();

			float sy = waterMarkWidth / waterMkBmp.getHeight();
			float minScale = sx > sy ? sy:sx;
			Matrix matrix = new Matrix();
			matrix.postScale(minScale,minScale);
			scaleBmp = Bitmap.createBitmap(waterMkBmp,0,0,waterMkBmp.getWidth(),waterMkBmp.getHeight(),matrix,true);

		}
		return scaleBmp;
	}

	public static void drawWaterMarkLeft(Bitmap bmp, Bitmap markBmp, boolean hasDate)
	{
		if(bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0 && markBmp !=  null)
		{
			int rs = getReferenceSize(bmp);
			float ls = getLogoW(rs);
			float lr = getLogoRight(rs);
			float lb = getLogoBottom(rs, hasDate);
			float cx = lr + ls / 2f;
			float by = bmp.getHeight() - lb;
			float sx = ls / markBmp.getWidth();
			float sy = ls / markBmp.getHeight();
			float s = sx > sy ? sy : sx;

			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);

			Matrix matrix = new Matrix();
			matrix.postTranslate(cx - markBmp.getWidth() / 2f, by - markBmp.getHeight());
			matrix.postScale(s, s, cx, by);

			Canvas canvas = new Canvas(bmp);
			canvas.drawBitmap(markBmp, matrix, paint);
		}
	}

    public static Bitmap drawWaterMarkLeftTop(Bitmap bmp, Bitmap markBmp) {
        if (bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0 && markBmp != null) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);

            Matrix matrix = getDrawWaterMarkLeftTopMatrix(bmp.getWidth(), bmp.getHeight(), markBmp);
            Canvas canvas = new Canvas(bmp);
            canvas.drawBitmap(markBmp, matrix, paint);
        }
        return bmp;
    }

    public static Matrix getDrawWaterMarkLeftTopMatrix(int w, int h, Bitmap markBmp) {

        Matrix matrix = new Matrix();

        if (w > 0 && h > 0 && markBmp != null) {
            int rs = Math.min(w, h);
            float ls = getLogoW(rs);
            float lr = getLogoRight(rs);
            float lb = getLogoBottom(rs, false);
            float cx = lr + ls / 2f;
            float sx = ls / markBmp.getWidth();
            float sy = ls / markBmp.getHeight();
            float s = sx > sy ? sy : sx;

            matrix.postTranslate(cx - markBmp.getWidth() / 2f, lb);
            matrix.postScale(s, s, cx, lb);
        }
        return matrix;
    }
}
