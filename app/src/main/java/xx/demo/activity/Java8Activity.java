package xx.demo.activity;

import android.util.Log;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    @Override
    public void onCreateInitData()
    {

    }

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
    }

    @Override
    public void onCreateFinal()
    {
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
        test(ArrayList::new);

        stringArr = Arrays.asList("abc", "", "cde", "", "abcde");
        List<String> collect = stringArr.stream().filter(String::isEmpty).collect(Collectors.toList());
    }

    public void test(Supplier<List<String>> obj)
    {
        Log.d("xxx", "Java8Activity --> test: obj is null ? :" + (obj == null));
    }
}
