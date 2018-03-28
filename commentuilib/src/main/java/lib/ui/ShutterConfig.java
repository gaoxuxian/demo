package lib.ui;

/**
 * Created by GAO-xx on 2018/1/13.
 */

public class ShutterConfig extends BaseConfig
{
    @Override
    public void init()
    {
        initDef();
        initSmall();
        initRecord();
    }

    protected void initDef()
    {

    }

    protected void initSmall()
    {

    }

    protected void initRecord()
    {

    }

    public Ring getDef()
    {
        return new Ring();
    }

    public Ring getSmall()
    {
        return new Ring();
    }

    public Ring getRecord()
    {
        return new Ring();
    }
}
