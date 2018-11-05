package indicator;

import android.content.Context;
import android.graphics.Canvas;
import androidx.annotation.NonNull;

/**
 * @author Gxx
 * Created by Gxx on 2018/9/26.
 */
public class RectIndicator extends SemiFinishedIndicator<RectIndicatorConfig>
{
    public RectIndicator(Context context, @NonNull RectIndicatorConfig config)
    {
        super(context, config);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }
}
