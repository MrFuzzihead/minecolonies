package net.minecraft.core;

/** 1.7.10 stub for net.minecraft.core.Direction (maps to ForgeDirection / EnumFacing). */
public enum Direction {
    DOWN, UP, NORTH, SOUTH, WEST, EAST;

    public Direction getOpposite() {
        switch (this) {
            case DOWN: return UP;
            case UP: return DOWN;
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case WEST: return EAST;
            case EAST: return WEST;
            default: return this;
        }
    }

    public int getStepX() {
        switch (this) { case WEST: return -1; case EAST: return 1; default: return 0; }
    }
    public int getStepY() {
        switch (this) { case DOWN: return -1; case UP: return 1; default: return 0; }
    }
    public int getStepZ() {
        switch (this) { case NORTH: return -1; case SOUTH: return 1; default: return 0; }
    }

    public String getName() { return name().toLowerCase(); }

    public static Direction[] values2d() { return new Direction[]{NORTH, SOUTH, WEST, EAST}; }
    public static Direction[] values3d() { return values(); }

    public static Direction fromNormal(int x, int y, int z) {
        if (x == 1) return EAST;
        if (x == -1) return WEST;
        if (y == 1) return UP;
        if (y == -1) return DOWN;
        if (z == 1) return SOUTH;
        if (z == -1) return NORTH;
        return NORTH;
    }

    public static Direction byName(String name) {
        for (Direction d : values()) if (d.name().equalsIgnoreCase(name)) return d;
        return null;
    }

    public Axis getAxis() {
        switch (this) {
            case DOWN: case UP: return Axis.Y;
            case NORTH: case SOUTH: return Axis.Z;
            default: return Axis.X;
        }
    }

    public enum Axis { X, Y, Z }
}

