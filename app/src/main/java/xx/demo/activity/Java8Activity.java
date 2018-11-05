package xx.demo.activity;

import android.graphics.Color;
import androidx.core.graphics.ColorUtils;

import android.util.Log;
import android.view.Gravity;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;
import java.util.function.Supplier;

import static android.view.animation.Animation.ABSOLUTE;
import static android.view.animation.Animation.RELATIVE_TO_SELF;

/**
 * http://www.runoob.com/java/java8-new-features.html
 * <p>
 * https://www.jianshu.com/p/bd825cb89e00
 * <p>
 * http://blog.oneapm.com/apm-tech/226.html
 * <p>
 * https://github.com/MaksTuev/EasyAdapter
 */
public class Java8Activity extends BaseActivity
{
    List<String> stringArr;
    List<Integer> integerArr;
    private ImageView imageView;

    @Override
    public void onCreateInitData()
    {

    }

    @Override
    protected void onStart()
    {
        super.onStart();
//        setStatusBarColor(this, Color.TRANSPARENT);
    }

//    static void setStatusBarColor(Activity activity, int statusColor) {
//        Window window = activity.getWindow();
//        WindowManager.LayoutParams attributes = window.getAttributes();
//        attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
//        window.setAttributes(attributes);
//        //取消状态栏透明
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        //添加Flag把状态栏设为可绘制模式
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
////        window.getDecorView().setSystemUiVisibility(
////        View.SYSTEM_UI_FLAG_LOW_PROFILE |
////                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
////                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
////                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        //设置状态栏颜色
//        window.setStatusBarColor(statusColor);
//        //让view不根据系统窗口来调整自己的布局
////        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
////        View mChildView = mContentView.getChildAt(0);
////        if (mChildView != null) {
////            ViewCompat.setFitsSystemWindows(mChildView, false);
////            ViewCompat.requestApplyInsets(mChildView);
////        }
//    }

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
//        parent.setBackgroundColor(Color.RED);
//
//        TextView textView = new TextView(this);
//        textView.setBackgroundColor(Color.WHITE);
//        textView.setText("测试1");
//        textView.setTextColor(Color.BLACK);
//        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        parent.addView(textView, params);
//
//        textView = new TextView(this);
//        textView.setBackgroundColor(Color.WHITE);
//        textView.setText("测试2");
//        textView.setTextColor(Color.BLACK);
//        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.gravity = Gravity.CENTER_HORIZONTAL;
//        parent.addView(textView, params);
//
//        textView = new TextView(this);
//        textView.setBackgroundColor(Color.WHITE);
//        textView.setText("测试3");
//        textView.setTextColor(Color.BLACK);
//        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.gravity = Gravity.END;
//        parent.addView(textView, params);
        imageView = new ImageView(parent.getContext());
        imageView.setBackgroundColor(ColorUtils.setAlphaComponent(Color.RED, (int) (255 * 1f)));
        params = new FrameLayout.LayoutParams(300, 300);
        params.gravity = Gravity.CENTER;
        parent.addView(imageView, params);

        ImageView imageView1 = new ImageView(parent.getContext());
        imageView1.setBackgroundColor(ColorUtils.setAlphaComponent(Color.GREEN, (int) (255 * 0.5f)));
        params = new FrameLayout.LayoutParams(300, 300);
        params.gravity = Gravity.CENTER;
        parent.addView(imageView1, params);
    }

    @Override
    public void onCreateFinal()
    {
        imageView.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                AnimationSet animationSet = new AnimationSet(true);
                ScaleAnimation scaleAnimation = new ScaleAnimation(
                        1.1f, 1.0f, 1.1f, 1.0f,
                        RELATIVE_TO_SELF, 0.5f,
                        RELATIVE_TO_SELF, 0.5f);
                animationSet.setFillAfter(true);
                animationSet.addAnimation(scaleAnimation);

                float fromY = 400f;

                TranslateAnimation animation = new TranslateAnimation(ABSOLUTE, 0, ABSOLUTE, 0, ABSOLUTE, fromY, ABSOLUTE, 0);
                animation.setFillAfter(true);
                animationSet.addAnimation(animation);
                animationSet.setDuration(3000);
                imageView.startAnimation(animationSet);
            }
        }, 5000);

        // type 1
//        test(new Supplier<List<String>>()
//        {
//            @Override
//            public List<String> get()
//            {
//                return new ArrayList<>();
//            }
//        });

        // type 2
//        test(() -> new ArrayList<>());

        // type 3
//        test(ArrayList::new);
//
//        stringArr = Arrays.asList("abc", "", "cde", "", "abcde");
//        List<String> collect = stringArr.stream().filter(String::isEmpty).collect(Collectors.toList());
    }

    public void test(Supplier<List<String>> obj)
    {
        Log.d("xxx", "Java8Activity --> test: obj is null ? :" + (obj == null));
    }
}
