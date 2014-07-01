package io.endertech.util;

import net.minecraft.util.Vec3;

public class Geometry
{
    public static Vec3[] cubeVertices = new Vec3[8];
    public static Vec3[][] cubeFaces = new Vec3[6][4];

    public static final int RIGHT_BOTTOM_FRONT = 0;
    public static final int RIGHT_BOTTOM_BACK = 1;
    public static final int LEFT_BOTTOM_BACK = 2;
    public static final int LEFT_BOTTOM_FRONT = 3;
    public static final int RIGHT_TOP_FRONT = 4;
    public static final int RIGHT_TOP_BACK = 5;
    public static final int LEFT_TOP_BACK = 6;
    public static final int LEFT_TOP_FRONT = 7;

    public static final int FRONT_FACE = 0;
    public static final int BACK_FACE = 1;
    public static final int TOP_FACE = 2;
    public static final int BOTTOM_FACE = 3;
    public static final int RIGHT_FACE = 4;
    public static final int LEFT_FACE = 5;

    static
    {
        cubeVertices[RIGHT_BOTTOM_FRONT] = Vec3.createVectorHelper(+1, -1, -1); // Right bottom front
        cubeVertices[RIGHT_BOTTOM_BACK] = Vec3.createVectorHelper(+1, -1, +1); // Right bottom back
        cubeVertices[LEFT_BOTTOM_BACK] = Vec3.createVectorHelper(-1, -1, +1); // Left bottom back
        cubeVertices[LEFT_BOTTOM_FRONT] = Vec3.createVectorHelper(-1, -1, -1); // Left bottom front
        cubeVertices[RIGHT_TOP_FRONT] = Vec3.createVectorHelper(+1, +1, -1); // Right top front
        cubeVertices[RIGHT_TOP_BACK] = Vec3.createVectorHelper(+1, +1, +1); // Right top back
        cubeVertices[LEFT_TOP_BACK] = Vec3.createVectorHelper(-1, +1, +1); // Left top back
        cubeVertices[LEFT_TOP_FRONT] = Vec3.createVectorHelper(-1, +1, -1); // Left top front

        cubeFaces[FRONT_FACE] = new Vec3[] {cubeVertices[RIGHT_BOTTOM_FRONT], cubeVertices[RIGHT_BOTTOM_BACK], cubeVertices[LEFT_BOTTOM_BACK], cubeVertices[LEFT_BOTTOM_FRONT]};
        cubeFaces[BACK_FACE] = new Vec3[] {cubeVertices[RIGHT_TOP_BACK], cubeVertices[RIGHT_TOP_FRONT], cubeVertices[LEFT_TOP_FRONT], cubeVertices[LEFT_TOP_BACK]};
        cubeFaces[TOP_FACE] = new Vec3[] {cubeVertices[RIGHT_TOP_FRONT], cubeVertices[RIGHT_TOP_BACK], cubeVertices[RIGHT_BOTTOM_BACK], cubeVertices[RIGHT_BOTTOM_FRONT]};
        cubeFaces[BOTTOM_FACE] = new Vec3[] {cubeVertices[LEFT_TOP_BACK], cubeVertices[LEFT_TOP_FRONT], cubeVertices[LEFT_BOTTOM_FRONT], cubeVertices[LEFT_BOTTOM_BACK]};
        cubeFaces[RIGHT_FACE] = new Vec3[] {cubeVertices[RIGHT_BOTTOM_BACK], cubeVertices[RIGHT_TOP_BACK], cubeVertices[LEFT_TOP_BACK], cubeVertices[LEFT_BOTTOM_BACK]};
        cubeFaces[LEFT_FACE] = new Vec3[] {cubeVertices[RIGHT_BOTTOM_BACK], cubeVertices[RIGHT_BOTTOM_FRONT], cubeVertices[LEFT_BOTTOM_FRONT], cubeVertices[LEFT_TOP_FRONT]};
    }
}
