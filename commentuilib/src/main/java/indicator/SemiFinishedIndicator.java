package indicator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * @author Gxx
 * Created by Gxx on 2018/9/26.
 */
public abstract class SemiFinishedIndicator<T extends IConfig> extends View
{
    private T mConfig;

    private SemiFinishedIndicator(Context context)
    {
        super(context);
    }

    public SemiFinishedIndicator(Context context, @NonNull T config)
    {
        this(context);
        mConfig = config;
    }

}
