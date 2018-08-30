package gpu;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class WaterEntry extends ResourceEntry
{
    private Object mWaterID;

    public WaterEntry(Resources resources)
    {
        super(resources);
    }

    public Bitmap processBitmap()
    {
        if (mWaterID != null)
        {
            if (mWaterID instanceof Integer)
            {
                return BitmapFactory.decodeResource(getResource(), (Integer) mWaterID);
            }
            else if (mWaterID instanceof String)
            {
                return BitmapFactory.decodeFile((String) mWaterID);
            }
        }

        return null;
    }

    public void setWaterID(Object id)
    {
        this.mWaterID = id;
    }
}
