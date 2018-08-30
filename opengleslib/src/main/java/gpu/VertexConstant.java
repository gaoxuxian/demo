package gpu;

public @interface VertexConstant
{
    float[] CUBE = {
            -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f
    };

    float[] TEXTURE = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f
    };

    short[] CUBE_INDEX = {
            0, 1, 2,
            0, 2, 3
    };
}
