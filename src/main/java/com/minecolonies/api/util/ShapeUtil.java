package com.minecolonies.api.util;

// [1.7.10] VoxelShape, Shapes, Direction.Axis do not exist in 1.7.10
// All methods are stubbed with TODO

import net.minecraft.world.IBlockAccess;

/**
 * Utility methods for dealing with voxel shapes.
 * [1.7.10] VoxelShape API does not exist; methods are no-op stubs.
 */
public class ShapeUtil
{
    /**
     * [1.7.10] VoxelShape not available; always returns 1.0 (full block max).
     */
    public static double max(final Object shape, final Object axis)
    {
        // TODO: no 1.7.10 VoxelShape equivalent
        return 1.0;
    }

    /**
     * [1.7.10] VoxelShape not available; always returns 0.0 (full block min).
     */
    public static double min(final Object shape, final Object axis)
    {
        // TODO: no 1.7.10 VoxelShape equivalent
        return 0.0;
    }

    /**
     * [1.7.10] VoxelShape collision check not available; always returns false.
     */
    public static boolean hasCollision(final IBlockAccess world, final int[] pos, final Object blockState)
    {
        // TODO: no 1.7.10 VoxelShape equivalent
        return false;
    }

    /**
     * [1.7.10] VoxelShape not available; returns empty stub.
     */
    public static Object empty()
    {
        // TODO: no 1.7.10 VoxelShape equivalent
        return null;
    }

    /**
     * [1.7.10] VoxelShape not available; returns full block stub.
     */
    public static Object block()
    {
        // TODO: no 1.7.10 VoxelShape equivalent
        return null;
    }
}
