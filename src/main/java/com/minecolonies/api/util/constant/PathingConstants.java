package com.minecolonies.api.util.constant;

// [1.7.10] int[] -> int x,y,z

/**
 * Pathing constants class.
 */
public final class PathingConstants
{
    //  Debug Output
    public static final int      DEBUG_VERBOSITY_NONE = 0;
    public static final int      DEBUG_VERBOSITY_FULL = 2;
    public static final int[] BLOCKPOS_IDENTITY    = new int[]{0, 0, 0};
    public static final int[] BLOCKPOS_UP          = new int[]{0, 1, 0};
    public static final int[] BLOCKPOS_DOWN        = new int[]{0, -1, 0};
    public static final int[] BLOCKPOS_NORTH       = new int[]{0, 0, -1};
    public static final int[] BLOCKPOS_SOUTH       = new int[]{0, 0, 1};
    public static final int[] BLOCKPOS_EAST        = new int[]{1, 0, 0};
    public static final int[] BLOCKPOS_WEST        = new int[]{-1, 0, 0};

    /**
     * Distance which is considered to be on one side of the fence/glasspane/wall etc.
     */
    public static final double ONE_SIDE = 0.25D;

    /**
     * Distance which is considered to be on the other side of the fence/glasspane/wall etc.
     */
    public static final double OTHER_SIDE = 0.75D;

    /**
     * Shift x by this value to calculate the node key..
     */
    public static final int SHIFT_X_BY = 20;

    /**
     * Shift the y value by this to calculate the node key..
     */
    public static final int SHIFT_Y_BY = 12;

    /**
     * Max jump height.
     */
    public static final double MAX_JUMP_HEIGHT = 1.3;

    /**
     * Half a block.
     */
    public static final double HALF_A_BLOCK = 0.5;

    /**
     * Private constructor to hide implicit one.
     */
    private PathingConstants()
    {
        /*
         * Intentionally left empty.
         */
    }
}


