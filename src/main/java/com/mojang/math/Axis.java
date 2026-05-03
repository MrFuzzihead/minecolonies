package com.mojang.math;

/** [1.7.10 stub] Axis - rotation axis helper */
public class Axis
{
    public static final Axis XP = new Axis(1, 0, 0);
    public static final Axis XN = new Axis(-1, 0, 0);
    public static final Axis YP = new Axis(0, 1, 0);
    public static final Axis YN = new Axis(0, -1, 0);
    public static final Axis ZP = new Axis(0, 0, 1);
    public static final Axis ZN = new Axis(0, 0, -1);

    public final float x, y, z;

    public Axis(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Object rotation(float angle) { return null; }
    public Object rotationDegrees(float degrees) { return null; }
    public Axis normal() { return this; }
}

