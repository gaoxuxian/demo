package xx.demo.activity.life;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/5.
 */
public class MyViewModel extends ViewModel
{
    private MutableLiveData<String> data = new MutableLiveData<>();

    public LiveData<String> getData()
    {
        return data;
    }

    public void loadData()
    {
        data.setValue("测试");
    }
}
