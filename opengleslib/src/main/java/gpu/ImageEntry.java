package gpu;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageEntry extends ResourceEntry
{
    private Object mImage;

    public ImageEntry(Resources resources)
    {
        super(resources);
    }

    public void setImage(Object image)
    {
        mImage = image;
    }

    public Bitmap processBitmap()
    {
        if (mImage != null)
        {
            if (mImage instanceof Integer)
            {
                return BitmapFactory.decodeResource(getResource(), (Integer) mImage);
            }
            else if (mImage instanceof String && ((String) mImage).startsWith("/"))
            {
                return BitmapFactory.decodeFile((String) mImage);
            }
        }
        return null;
    }
}
