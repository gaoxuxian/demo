package xx.demo.view;

/**
 * 快门的配置信息
 * Created by Gxx on 2018/1/12.
 */

public abstract class ShutterConfig extends BaseConfig
{
    protected Ring mGifRing;
    protected Ring mPhotoRing;

    protected Ring mRing11; // 1:1
    protected Ring mRing43; // 4:3
    protected Ring mRing916; // 9:16
    protected Ring mRing169; // 16:9
    protected Ring mRingFull; // 全屏

    public ShutterConfig()
    {
        mRing11 = new Ring();
        mRing43 = new Ring();
        mRing169 = new Ring();
        mRing916 = new Ring();
        mRingFull = new Ring();
    }
}
