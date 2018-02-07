package xx.demo.view;

import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.graphics.ColorUtils;
import android.util.TypedValue;
import android.view.View;

import xx.demo.R;
import xx.demo.util.CameraPercentUtil;
import xx.demo.util.ShareData;

/**
 * Created by admin on 2018/1/23.
 */

public class ARWishViewV2 extends View
{
    private Bitmap mBitmap;

    private BitmapShader mBmpShader;

    private Paint mPaint;
    private float mRadius;

    private int mBigBmpWH;
    private int mSmallBmpWH;

    private float mCircleX, mCircleY;
    private Bitmap mCircleBmp;

    public ARWishViewV2(Context context)
    {
        super(context);

        mBigBmpWH = CameraPercentUtil.WidthPxToPercent(434);
        mSmallBmpWH = CameraPercentUtil.WidthPxToPercent(120);

        mCircleBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ar_circle);

        mBmpShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        matrix.reset();
        scale = CameraPercentUtil.WidthPxToPercent(118) * 1f / CameraPercentUtil.WidthPxToPercent(434);
        float w = bitmapW * scale;
        x = (ShareData.m_screenRealWidth * 1f - w) / 2f;
        y = CameraPercentUtil.WidthPxToPercent(192) - CameraPercentUtil.WidthPxToPercent(92 + 217) * scale;
        matrix.postScale(scale, scale);
        matrix.postTranslate(x, y);
        mBmpShader.setLocalMatrix(matrix);
        mPaint.setShader(mBmpShader);

        mRadius = CameraPercentUtil.WidthPxToPercent(217) * scale + CameraPercentUtil.WidthPxToPercent(3);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }



    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.save();
        canvas.drawColor(ColorUtils.setAlphaComponent(Color.BLACK, (int) (255 * 0.5f)));

        canvas.drawCircle(mCircleX, mCircleY, mRadius, mPaint);

        canvas.restore();
    }
}
