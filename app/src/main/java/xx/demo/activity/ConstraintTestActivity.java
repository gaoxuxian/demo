package xx.demo.activity;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Constraints;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;

import util.PxUtil;

public class ConstraintTestActivity extends BaseActivity
{
    private CoordinatorLayout mCoordinatorLayout;
    private ConstraintLayout mConstraintLayout;
    private Button btn1;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mConstraintLayout = new ConstraintLayout(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mConstraintLayout, params);
        {
            Button button = new Button(parent.getContext());
            button.setId(View.generateViewId());
            button.setText("测试");
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // btn1.setTranslationY(0);
                    // btn1.animate().translationY(200).setDuration(300).start();
                    if (btn1.getVisibility() == View.VISIBLE)
                    {
                        btn1.setVisibility(View.GONE);
                    }
                    else
                    {
                        btn1.setVisibility(View.VISIBLE);
                    }
                }
            });
            ConstraintLayout.LayoutParams clp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mConstraintLayout.addView(button, clp);

            btn1 = new Button(parent.getContext());
            btn1.setId(View.generateViewId());
            btn1.setText("btn1");
            mConstraintLayout.addView(btn1);

            Button btn2 = new Button(parent.getContext());
            btn2.setId(View.generateViewId());
            btn2.setText("btn2");
            mConstraintLayout.addView(btn2);

            ConstraintSet set = new ConstraintSet();
            set.connect(btn1.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            set.connect(btn1.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
            set.connect(btn1.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
            set.connect(btn1.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            set.constrainWidth(btn1.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainHeight(btn1.getId(), ConstraintSet.WRAP_CONTENT);

            set.connect(btn2.getId(), ConstraintSet.LEFT, btn1.getId(), ConstraintSet.RIGHT, PxUtil.sU_1080p(30));
            set.connect(btn2.getId(), ConstraintSet.TOP, btn1.getId(), ConstraintSet.TOP);
            set.constrainWidth(btn2.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainHeight(btn2.getId(), ConstraintSet.WRAP_CONTENT);

            set.applyTo(mConstraintLayout);

            ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(300);
            LayoutAnimationController controller = new LayoutAnimationController(scaleAnimation, 0.4f);
            controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
            mConstraintLayout.setLayoutAnimation(controller);
            mConstraintLayout.startLayoutAnimation();
        }
    }
}
