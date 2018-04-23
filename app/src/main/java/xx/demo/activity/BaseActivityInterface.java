package xx.demo.activity;

import android.widget.FrameLayout;

public interface BaseActivityInterface
{
    void createChildren(FrameLayout parent, FrameLayout.LayoutParams params);

    default void onCreateInitData() {}

    default void onCreateFinal() {}
}
