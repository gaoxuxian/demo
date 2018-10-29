package lib.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import util.PxUtil;

public class SimpleRcAdapter extends RecyclerView.Adapter implements View.OnClickListener
{
    public interface Source
    {
        /**
         * 根据 key 找到 source
         *
         * @param key 一般是 source 下标
         * @return source
         */
        Object getSource(Object key);

        int getSourceSize();

        /**
         * source 点击事件
         *
         * @param source_key 一般是 source 下标
         */
        void onSourceClick(Object source_key);
    }

    private Source mSourceListener;

    public SimpleRcAdapter(Source source)
    {
        this.mSourceListener = source;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        SimpleItemView itemView = new SimpleItemView(parent.getContext());
        itemView.setOnClickListener(this);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sU_1080p(150));
        itemView.setLayoutParams(params);
        return new RecyclerView.ViewHolder(itemView)
        {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (mSourceListener == null) return;

        Object source = mSourceListener.getSource(position);

        if (source == null) return;

        if (source instanceof String)
        {
            View itemView = holder.itemView;

            if (itemView == null) return;

            itemView.setTag(position);

            if (itemView instanceof SimpleItemView)
            {
                ((SimpleItemView) itemView).setItemText((String) source);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return mSourceListener != null ? mSourceListener.getSourceSize() : 0;
    }

    @Override
    public void onClick(View v)
    {
        if (mSourceListener == null) return;

        int position = (int) v.getTag();

        mSourceListener.onSourceClick(position);
    }
}
