package xx.demo.view;

import android.graphics.RectF;

/**
 *
 * Created by Gxx on 2018/1/12.
 */

public class Ring
{
    public int mOutTopColor;
    public int mOutBotColor;

    private RectF mOutRoundRect;

    private float mOutRx, mOutRy;
    private float mInnerRx, mInnerRy;

    private RectF mInnerRoundRect;

    private int mOutColor;

    private int mInnerColor;

    public int mInnerTopColor;
    public int mInnerBotColor;

    private String mMidText;

    public Ring()
    {
        mOutRoundRect = new RectF();
        mInnerRoundRect = new RectF();
    }

    // ====================================== Set =================================== //

    public void setOutRadius(int radius)
    {
        this.mOutRoundRect.set(-radius, -radius, radius, radius);
        mOutRx = mOutRy = radius;
    }

    public void setInnerRadius(int radius)
    {
        this.mInnerRoundRect.set(-radius, -radius, radius, radius);
        mInnerRx = mInnerRy = radius;
    }

    public void setOutColor(int color)
    {
        this.mOutColor = color;
        mOutTopColor = mOutBotColor = color;
    }

    public void setInnerColor(int color)
    {
        this.mInnerColor = color;
        mInnerTopColor = mInnerBotColor= color;
    }

    public void setMidText(String text)
    {
        this.mMidText = text;
    }

    public void setRoundRectParams(float rx, float ry)
    {

    }

    // ====================================== Get =================================== //
    public RectF getOutRoundRect()
    {
        return mOutRoundRect == null ? new RectF() : mOutRoundRect;
    }

    public RectF getInnerRoundRect()
    {
        return mInnerRoundRect == null ? new RectF() : mInnerRoundRect;
    }

    public int getOutColor()
    {
        return mOutColor;
    }

    public int getInnerColor()
    {
        return mInnerColor;
    }

    public String getMidText()
    {
        return mMidText == null ? "" : mMidText;
    }

    public float getOutRx()
    {
        return mOutRx;
    }

    public float getOutRy()
    {
        return mOutRy;
    }

    public float getInnerRx()
    {
        return mInnerRx;
    }

    public float getInnerRy()
    {
        return mInnerRy;
    }

    public Ring copy()
    {
        Ring out = new Ring();
        out.set(this);
        return out;
    }

    public void set(Ring ring)
    {
        this.mOutColor = ring.mOutColor;
        this.mOutTopColor = ring.mOutTopColor;
        this.mOutBotColor = ring.mOutBotColor;
        this.mOutRoundRect = ring.mOutRoundRect;

        this.mInnerColor = ring.mInnerColor;
        this.mInnerTopColor = ring.mInnerTopColor;
        this.mInnerBotColor = ring.mInnerBotColor;
        this.mInnerRoundRect = ring.mInnerRoundRect;

        this.mOutRx = ring.mOutRx;
        this.mOutRy = ring.mOutRy;
        this.mInnerRx = ring.mInnerRx;
        this.mInnerRy = ring.mInnerRy;
    }
}
