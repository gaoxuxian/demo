package gpu;

import android.graphics.Bitmap;

public class WaterTask extends AbsTask
{
    private volatile Bitmap mBitmap;
    private volatile WaterEntry mEntry;
    private CallBack mCB;

    public interface CallBack
    {
        void onBitmapSucceed(Bitmap bitmap);
    }

    public WaterTask(WaterEntry entry, CallBack cb)
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
