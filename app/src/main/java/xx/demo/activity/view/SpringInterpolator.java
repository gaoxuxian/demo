package xx.demo.activity.view;

import android.support.annotation.FloatRange;
import android.view.animation.BaseInterpolator;

// 弹性插值器
public class SpringInterpolator extends BaseInterpolator
{
    private float mFactor;

    public SpringInterpolator()
    {
        setFactorSize(0.4f);
    }

    /**
     * 动画系数，系数越小，弹性次数越多
     * @param factor
     */
    public void setFactorSize(@FloatRange(from = 0f, to = 2f) float factor)
    {
        mFactor = factor;
    }

    @Override
    public float getInterpolation(float input)
    {
        return (float) (Math.pow(2, -10 * input) * Math.sin((input - mFactor / 4) * (2 * Math.PI) / mFactor) + 1f);
    }
}
