package xx.demo.util;

/**
 *
 * Created by Gxx on 2017/11/8.
 */

public class PixelPercentUtil
{
    /**
     * 宽720为标准，获取百分比位置
     *
     * @return
     */
    public static int WidthPxToPercent(int size)
    {
        return (int) (size * ShareData.m_screenRealWidth / 720f + 0.5f);
    }

    /**
     * 高1280为标准,获取百分比位置
     */
    public static int HeightPxToPercent(int size)
    {
        return (int) (size * ShareData.m_screenRealWidth * 16f / 9f / 1280f + 0.5f);
    }

    /**
     * 宽1080为标准，获取百分比位置
     *
     * @return
     */
    public static int WidthPxxToPercent(int size)
    {
        return (int) (size * ShareData.m_screenRealWidth / 1080f + 0.5f);
    }

    /**
     * 高1920为标准,获取百分比位置
     */
    public static int HeightPxxToPercent(int size)
    {
        return (int) (size * ShareData.m_screenRealWidth * 16f / 9f / 1920f + 0.5f);
    }
}
