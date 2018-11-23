package gpu.filter;

import android.content.Context;
import android.opengl.GLES20;

import util.VaryTools;

/**
 * @author Gxx
 * Created by Gxx on 2018/11/22.
 */
public class DisplayFilter extends GPUImageFilter
{
    public DisplayFilter(Context context)
    {
        super(context);
    }

    @Override
    protected void onDrawArraysPre()
    {
        GLES20.glViewport(0, 0, getSurfaceW(), getSurfaceH());
        VaryTools matrix = getMatrix();
        matrix.pushMatrix();
        matrix.scale(1f, -1f, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }
}
