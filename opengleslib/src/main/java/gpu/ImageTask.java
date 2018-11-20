package gpu;

import android.graphics.Bitmap;

public class ImageTask extends AbsTask
{
    private ImageEntry mEntry;
    private volatile Bitmap mBitmap;
    private CallBack mCB;

    public interface CallBack
    {
        void onBitmapSucceed(Bitmap bitmap);
    }

    public ImageTask(ImageEntry entry, CallBack cb)
    {
        this.mEntry = entry;
        this.mCB = cb;
    }

    @Override
    public void runOnThread()
    {
        if (mEntry != null)
        {
            try
            {
                mBitmap = mEntry.processBitmap();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.gc();
            }
        }
    }

    @Override
    public void executeTaskCallback()
    {
        if (mCB != null)
        {
            mCB.onBitmapSucceed(mBitmap);
        }
    }

    @Override
    public void clear()
    {
        super.clear();
        mBitmap = null;
        mEntry = null;
        mCB = null;
    }
}
