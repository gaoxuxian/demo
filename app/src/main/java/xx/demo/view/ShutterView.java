package xx.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 快门
 * Created by Gxx on 2018/1/12.
 */

public class ShutterView extends BaseView<ShutterConfig>
{
    private Ring mRing;

    public ShutterView(Context context)
    {
        this(context, 0);
    }

    public ShutterView(Context context, int def_wh)
    {
        super(context, def_wh);
    }

    public Ring getRing()
    {
        return mRing != null ? mRing : new Ring();
    }

    public void setRing(Ring ring)
    {
        mRing = ring;
        invalidate();
    }

    @Override
    public void setConfig(ShutterConfig config)
    {
        mRing = config.getDef();
        startToDraw();
    }

    @Override
    protected void drawToCanvas(Canvas canvas)
    {
        if (mRing != null)
        {
            canvas.save();
            canvas.translate(mViewW / 2f, mViewH / 2f);
            mPaint.reset();
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mRing.mOutBotColor);
            canvas.drawRoundRect(mRing.getOutRoundRect(), mRing.getOutRx(), mRing.getOutRy(), mPaint);

            mPaint.setColor(mRing.mOutTopColor);
            canvas.drawRoundRect(mRing.getOutRoundRect(), mRing.getOutRx(), mRing.getOutRy(), mPaint);

            mPaint.setColor(mRing.mInnerBotColor);
            canvas.drawRoundRect(mRing.getInnerRoundRect(), mRing.getInnerRx(), mRing.getInnerRy(), mPaint);

            mPaint.setColor(mRing.mInnerTopColor);
            canvas.drawRoundRect(mRing.getInnerRoundRect(), mRing.getInnerRx(), mRing.getInnerRy(), mPaint);

            canvas.restore();
        }
    }
}
