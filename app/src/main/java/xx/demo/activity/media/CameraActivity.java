package xx.demo.activity.media;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;

import xx.demo.activity.BaseActivity;
import xx.demo.view.comment.SimpleRcAdapter;

public class CameraActivity extends BaseActivity
{
    private static final String TITLE = "title";
    private static final String CLASS_NAME_KEY = "class_name";
    private static final String CLASS_NAME = "xx.demo.activity";
    private static final String CLASS_PACKAGE_MEDIA = ".media";

    private RecyclerView mList;
    private ArrayList<HashMap<String, Object>> mActivityArr;

    @Override
    protected void initData()
    {
        String[][] EXAMPLES = new String[][]{
                {
                        CLASS_NAME + CLASS_PACKAGE_MEDIA + ".PreviewCameraActivity", "SurfaceView 预览镜头"
                },
                {
                        CLASS_NAME + CLASS_PACKAGE_MEDIA + ".PreviewCameraV2Activity", "GLSurfaceView + OpenGL ES20 预览镜头"
                },
                {
                        CLASS_NAME + CLASS_PACKAGE_MEDIA + ".GLESBaseActivity", "OpenGL ES20 画基础图形(基础代码流程)"
                },
                {
                        CLASS_NAME + CLASS_PACKAGE_MEDIA + ".GLESBaseV2Activity", "OpenGL ES20 画图片(基础代码流程)"
                },
                {
                        CLASS_NAME + CLASS_PACKAGE_MEDIA + ".GLESActivity", "OpenGL ES20 画基础图形(封装部分代码)"
                },
                {
                        CLASS_NAME + CLASS_PACKAGE_MEDIA + ".GLESV2Activity", "OpenGL ES20 画图片(封装部分代码,和镜头预览有区别)"
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

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mList = new RecyclerView(parent.getContext());
        mList.setLayoutManager(new LinearLayoutManager(parent.getContext(), LinearLayoutManager.VERTICAL, false));
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mList, params);

        initAdapter();
    }

    private void initAdapter()
    {
        SimpleRcAdapter adapter = new SimpleRcAdapter(new SimpleRcAdapter.Source()
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
//                finish();
            }
        });

        mList.setAdapter(adapter);
    }
}