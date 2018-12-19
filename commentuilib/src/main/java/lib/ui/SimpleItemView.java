package lib.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SimpleItemView extends FrameLayout
{
    private Button mTx;

    public SimpleItemView(@NonNull Context context)
    {
        super(context);
        initUI(context);
    }

    private void initUI(Context context)
    {
        mTx = new Button(context);
        mTx.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        mTx.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addView(mTx, params);
    }

    public void setItemText(String text)
    {
        if (mTx == null) return;

        mTx.setText(text);
    }
}
