package xx.demo.view;

import android.content.Context;
import android.graphics.Canvas;

/**
 * 快门
 * Created by Gxx on 2018/1/12.
 */

public class ShutterView extends BaseView
{
    public ShutterView(Context context, int def_wh)
    {
        super(context, def_wh);
    }

    @Override
    public void setConfig(BaseConfig config)
    {
        startToDraw();
    }

    @Override
    protected void drawToCanvas(Canvas canvas)
    {

    }
}
