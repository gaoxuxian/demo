package xx.demo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

import util.PxUtil;
import xx.demo.R;

public class SnapHelperTestActivity extends BaseActivity
{
    RecyclerView mItemView;
    private ArrayList<Bitmap> mData;

    @Override
    public void onCreateInitData()
    {
        mData = new ArrayList<>();
        int[] resArr = new int[]{R.drawable.ic_test_1, R.drawable.ic_test_2, R.drawable.ic_test_3, R.drawable.ic_test_4,
                R.drawable.ic_test_5,R.drawable.ic_test_6, R.drawable.ic_test_7,R.drawable.ic_test_8,R.drawable.ic_test_9,
                R.drawable.ic_test_10,R.drawable.ic_test_11,R.drawable.ic_test_12,R.drawable.ic_test_13, R.drawable.ic_test_14};

        for (int id : resArr)
        {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
            mData.add(bitmap);
        }
    }

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mItemView = new RecyclerView(parent.getContext());
        mItemView.setLayoutManager(new LinearLayoutManager(parent.getContext(), LinearLayoutManager.HORIZONTAL, false));
        PagerSnapHelper pagerSnapHelper = new MyPagerSnapHelper(mItemView);
        pagerSnapHelper.attachToRecyclerView(mItemView);
        mItemView.addItemDecoration(new RecyclerView.ItemDecoration()
        {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
            {
                int count = parent.getAdapter().getItemCount();
                RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager)
                {
                    int pos = parent.getChildAdapterPosition(view);

                    // if (pos == 0)
                    // {
                    //     outRect.left = PxUtil.sU_1080p(360);
                    //     outRect.right = PxUtil.sU_1080p(30);
                    // }
                    // else if (pos == count - 1)
                    // {
                    //     outRect.right = PxUtil.sU_1080p(360);
                    //     outRect.left = PxUtil.sU_1080p(30);
                    // }
                    // else
                    // {
                    //     outRect.left = PxUtil.sU_1080p(30);
                    //     outRect.right = PxUtil.sU_1080p(30);
                    // }
                    outRect.left = PxUtil.sU_1080p(60);
                    outRect.right = PxUtil.sU_1080p(60);
                }
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        parent.addView(mItemView, params);

        mItemView.setAdapter(new MyAdapter(mData));

        Button button = new Button(parent.getContext());
        button.setText("测试");
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mItemView.smoothScrollToPosition(5);
                // mItemView.scrollToPosition(5);
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(button, params);
    }

    private static class MyAdapter extends RecyclerView.Adapter
    {
        private ArrayList<Bitmap> mData;

        public MyAdapter(ArrayList<Bitmap> data)
        {
            mData = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            ImageView view = new ImageView(parent.getContext());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(PxUtil.sU_1080p(960), PxUtil.sU_1080p(1706));
            view.setLayoutParams(params);
            return new RecyclerView.ViewHolder(view) {};
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            ImageView itemView = (ImageView) holder.itemView;
            itemView.setImageBitmap(mData.get(position));
        }

        @Override
        public int getItemCount()
        {
            return mData != null ? mData.size() : 0;
        }
    }

    private static class MyPagerSnapHelper extends PagerSnapHelper
    {
        private RecyclerView recyclerView;

        public MyPagerSnapHelper(RecyclerView recyclerView)
        {
            this.recyclerView = recyclerView;
        }

        @Nullable
        @Override
        public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView)
        {
            int i = recyclerView.getChildAdapterPosition(targetView);
            Log.d("xxx", "findSnapView: i == " + i);
            return super.calculateDistanceToFinalSnap(layoutManager, targetView);
        }

        @Nullable
        @Override
        public View findSnapView(RecyclerView.LayoutManager layoutManager)
        {
            return super.findSnapView(layoutManager);
        }

        @Override
        public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY)
        {
            return super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
        }
    }
}
