package seekbar;

public class SeekBarConfigFactory
{
    public @interface ConfigType
    {
        int line = 0;
        int circle_point = 1;
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
            case ConfigType.circle_point:
            {
                return new CirclePointConfig();
            }

            default:
            {
                return new LineConfig();
            }
        }
    }
}
