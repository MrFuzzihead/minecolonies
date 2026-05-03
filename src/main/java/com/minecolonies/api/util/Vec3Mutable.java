package com.minecolonies.api.util;

import net.minecraft.util.MathHelper;

/**
 * Helper class for storing a mutable vector.
 * [1.7.10] Ported: removed Vec3/Mth; uses MathHelper.floor_double; asVec3() replaced with double[].
 */
public class Vec3Mutable
{
    double x;
    double y;
    double z;

    public Vec3Mutable(final double x, final double y, final double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(final double x, final double y, final double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vec3Mutable createEmpty()
    {
        Vec3Mutable empty = new Vec3Mutable(0, 0, 0);
        empty.setEmpty();
        return empty;
    }

    public int[] asBlockPos()
    {
        return new int[]{MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z)};
    }

    /** [1.7.10] Returns [x, y, z] as double array (Vec3 removed) */
    public double[] asVec3()
    {
        return new double[]{x, y, z};
    }

    public boolean empty()
    {
        return x == Double.NEGATIVE_INFINITY && y == Double.NEGATIVE_INFINITY && z == Double.NEGATIVE_INFINITY;
    }

    public void setEmpty()
    {
        x = Double.NEGATIVE_INFINITY;
        y = Double.NEGATIVE_INFINITY;
        z = Double.NEGATIVE_INFINITY;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    public int getXi()
    {
        return MathHelper.floor_double(x);
    }

    public int getYi()
    {
        return MathHelper.floor_double(y);
    }

    public int getZi()
    {
        return MathHelper.floor_double(z);
    }

    public String toString()
    {
        return "{x:" + x + " y:" + y + " z:" + z + "}";
    }
}
