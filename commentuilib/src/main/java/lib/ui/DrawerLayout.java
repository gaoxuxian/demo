package lib.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import util.PxUtil;

public class DrawerLayout extends LinearLayout implements View.OnClickListener
{
    private RecyclerView mContentList;
    private FrameLayout mDrawerView;
    private TextView mDrawerTextView;
    private ImageView mDrawerLogoView;

    private boolean isOpen;

    private ArrayList<String> mDataList;

    private String mData;

    public DrawerLayout(Context context)
    {
        super(context);
        setOrientation(VERTICAL);
        initData();
        initView(context);
    }

    private void initData()
    {
        mDataList = new ArrayList<>();
        mDataList.add("10s");
        mDataList.add("3min");
        mDataList.add("2h");
        mDataList.add("4D");
        mDataList.add("5Y");

        mData = mDataList.remove(0);

        isOpen = true;
    }

    private void initView(Context context)
    {
        mContentList = new RecyclerView(context);
        mContentList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true));
        mContentList.setAdapter(new MyAdapter());
        LinearLayout.LayoutParams lp = new LayoutParams(PxUtil.sU_1080p(250), PxUtil.sU_1080p(600));
        this.addView(mContentList, lp);

        mDrawerView = new FrameLayout(context);
        mDrawerView.setOnClickListener(this);
        lp = new LayoutParams(PxUtil.sU_1080p(250), PxUtil.sU_1080p(130));
        this.addView(mDrawerView, lp);
        {
            mDrawerTextView = new TextView(context);
            mDrawerTextView.setBackgroundColor(Color.RED);
            mDrawerTextView.setTextColor(Color.WHITE);
            mDrawerTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            mDrawerTextView.setText(mData);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mDrawerView.addView(mDrawerTextView, params);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == mDrawerView)
        {
            isOpen = !isOpen;
            if (isOpen)
            {
                mContentList.setVisibility(VISIBLE);

                ObjectAnimator a = ObjectAnimator.ofFloat(mContentList, "translationY", PxUtil.sV_1080p(600), 0);
                ObjectAnimator b = ObjectAnimator.ofFloat(mContentList, "alpha", 0, 1);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(a, b);
                set.setDuration(200);
                set.start();
            }
            else
            {
                ObjectAnimator a = ObjectAnimator.ofFloat(mContentList, "translationY", PxUtil.sV_1080p(600));
                ObjectAnimator b = ObjectAnimator.ofFloat(mContentList, "alpha", 1, 0);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(a, b);
                set.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        mContentList.setVisibility(INVISIBLE);
                    }
                });
                set.setDuration(200);
                set.start();
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter implements View.OnClickListener
    {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            TextView itemView = new TextView(parent.getContext());
            itemView.setBackgroundColor(Color.GRAY);
            itemView.setTextColor(Color.WHITE);
            itemView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            itemView.setOnClickListener(this);
            ViewGroup.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sU_1080p(130));
            itemView.setLayoutParams(params);
            return new RecyclerView.ViewHolder(itemView) {};
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            TextView itemView = (TextView) holder.itemView;
            itemView.setTag(position);
            itemView.setText(mDataList.get(position));
        }

        @Override
        public int getItemCount()
        {
            return mDataList.size();
        }

        @Override
        public void onClick(View v)
        {
            int position = (int) v.getTag();
            String data = mDataList.remove(position);
            mDataList.add(mData);
            mData = data;

            mDrawerTextView.setText(mData);
            notifyDataSetChanged();
        }
    }
}
