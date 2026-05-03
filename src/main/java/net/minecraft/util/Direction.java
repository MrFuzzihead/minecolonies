package net.minecraft.util;

/**
 * [1.7.10] Shim for 1.21 Direction enum.
 * In 1.7.10 the equivalent is EnumFacing.
 */
public enum Direction
{
    DOWN, UP, NORTH, SOUTH, WEST, EAST;

    public int get2DDataValue()
    {
        switch (this)
        {
            case SOUTH: return 0;
            case WEST:  return 1;
            case NORTH: return 2;
            case EAST:  return 3;
            default:    return -1;
        }
    }

    public int ordinal3D()
    {
        return ordinal();
    }

    public static Direction from2DDataValue(final int value)
    {
        switch (value & 3)
        {
            case 0: return SOUTH;
            case 1: return WEST;
            case 2: return NORTH;
            case 3: return EAST;
            default: return NORTH;
        }
    }

    public Direction getOpposite()
    {
        switch (this)
        {
            case DOWN:  return UP;
            case UP:    return DOWN;
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case WEST:  return EAST;
            case EAST:  return WEST;
            default:    return this;
        }
    }

    public Direction getClockWise()
    {
        switch (this)
        {
            case NORTH: return EAST;
            case EAST:  return SOUTH;
            case SOUTH: return WEST;
            case WEST:  return NORTH;
            default:    return this;
        }
    }
}


