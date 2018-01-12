package xx.demo.view;

import android.graphics.Rect;

/**
 *
 * Created by Gxx on 2018/1/12.
 */

public class Ring
{
    private Rect mOutRoundRect;

    private Rect mInnerRoundRect;

    private int mOutColor;

    private int mInnerColor;

    private String mMidText;

    public Ring()
    {
        mOutRoundRect = new Rect();
        mInnerRoundRect = new Rect();
    }

    // ====================================== Set =================================== //

    public void setOutRadius(int radius)
    {
        this.mOutRoundRect.set(-radius, -radius, radius, radius);
    }

    public void setInnerRadius(int radius)
    {
        this.mInnerRoundRect.set(-radius, -radius, radius, radius);
    }

    public void setOutColor(int color)
    {
        this.mOutColor = color;
    }

    public void setInnerColor(int color)
    {
        this.mInnerColor = color;
    }

    public void setMidText(String text)
    {
        this.mMidText = text;
    }

    // ====================================== Get =================================== //
    public Rect getOutRoundRect()
    {
        return mOutRoundRect == null ? new Rect() : mOutRoundRect;
    }

    public Rect getInnerRoundRect()
    {
        return mInnerRoundRect == null ? new Rect() : mInnerRoundRect;
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
}
