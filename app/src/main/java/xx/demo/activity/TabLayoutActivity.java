package xx.demo.activity;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import xx.demo.util.CameraPercentUtil;
import xx.demo.util.ShareData;

public class TabLayoutActivity extends Activity
{
    ViewPager viewPager;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FrameLayout layout = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        setContentView(layout);

        ArrayList<String> data = new ArrayList<>();
        data.add("HOT");
        data.add("小黑猫");
        data.add("发抖发抖");
        data.add("管理");
        data.add("HOT1");
        data.add("小黑猫1");
        data.add("发抖发抖1");
        data.add("管理1");
        data.add("HOT2");
        data.add("小黑猫2");
        data.add("发抖发抖2");
        data.add("管理2");
        data.add("HOT3");
        data.add("小黑猫3");
        data.add("发抖发抖3");
        data.add("管理3");

        MyAdapter adapter = new MyAdapter();
        adapter.setData(data);

        viewPager = new ViewPager(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.WidthPxToPercent(300));
        params.gravity = Gravity.BOTTOM;
        layout.addView(viewPager, params);
        viewPager.setAdapter(adapter);

        tabLayout = new TabLayout(this);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.WidthPxToPercent(80));
        params.gravity = Gravity.BOTTOM;
        params.bottomMargin = CameraPercentUtil.WidthPxToPercent(300);
        layout.addView(tabLayout, params);

        layout.post(new Runnable()
        {
            @Override
            public void run()
            {
                viewPager.setCurrentItem(7);
            }
        });
    }

    public class MyAdapter extends PagerAdapter
    {
        private ArrayList<String> mData;

        public MyAdapter()
        {
            mData = new ArrayList<>();
        }

        public void setData(ArrayList<String> data)
        {
            if (mData != null)
            {
                mData.clear();
                mData.addAll(data);
                notifyDataSetChanged();
            }
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mData.get(position);
        }

        @Override
        public int getCount()
        {
            return mData != null ? mData.size() : 0;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            TextView textView = new TextView(container.getContext());
            textView.setText(mData.get(position));
            textView.setTextColor(Color.BLACK);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(textView, params);
            return textView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }
    }
}
