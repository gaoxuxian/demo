package xx.demo.view;

/**
 * Created by GAO-xx on 2018/1/13.
 */

public class ShutterConfig extends BaseConfig
{
    protected Ring mRing;

    @Override
    public void init()
    {
        mRing = new Ring();

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

    public Ring getRing()
    {
        return mRing;
    }

    public Ring getDef()
    {
        return getRing();
    }

    public Ring getSmall()
    {
        return getRing();
    }

    public Ring getRecord()
    {
        return getRing();
    }
}
