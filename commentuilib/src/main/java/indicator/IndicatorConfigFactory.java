package indicator;

/**
 * @author Gxx
 * Created by Gxx on 2018/9/26.
 */
public class IndicatorConfigFactory
{
    public @interface ConfigType
    {
        int rect = 0;
        int circle = 1;
    }

    /**
     *
     * @param type {@link ConfigType}
     * @return
     */
    public static IConfig createConfig(@ConfigType int type)
    {
        switch (type)
        {
            case ConfigType.rect:
            {
                return new RectIndicatorConfig();
            }

            default:
            {
                return new CircleIndicatorConfig();
            }
        }
    }
}
