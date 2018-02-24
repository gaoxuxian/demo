package xx.demo.util;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Gxx on 2017/5/11.
 *
 */

public class MyHolder extends RecyclerView.ViewHolder
{
	public View mItemView;
	private SparseArray<View> mViews;

	public MyHolder(View itemView)
	{
		super(itemView);
		mItemView = itemView;
		mViews = new SparseArray<>();
	}

	public <T extends View> T getViewById(int viewId)
	{
		View view = mViews.get(viewId);
		if (view == null)
		{
			if(mItemView instanceof ViewGroup)
			{
				view = mItemView.findViewById(viewId);
				mViews.put(viewId, view);
			}
		}
		return (T) view;
	}

	public <T extends View> T getItemView()
	{
		return (T) mItemView;
	}

	public void RemoveAllView()
	{
		if(mItemView != null && mItemView instanceof ViewGroup)
		{
			((ViewGroup)mItemView).removeAllViews();
			mItemView.setOnClickListener(null);
			mItemView.setOnTouchListener(null);
			mItemView.setOnLongClickListener(null);
			mItemView = null;
		}

		if(mViews != null && mViews.size() >0)
		{
			mViews.clear();
			mViews = null;
		}
	}
}
