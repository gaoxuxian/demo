package lib.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author Gxx
 * Created by Gxx on 2018/10/29.
 */
public class PictureTestView extends SurfaceView
{
    private Handler mHandler;
    private HandlerThread mThread;
    private Paint mPaint;
    private volatile int mState = 0;
    private Picture mPicture;

    public static final int UPDATE_MSG = 1000;

    public PictureTestView(Context context)
    {
        super(context);

        mPaint = new Paint();

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ar_circle);

        mPicture = new Picture();
        Canvas c = mPicture.beginRecording(100, 100);
        c.drawColor(Color.WHITE);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        c.drawBitmap(bmp, new Matrix(), mPaint);
        mPicture.endRecording();

        bmp.recycle();

        mThread = new HandlerThread("draw_content_thread");
        mThread.start();

        mHandler = new Handler(mThread.getLooper(), new Handler.Callback()
        {
            @Override
            public boolean handleMessage(Message msg)
            {
                if (msg != null && msg.what == UPDATE_MSG)
                {
                    if (mState == 0)
                    {
                        updateCanvas(msg.arg1);
                    }
                }
                return true;
            }
        });
    }

    private void updateCanvas(int radius)
    {
        mState = 1;
        SurfaceHolder holder = getHolder();
        if (holder != null)
        {
            Canvas canvas = holder.lockCanvas();

            if (canvas != null)
            {
                canvas.translate(getMeasuredWidth() / 2f, getMeasuredHeight() / 2f);
                canvas.drawPicture(mPicture);

                mPaint.reset();
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                mPaint.setColor(ColorUtils.setAlphaComponent(Color.BLUE, (int) (0.6f * 255)));
                canvas.drawCircle(0, 0, radius, mPaint);

                holder.unlockCanvasAndPost(canvas);
            }
        }
        mState = 0;
    }

    public void requireRender(int radius)
    {
        if (mHandler != null)
        {
            Message message = mHandler.obtainMessage();
            message.what = UPDATE_MSG;
            message.arg1 = radius;
            mHandler.sendMessage(message);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            {
                Log.d("xxx", "onTouchEvent: down");
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                Log.d("xxx", "onTouchEvent: move");
                break;
            }

            case MotionEvent.ACTION_UP:
            {
                Log.d("xxx", "onTouchEvent: up");
                break;
            }
        }
        return true;
    }
}
