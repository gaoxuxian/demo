package util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class BufferUtil
{
    public static FloatBuffer getNativeFloatBuffer(float[] dataArr)
    {
        if (dataArr == null || dataArr.length <= 0) return null;

        FloatBuffer buffer = ByteBuffer.allocateDirect(dataArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(dataArr);
        buffer.position(0);
        return buffer;
    }

    public static ShortBuffer getNativeShortBuffer(short[] dataArr)
    {
        if (dataArr == null || dataArr.length <= 0) return null;

        ShortBuffer buffer = ByteBuffer.allocateDirect(dataArr.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer().put(dataArr);
        buffer.position(0);
        return buffer;
    }
}
