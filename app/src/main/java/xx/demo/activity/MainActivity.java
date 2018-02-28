package xx.demo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import xx.demo.util.CameraPercentUtil;

public class MainActivity extends Activity
{
    private static final String TITLE = "title";
    private static final String CLASS_NAME_KEY = "class_name";
    private static final String CLASS_NAME = "xx.demo.activity";
    private static final String CLASS_PACKAGE_MEDIA = ".media";
    private static final String CLASS_PACKAGE_VIEW = ".view";

    private ArrayList<HashMap<String, Object>> mActivityArr;

    FrameLayout mParent;
    RecyclerView mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        createActivityInfo();
        initUI(this);
    }

    private void createActivityInfo()
    {
        String[][] EXAMPLES = new String[][]{
                {
                        CLASS_NAME + CLASS_PACKAGE_VIEW + ".ARActivity", "AR Activity"
                },
                {
                        CLASS_NAME + CLASS_PACKAGE_MEDIA + ".ExoActivity", "Exo Activity"
                },
                {
                        CLASS_NAME + CLASS_PACKAGE_VIEW + ".ShutterActivity", "Shutter Activity"
                },
                {
                        CLASS_NAME + CLASS_PACKAGE_MEDIA + ".CameraActivity", "Camera Activity"
                },
                {
                        CLASS_NAME + CLASS_PACKAGE_MEDIA + ".RecordActivity", "Record Activity"
                },
                {
                        CLASS_NAME + ".JavaLockActivity", "Java Lock Activity"
                },
                {
                        CLASS_NAME + CLASS_PACKAGE_MEDIA + ".GLES20Activity", "OpenGL ES20 Base Activity (gl 简单使用)"
                },
                {
                        CLASS_NAME + CLASS_PACKAGE_MEDIA + ".GLES20ActivityV2", "OpenGL ES20 Image Activity (用 gl 画图片)"
                },
        };

        mActivityArr = new ArrayList<>();
        {
            for (String[] example : EXAMPLES)
            {
                HashMap<String, Object> map = new HashMap<>();
                map.put(TITLE, example[1]);
                try
                {
                    Intent intent = new Intent();
                    Class cls = Class.forName(example[0]);
                    intent.setClass(this, cls);
                    map.put(CLASS_NAME_KEY, intent);

                    mActivityArr.add(map);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initUI(Context context)
    {
        mParent = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mParent.setLayoutParams(params);
        setContentView(mParent);
        {
            mContentView = new RecyclerView(context);
            mContentView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            mParent.addView(mContentView, params);
        }

        initSimpleAdapter();
    }

    private void initSimpleAdapter()
    {
        SimpleRcAdapter adapter = new SimpleRcAdapter(new Source()
        {
            @Override
            public Object getSource(Object key)
            {
                Object source = null;
                if (mActivityArr != null)
                {
                    source = mActivityArr.get((int) key);
                    if (source != null)
                    {
                        source = ((HashMap) source).get(TITLE);
                    }
                }
                return source;
            }

            @Override
            public int getSourceSize()
            {
                return mActivityArr != null ? mActivityArr.size() : 0;
            }

            @Override
            public void onSourceClick(Object source_key)
            {
                Object source = null;
                if (mActivityArr != null)
                {
                    source = mActivityArr.get((int) source_key);
                    if (source != null)
                    {
                        source = ((HashMap) source).get(CLASS_NAME_KEY);
                    }
                }

                startActivity((Intent) source);
            }
        });

        mContentView.setAdapter(adapter);
    }

    private static class SimpleRcAdapter extends RecyclerView.Adapter implements View.OnClickListener
    {
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
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.WidthPxToPercent(100));
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

    private static class SimpleItemView extends FrameLayout
    {
        private TextView mTx;

        public SimpleItemView(@NonNull Context context)
        {
            super(context);
            initUI(context);
        }

        private void initUI(Context context)
        {
            mTx = new TextView(context);
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
}
