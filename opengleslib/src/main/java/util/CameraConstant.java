package util;

public class CameraConstant
{
    private static float mCurrentPreviewWHSize;

    public @interface PreviewWHSize
    {
        float s_9_16 = (float) 9 / 16;
        float s_3_4 = (float) 3 / 4;
        float s_1_1 = (float) 1;
        float s_4_3 = (float) 4 / 3;
        float s_16_9 = (float) 16 / 9;
        float s_full_screen = (float) ShareData.m_screenRealWidth / ShareData.m_screenRealHeight;

        float[] s_all_size_arr = new float[]{
                s_9_16, s_3_4, s_1_1, s_4_3, s_16_9, s_full_screen
        };
    }

    public static float sGetDefaultPreviewWHSize()
    {
        sSetCurrentPreviewWHSize(PreviewWHSize.s_1_1);
        return PreviewWHSize.s_1_1;
    }

    public static void sSetCurrentPreviewWHSize(@PreviewWHSize float size)
    {
        mCurrentPreviewWHSize = size;
    }

    public static float sGetCurrentPreviewWHSize()
    {
        return mCurrentPreviewWHSize;
    }
}
