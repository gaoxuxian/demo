package video;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/13.
 */
public class VideoBean
{
    public VideoFrame[] frame; // 帧

    public static class VideoFrame
    {
        public VideoFrameLayer[] layers; // 每一帧多少层
    }

    public static class VideoFrameLayer
    {
        public float[] vertexPots;
        public int intAlpha;
        public int frameType;
        public float scaleX = 1f;
        public float scaleY = 1f;
        public float rotate;
    }
}
