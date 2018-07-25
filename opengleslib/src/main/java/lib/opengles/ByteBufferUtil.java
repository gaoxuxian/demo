package lib.opengles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ByteBufferUtil
{
    public static FloatBuffer getNativeFloatBuffer(float[] dataArr)
    {
        if (dataArr == null || dataArr.length <= 0) return null;

        return ByteBuffer.allocateDirect(dataArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(dataArr);
    }
}
