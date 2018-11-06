package xx.demo.activity.life;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/5.
 */
public class MyViewModel extends ViewModel
{
    private MutableLiveData<ArrayList<String>> data = new MutableLiveData<>();

    public LiveData<ArrayList<String>> getData()
    {
        return data;
    }

    public void loadData()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("测试");
        list.add("测试1");

        data.setValue(list);
    }
}
