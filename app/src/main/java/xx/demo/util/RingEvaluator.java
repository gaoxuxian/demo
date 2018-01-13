package xx.demo.util;

import android.animation.TypeEvaluator;
import android.graphics.Color;
import android.util.Log;

import xx.demo.view.Ring;

/**
 * Created by GAO-xx on 2018/1/13.
 */

public class RingEvaluator implements TypeEvaluator<Ring>
{
    private Ring mRing;

    public RingEvaluator()
    {
        mRing = new Ring();
    }

    @Override
    public Ring evaluate(float percent, Ring start_ring, Ring end_ring)
    {
        mRing.set(end_ring);

        /* 对快门 内外圆 颜色作处理 */
        transformColor(percent, start_ring, end_ring, true);
        transformColor(percent, start_ring, end_ring, false);

        // other

        return mRing;
    }

    private void transformColor(float percent, Ring start_ring, Ring end_ring, boolean out)
    {
        int old_color = out ? start_ring.getOutColor() : start_ring.getInnerColor();
        int new_color = out ? end_ring.getOutColor() : end_ring.getInnerColor();

        int old_color_alpha = Color.alpha(old_color);
        int new_color_alpha = Color.alpha(new_color);

        old_color_alpha *= (1f - percent);
        new_color_alpha *= percent;

        if (out)
        {
            Log.d("xxx", "transformColor: old_color_alpha == "+old_color_alpha + "   new_color_alpha == "+new_color_alpha);
        }

        if (out)
        {
            mRing.mOutBotColor = Color.argb(old_color_alpha, Color.red(old_color), Color.green(old_color), Color.blue(old_color));
            mRing.mOutTopColor = Color.argb(new_color_alpha, Color.red(new_color), Color.green(new_color), Color.blue(new_color));
        }
        else
        {
            mRing.mInnerBotColor = Color.argb(old_color_alpha, Color.red(old_color), Color.green(old_color), Color.blue(old_color));
            mRing.mInnerTopColor = Color.argb(new_color_alpha, Color.red(new_color), Color.green(new_color), Color.blue(new_color));
        }
    }
}
