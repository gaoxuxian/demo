package gpu;

import android.content.res.Resources;

public class ResourceEntry
{
    private Resources mRes;

    public ResourceEntry(Resources resources)
    {
        mRes = resources;
    }

    public Resources getResource()
    {
        return mRes;
    }

    public void clear()
    {
        mRes = null;
    }
}
