package xx.demo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;

import lib.ui.SimpleRcAdapter;

public class MainActivity extends Activity
{
    private static final String TITLE = "title";
    private static final String CLASS_NAME_KEY = "class_name";
    private static final String CLASS_NAME = "xx.demo.activity";
    private static final String CLASS_PACKAGE_MEDIA = ".media";
    private static final String CLASS_PACKAGE_VIEW = ".view";
    private static final String CLASS_PACKAGE_LIFE = ".life";

    private static final String GLES_CLASE_NAME = "xx.demo.gles";

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
                        CLASS_NAME + ".JavaLockActivity", "Java Lock Activity"
                },
                {
                        CLASS_NAME + CLASS_PACKAGE_VIEW + ".ARActivity", "AR Activity"
                },
                {
                        CLASS_NAME + CLASS_PACKAGE_VIEW + ".ShutterActivity", "Shutter Activity"
                },
                {
                        CLASS_NAME + CLASS_PACKAGE_MEDIA + ".ExoActivity", "Exo Activity"
                },
                {
                        GLES_CLASE_NAME + ".GlesActivity8", "一个 Filter 画多个 Program"
                },
                {
                        GLES_CLASE_NAME + ".GlesActivity9", "一个 Filter 加载多张 2D 图片"
                },
                {
                        GLES_CLASE_NAME + ".GlesActivity10", "2D 图片画水印"
                },
                {
                        GLES_CLASE_NAME + ".GlesActivity11", "OpenGL 绘制镜头数据"
                },
                {
                        GLES_CLASE_NAME + ".GlesActivity12", "OpenGL FBO 绘制图片"
                },
                {
                        GLES_CLASE_NAME + ".GlesActivity13", "EGL 后台处理图片"
                },
                {
                        GLES_CLASE_NAME + ".GlesActivity14", "GPUFilter 测试"
                },
                {
                        GLES_CLASE_NAME + ".GlesActivity15", "OPEN GL ES 3.0 测试 抗锯齿"
                },
                {
                        GLES_CLASE_NAME + ".GlesActivity16", "OPEN GL ES 蒙版测试"
                },
                {
                        GLES_CLASE_NAME + ".GlesActivity17", "OPEN GL ES 复制纹理测试"
                },
                {
                        CLASS_NAME + ".SurfaceViewBgTestActivity", "SurfaceView 线程绘制"
                },
                {
                        CLASS_NAME + ".RenderScriptTestActivity", "测试 RenderScript 毛玻璃效果"
                },
                {
                        CLASS_NAME + ".ConstraintTestActivity", "测试 ConstraintLayout"
                },
                {
                        CLASS_NAME + ".SnapHelperTestActivity", "测试 SnapHelper"
                },
                {
                        CLASS_NAME + ".LayoutAnimationTestActivity", "测试 LayoutAnimation"
                },
                {
                        CLASS_NAME + CLASS_PACKAGE_LIFE + ".LifecycleActivity", "测试 Lifecycle"
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
        mParent.setBackgroundColor(Color.WHITE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mParent.setLayoutParams(params);
        setContentView(mParent);
        {
            mContentView = new RecyclerView(context);
            mContentView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            mParent.addView(mContentView, params);

//            DottedLineView dottedLineView = new DottedLineView(context);
//            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            params.gravity = Gravity.CENTER;
//            mParent.addView(dottedLineView, params);
        }

        initSimpleAdapter();
    }

    private void initSimpleAdapter()
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

        mContentView.setAdapter(adapter);
    }
}
