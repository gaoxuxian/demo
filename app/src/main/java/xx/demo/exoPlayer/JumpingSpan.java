package xx.demo.exoPlayer;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by admin on 2018/2/12.
 */

public class JumpingSpan extends SuperscriptSpan implements ValueAnimator.AnimatorUpdateListener{
    private final WeakReference<TextView> textView;
    private final int delay;//每一个字体的间隔的时间
    private final int loopDuration;//设置字体跳动的循环的时间
    private final float animatedRange;//字体摆动的速度范围0,1
    private int shift;//字体摆动的偏移量
    private ValueAnimator jumpAnimator;
    public JumpingSpan(@NonNull TextView textView,
                       @IntRange(from = 1) int loopDuration,
                       @IntRange(from = 0) int position,
                       @IntRange(from = 0) int waveCharOffset,//单个字体偏移量时间间隔
                       @FloatRange(from = 0, to = 1, fromInclusive = false) float animatedRange) {
        this.textView = new WeakReference<>(textView);
        this.delay = waveCharOffset * position;
        this.loopDuration = loopDuration;
        this.animatedRange = animatedRange;
    }


    @Override
    public void updateDrawState(TextPaint tp) {
        super.updateDrawState(tp);
        initIfNecessary(tp.ascent());
        tp.baselineShift = shift;//更新字体底部基线的偏移量

    }

    @Override
    public void updateMeasureState(TextPaint tp) {
        super.updateMeasureState(tp);
        initIfNecessary(tp.ascent());
        tp.baselineShift = shift;
    }

    private void initIfNecessary(float ascent) {
        if (jumpAnimator != null) {
            return;
        }

        this.shift = 0;
        int maxShift = (int) ascent / 2;
        //设置最大字体偏移量  maxshift*(0,1)
        jumpAnimator = ValueAnimator.ofInt(0, maxShift);
        jumpAnimator
                .setDuration(loopDuration)
                .setStartDelay(delay);
        jumpAnimator.setInterpolator(new JumpInterpolator(animatedRange));
        jumpAnimator.setRepeatCount(ValueAnimator.INFINITE);
        jumpAnimator.setRepeatMode(ValueAnimator.RESTART);
        jumpAnimator.addUpdateListener(this);
        jumpAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        // No need for synchronization as this always runs on main thread anyway
        TextView v = textView.get();
        if (v != null) {
            updateAnimationFor(animation, v);
        }
    }

    private void updateAnimationFor(ValueAnimator animation, TextView v) {
        if (isAttachedToHierarchy(v)) {
            //设置最大字体偏移量  maxshift*(0,1)
            shift = (int) animation.getAnimatedValue();//
            v.invalidate();
        }
    }

    private static boolean isAttachedToHierarchy(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return v.isAttachedToWindow();
        }
        return v.getParent() != null;   // Best-effort fallback (without adding support-v4 just for this...)
    }

    private static class JumpInterpolator implements TimeInterpolator
    {

        private final float animRange;

        public JumpInterpolator(float animatedRange) {
            animRange = Math.abs(animatedRange);//字体摆动的速度 0-1
        }

        @Override
        public float getInterpolation(float input) {
            // We want to map the [0, PI] sine range onto [0, animRange]
            if (input > animRange) {
                return 0f;
            }//(0,animRange)//在这个速度范围内摆动的弧度(0,pi)
            double radians = (input / animRange) * Math.PI;
            return (float) Math.sin(radians);//(0,1)
        }

    }
}
