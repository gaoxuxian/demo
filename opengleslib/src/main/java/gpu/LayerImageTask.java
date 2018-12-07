package gpu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import util.FileUtil;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/6.
 */
public class LayerImageTask extends AbsTask
{
    private volatile Listener mListener;
    private volatile Object mBitmapRes;
    private volatile Object mBitmap;

    public interface Listener
    {
        void onStart();

        void onBitmapSucceed(Bitmap bitmap);
    }

    public LayerImageTask(Context context, Listener listener)
    {
        super(context);
        this.mListener = listener;
    }

    public void setBitmapResource(Object res)
    {
        this.mBitmapRes = res;
    }

    @Override
    public void runOnThread()
    {
        try
        {
            processBitmap();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void executeTaskCallback()
    {
        if (mListener != null)
        {
            mListener.onStart();

            if (mBitmap != null)
            {
                if (mBitmap instanceof Bitmap)
                {
                    mListener.onBitmapSucceed((Bitmap) mBitmap);
                }
            }
        }
    }

    @Override
    public void destroy()
    {
        super.destroy();

        mListener = null;
    }

    public void processBitmap()
    {
        if (mBitmapRes != null)
        {
            if (mBitmapRes instanceof Integer)
            {
                mBitmap = BitmapFactory.decodeResource(getContext().getResources(), (int) mBitmapRes);
            }
            else if (mBitmapRes instanceof String && !TextUtils.isEmpty((String) mBitmapRes) && FileUtil.isFileExists((String) mBitmapRes))
            {
                mBitmap = BitmapFactory.decodeFile((String) mBitmapRes);
            }
            else if (mBitmapRes instanceof Bitmap)
            {
                mBitmap = mBitmapRes;
            }
        }
    }
}
