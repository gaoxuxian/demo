package xx.demo.view;

import android.graphics.Color;

import xx.demo.util.CameraPercentUtil;

/**
 * Created by GAO-xx on 2018/1/13.
 */

public class GifShutterConfig extends ShutterConfig
{
    private Ring mDefRing;
    private Ring mSmallRing;
    private Ring mRecordRing;

    @Override
    protected void initDef()
    {
        super.initDef();

        mDefRing = new Ring();
        mDefRing.setOutRadius(CameraPercentUtil.WidthPxToPercent(100));
        mDefRing.setInnerRadius(CameraPercentUtil.WidthPxToPercent(80));
        mDefRing.setOutColor(0x4DFFFFFF);
        mDefRing.setInnerColor(Color.YELLOW);
    }

    @Override
    protected void initSmall()
    {
        super.initSmall();

        mSmallRing = new Ring();
        mSmallRing.setOutRadius(CameraPercentUtil.WidthPxToPercent(80));
        mSmallRing.setInnerRadius(CameraPercentUtil.WidthPxToPercent(64));
        mSmallRing.setOutColor(Color.WHITE);
        mSmallRing.setInnerColor(Color.YELLOW);
    }

    @Override
    protected void initRecord()
    {
        super.initRecord();

        mRecordRing = new Ring();
        mRecordRing.setOutRadius(CameraPercentUtil.WidthPxToPercent(120));
        mRecordRing.setInnerRadius(CameraPercentUtil.WidthPxToPercent(70));
        mRecordRing.setRoundRectParams(CameraPercentUtil.WidthPxToPercent(5), CameraPercentUtil.WidthPxToPercent(5));
        mRecordRing.setOutColor(Color.WHITE);
        mRecordRing.setInnerColor(Color.YELLOW);
    }

    @Override
    public Ring getDef()
    {
        return mDefRing.copy();
    }

    @Override
    public Ring getSmall()
    {
        return mSmallRing.copy();
    }

    @Override
    public Ring getRecord()
    {
        return mRecordRing.copy();
    }
}
