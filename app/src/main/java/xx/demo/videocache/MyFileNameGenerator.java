package xx.demo.videocache;

import android.text.TextUtils;

import com.danikula.videocache.file.FileNameGenerator;

/**
 * Created by Gxx on 2018/2/9.
 */

public class MyFileNameGenerator implements FileNameGenerator
{
    @Override
    public String generate(String url)
    {
        String out = null;

        if (!TextUtils.isEmpty(url))
        {
            String[] partArr = url.split("/");
            if (partArr.length > 0)
            {
                out = partArr[partArr.length - 1];
            }
        }

        return out;
    }
}
