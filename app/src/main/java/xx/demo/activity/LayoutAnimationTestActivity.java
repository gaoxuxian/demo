package xx.demo.activity;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import util.PxUtil;

/**
 * https://blog.csdn.net/harvic880925/article/details/50785786
 *
 * http://glanwang.com/2016/01/26/Android/Android%E5%B8%83%E5%B1%80%E5%8A%A8%E7%94%BB(LayoutAnimation)/
 */
public class LayoutAnimationTestActivity extends BaseActivity
{
    private LinearLayout mItemView;

    @Override
    public void onCreateInitData()
    {

    }

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mItemView = new LinearLayout(parent.getContext());
        mItemView.setOrientation(LinearLayout.VERTICAL);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.topMargin = PxUtil.sV_1080p(300);
        parent.addView(mItemView, params);

        // PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0, 1);
        // PropertyValuesHolder translationX = PropertyValuesHolder.ofFloat("translationX", PxUtil.sScreenRealWidth, 0);
        // ObjectAnimator enter = ObjectAnimator.ofPropertyValuesHolder(mItemView, alpha, translationX);
        // enter.setDuration(300);
        //
        // alpha = PropertyValuesHolder.ofFloat("alpha", 1, 0);
        // translationX = PropertyValuesHolder.ofFloat("translationX", 0, PxUtil.sScreenRealWidth);
        // ObjectAnimator out = ObjectAnimator.ofPropertyValuesHolder(mItemView, alpha, translationX);
        // out.setDuration(300);
        //
        // LayoutTransition transition = new LayoutTransition();
        // transition.setAnimator(LayoutTransition.APPEARING, enter);
        // transition.setAnimator(LayoutTransition.DISAPPEARING, out);
        // mItemView.setLayoutTransition(transition);

        Button btn = new Button(parent.getContext());
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sV_1080p(200));
        mItemView.addView(btn, ll);

        btn = new Button(parent.getContext());
        ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sV_1080p(200));
        mItemView.addView(btn, ll);

        btn = new Button(parent.getContext());
        ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sV_1080p(200));
        mItemView.addView(btn, ll);

        // TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, PxUtil.sScreenRealWidth, Animation.ABSOLUTE,  0, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0);
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF,  0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        translateAnimation.setDuration(300);
        LayoutAnimationController controller = new LayoutAnimationController(translateAnimation);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        controller.setDelay(0.3f);

        mItemView.setLayoutAnimation(controller);
        mItemView.startLayoutAnimation();

        LinearLayout btnLayout = new LinearLayout(parent.getContext());
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sV_1080p(300));
        parent.addView(btnLayout, params);
        {
            Button button = new Button(parent.getContext());
            button.setText("增加1个");
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Button btn = new Button(v.getContext());
                    LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sV_1080p(200));
                    mItemView.addView(btn, ll);
                    mItemView.scheduleLayoutAnimation();
                }
            });
            ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            btnLayout.addView(button, ll);

            button = new Button(parent.getContext());
            button.setText("删除1个");
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int childCount = mItemView.getChildCount();
                    if (childCount > 0)
                    {
                        mItemView.removeViewAt(childCount - 1);
                    }
                }
            });
            ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            btnLayout.addView(button, ll);

            button = new Button(parent.getContext());
            button.setText("清空");
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mItemView.removeAllViews();
                }
            });
            ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            btnLayout.addView(button, ll);
        }
    }
}
