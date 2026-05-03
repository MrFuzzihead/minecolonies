package com.minecolonies.api.util;

import com.minecolonies.api.util.constant.ColonyConstants;
import net.minecraft.util.Direction;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.minecolonies.api.util.constant.Constants.*;

/**
 * Utility methods for block positions (replacing int[] in 1.7.10).
 * Positions are represented as int[]{x, y, z} or separate int parameters.
 */
public final class BlockPosUtil
{
    /**
     * Max depth of the floor check to avoid endless void searching.
     */
    private static final int MAX_DEPTH = 50;

    /**
     * Amount of string required to try to calculate a int[].
     */
    private static final int BLOCKPOS_LENGTH = 3;

    private BlockPosUtil()
    {
        // Hide default constructor.
    }

    /**
     * Writes a position to an NBT compound, with a specific NBTBase name.
     *
     * @param compound Compound to write to.
     * @param name     Name of the NBTBase.
     * @param x        x coordinate.
     * @param y        y coordinate.
     * @param z        z coordinate.
     * @return the resulting compound.
     */
    public static NBTTagCompound write(@NotNull final NBTTagCompound compound, final String name, final int x, final int y, final int z)
    {
        @NotNull final NBTTagCompound coordsCompound = new NBTTagCompound();
        coordsCompound.setInteger("x", x);
        coordsCompound.setInteger("y", y);
        coordsCompound.setInteger("z", z);
        compound.setTag(name, coordsCompound);
        return compound;
    }

    /**
     * Reads a position from an NBT compound with a specific NBTBase name.
     *
     * @param compound Compound to read data from.
     * @param name     NBTBase name to read data from.
     * @return int[]{x, y, z} read from the compound.
     */
    @NotNull
    public static int[] read(@NotNull final NBTTagCompound compound, final String name)
    {
        final NBTTagCompound coordsCompound = compound.getCompoundTag(name);
        final int x = coordsCompound.getInteger("x");
        final int y = coordsCompound.getInteger("y");
        final int z = coordsCompound.getInteger("z");
        return new int[]{x, y, z};
    }

    /**
     * Reads a position from an NBT compound, returns null if zero or absent.
     *
     * @param compound Compound to read data from.
     * @param name     NBTBase name to read data from.
     * @return int[]{x, y, z} or null.
     */
    @Nullable
    public static int[] readOrNull(@NotNull final NBTTagCompound compound, @NotNull final String name)
    {
        if (!compound.hasKey(name))
        {
            return null;
        }
        final int[] result = read(compound, name);
        if (result[0] == 0 && result[1] == 0 && result[2] == 0)
        {
            return null;
        }
        return result;
    }

    /**
     * Write a compound with a position to a NBTBase list.
     *
     * @param tagList NBTBase list to write to.
     * @param x       x coordinate.
     * @param y       y coordinate.
     * @param z       z coordinate.
     */
    public static void writeToListNBT(@NotNull final NBTTagList tagList, final int x, final int y, final int z)
    {
        @NotNull final NBTTagCompound coordsCompound = new NBTTagCompound();
        coordsCompound.setInteger("x", x);
        coordsCompound.setInteger("y", y);
        coordsCompound.setInteger("z", z);
        tagList.appendTag(coordsCompound);
    }

    /**
     * Write a list of positions to a compound.
     *
     * @param compoundNBT compound to save the list on.
     * @param tagname     key to save the list on.
     * @param positions   block positions to write as [x, y, z] arrays.
     */
    public static void writePosListToNBT(final NBTTagCompound compoundNBT, final String tagname, final List<int[]> positions)
    {
        final NBTTagList listNBT = new NBTTagList();
        for (final int[] pos : positions)
        {
            writeToListNBT(listNBT, pos[0], pos[1], pos[2]);
        }
        compoundNBT.setTag(tagname, listNBT);
    }

    /**
     * Reads a list of block positions from NBT.
     *
     * @param compoundNBT compound the list is in.
     * @param tagname     key of the list.
     * @return list of block positions as [x, y, z] arrays.
     */
    public static List<int[]> readPosListFromNBT(final NBTTagCompound compoundNBT, final String tagname)
    {
        final List<int[]> result = new ArrayList<>();
        final NBTTagList listNBT = compoundNBT.getTagList(tagname, 10); // 10 = NBTTagCompound
        for (int i = 0; i < listNBT.tagCount(); i++)
        {
            result.add(readFromListNBT(listNBT, i));
        }
        return result;
    }

    /**
     * Reads a position from a NBTBase list.
     *
     * @param tagList NBTBase list to read from.
     * @param index   Index in the NBTBase list.
     * @return int[]{x, y, z}.
     */
    @NotNull
    public static int[] readFromListNBT(@NotNull final NBTTagList tagList, final int index)
    {
        final NBTTagCompound coordsCompound = tagList.getCompoundTagAt(index);
        final int x = coordsCompound.getInteger("x");
        final int y = coordsCompound.getInteger("y");
        final int z = coordsCompound.getInteger("z");
        return new int[]{x, y, z};
    }

    /**
     * Try to parse a position from an input string ("x y z").
     *
     * @param inputText the string to parse.
     * @return int[]{x, y, z} or null.
     */
    @Nullable
    public static int[] getBlockPosOfString(@NotNull final String inputText)
    {
        final String[] strings = inputText.split(" ");
        if (strings.length == BLOCKPOS_LENGTH)
        {
            try
            {
                final int x = Integer.parseInt(strings[0]);
                final int y = Integer.parseInt(strings[1]);
                final int z = Integer.parseInt(strings[2]);
                return new int[]{x, y, z};
            }
            catch (final NumberFormatException e)
            {
                // Expected for invalid input.
            }
        }
        return null;
    }

    /**
     * Returns a string representation of the block position.
     *
     * @param x x coordinate.
     * @param y y coordinate.
     * @param z z coordinate.
     * @return The string representation.
     */
    @NotNull
    public static String getString(final int x, final int y, final int z)
    {
        return "{x=" + x + ", y=" + y + ", z=" + z + "}";
    }

    /**
     * Finds a safe ground position above/below the given position.
     *
     * @param x     x coordinate.
     * @param y     y coordinate.
     * @param z     z coordinate.
     * @param world the world to search in.
     * @return int[]{x, y, z} safe position.
     */
    public static int[] findLand(final int x, int y, final int z, final World world)
    {
        int top = y;
        int bot = 0;
        int mid = y;
        int foundY = y;

        while (top >= bot)
        {
            final Block block = world.getBlock(x, mid, z);
            if (block == net.minecraft.init.Blocks.air && world.canBlockSeeTheSky(x, mid, z))
            {
                top = mid - 1;
                foundY = mid;
            }
            else
            {
                bot = mid + 1;
                foundY = mid;
            }
            mid = (bot + top) / 2;
        }

        if (world.getBlock(x, foundY, z) != net.minecraft.init.Blocks.air)
        {
            foundY += 1;
        }
        return new int[]{x, foundY, z};
    }

    /**
     * Gets a random position around a center position.
     *
     * @param centerX  center x.
     * @param centerY  center y.
     * @param centerZ  center z.
     * @param distance distance away.
     * @return int[]{x, y, z}.
     */
    public static int[] getRandomPosAround(final int centerX, final int centerY, final int centerZ, final int distance)
    {
        final double nx = (ColonyConstants.rand.nextDouble() - 0.5) * distance;
        final double ny = (ColonyConstants.rand.nextDouble() - 0.5) * distance;
        final double nz = (ColonyConstants.rand.nextDouble() - 0.5) * distance;
        final double len = Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len == 0)
        {
            return new int[]{centerX, centerY, centerZ};
        }
        return new int[]{
          (int) Math.round(centerX + nx / len * distance),
          (int) Math.round(centerY + ny / len * distance),
          (int) Math.round(centerZ + nz / len * distance)
        };
    }

    /**
     * Returns the tile entity at a specific position.
     *
     * @param world World the tile entity is in.
     * @param x     x coordinate.
     * @param y     y coordinate.
     * @param z     z coordinate.
     * @return Tile entity at the given coordinates.
     */
    public static TileEntity getTileEntity(@NotNull final World world, final int x, final int y, final int z)
    {
        return world.getTileEntity(x, y, z);
    }

    /**
     * Returns a list of drops from mining a specific block.
     *
     * @param world   World the block is in.
     * @param x       x coordinate.
     * @param y       y coordinate.
     * @param z       z coordinate.
     * @param fortune World of fortune.
     * @param stack   the tool.
     * @return List of drops.
     */
    public static List<ItemStack> getBlockDrops(@NotNull final World world, final int x, final int y, final int z, final int fortune, final ItemStack stack)
    {
        final Block block = world.getBlock(x, y, z);
        final int meta = world.getBlockMetadata(x, y, z);
        return block.getDrops(world, x, y, z, meta, fortune);
    }

    /**
     * Returns the block at a specific position.
     *
     * @param world  World the block is in.
     * @param x      x coordinate.
     * @param y      y coordinate.
     * @param z      z coordinate.
     * @return Block at the given coordinates.
     */
    public static Block getBlock(@NotNull final IBlockAccess world, final int x, final int y, final int z)
    {
        return world.getBlock(x, y, z);
    }

    /**
     * Returns the metadata of a block at a specific position.
     *
     * @param world  World the block is in.
     * @param x      x coordinate.
     * @param y      y coordinate.
     * @param z      z coordinate.
     * @return Metadata of the block.
     */
    public static int getBlockMeta(@NotNull final IBlockAccess world, final int x, final int y, final int z)
    {
        return world.getBlockMetadata(x, y, z);
    }

    /**
     * Sets a block in the world.
     *
     * @param world  World the block needs to be set in.
     * @param x      x coordinate.
     * @param y      y coordinate.
     * @param z      z coordinate.
     * @param block  Block to place.
     * @param meta   Metadata.
     * @param flag   Flag to set.
     * @return True if block is placed, otherwise false.
     */
    public static boolean setBlock(@NotNull final World world, final int x, final int y, final int z, final Block block, final int meta, final int flag)
    {
        return world.setBlock(x, y, z, block, meta, flag);
    }

    /**
     * Returns whether two positions are equal.
     */
    public static boolean equals(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2)
    {
        return x1 == x2 && y1 == y2 && z1 == z2;
    }

    /**
     * Returns the position from an entity as int[]{x, y, z}.
     */
    @NotNull
    public static int[] fromEntity(@NotNull final Entity entity)
    {
        return new int[]{(int) Math.floor(entity.posX), (int) Math.floor(entity.posY), (int) Math.floor(entity.posZ)};
    }

    /**
     * Returns a Direction corresponding to the given delta (primarily horizontal).
     *
     * @param dx x delta
     * @param dy y delta
     * @param dz z delta
     * @return closest cardinal Direction
     */
    @NotNull
    public static Direction directionFromDelta(final int dx, final int dy, final int dz)
    {
        if (Math.abs(dx) >= Math.abs(dz))
        {
            if (dx > 0) return Direction.EAST;
            if (dx < 0) return Direction.WEST;
        }
        else
        {
            if (dz > 0) return Direction.SOUTH;
            if (dz < 0) return Direction.NORTH;
        }
        if (dy > 0) return Direction.UP;
        if (dy < 0) return Direction.DOWN;
        return Direction.EAST;
    }

    // ────────────────────────────────────────────────────────────
    // Distance helpers — all using plain ints, no int[]
    // ────────────────────────────────────────────────────────────

    public static long getDistanceSquared(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2)
    {
        final long xDiff = (long) x1 - x2;
        final long yDiff = (long) y1 - y2;
        final long zDiff = (long) z1 - z2;
        return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
    }

    /**
     * Euclidean distance between two positions (as int[]).
     */
    public static double dist(final int[] a, final int[] b)
    {
        return Math.sqrt(getDistanceSquared(a[0], a[1], a[2], b[0], b[1], b[2]));
    }

    /**
     * Chebyshev (max) distance in XZ between two positions.
     */
    public static int dist2D(final int x1, final int z1, final int x2, final int z2)
    {
        return Math.max(Math.abs(x1 - x2), Math.abs(z1 - z2));
    }

    public static long getDistance2D(final int x1, final int z1, final int x2, final int z2)
    {
        return Math.abs((long) x1 - x2) + Math.abs((long) z1 - z2);
    }

    public static int getMaxDistance2D(final int x1, final int z1, final int x2, final int z2)
    {
        return Math.max(Math.abs(x1 - x2), Math.abs(z1 - z2));
    }

    public static double getDistance(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2)
    {
        final long xDiff = x1 - x2;
        final long yDiff = y1 - y2;
        final long zDiff = z1 - z2;
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
    }

    public static long getDistanceSquared2D(final int x1, final int z1, final int x2, final int z2)
    {
        final long xDiff = (long) x1 - x2;
        final long zDiff = (long) z1 - z2;
        return xDiff * xDiff + zDiff * zDiff;
    }

    public static int distManhattan(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2)
    {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2) + Math.abs(z1 - z2);
    }

    public static int distSqr(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2)
    {
        final int xDist = x1 - x2;
        final int yDist = y1 - y2;
        final int zDist = z1 - z2;
        return xDist * xDist + yDist * yDist + zDist * zDist;
    }

    public static double dist(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2)
    {
        return Math.sqrt(distSqr(x1, y1, z1, x2, y2, z2));
    }

    /**
     * Returns a chunk-aligned bounding box. In 1.7.10, returns int[]{minX, minY, minZ, maxX, maxY, maxZ}.
     */
    public static int[] getChunkAlignedBB(final int x, final int y, final int z, final int chunkRadius)
    {
        final int blockRadius = chunkRadius * 16;
        final int x1 = x & ~15;
        final int y1 = y & ~15;
        final int z1 = z & ~15;
        return new int[]{
          x1 - blockRadius, y1 - blockRadius, z1 - blockRadius,
          x1 + blockRadius + 15, y1 + blockRadius + 15, z1 + blockRadius + 15
        };
    }

    /**
     * Calculate facing direction index from delta (0=NORTH, 1=SOUTH, 2=WEST, 3=EAST, 4=DOWN, 5=UP).
     */
    public static int getXZFacing(final int pos1X, final int pos1Z, final int pos2X, final int pos2Z)
    {
        if (pos2X > pos1X) return 3; // EAST
        if (pos2X < pos1X) return 2; // WEST
        if (pos2Z < pos1Z) return 0; // NORTH
        if (pos2Z > pos1Z) return 1; // SOUTH
        return 5; // UP (no horizontal facing)
    }

    /**
     * Calculates the direction between two positions, returning a DirectionResult.
     *
     * @param from the source position.
     * @param to   the target position.
     * @return a {@link DirectionResult} describing the direction and distance.
     */
    public static DirectionResult calcDirection(final int[] from, final int[] to)
    {
        final int dx = to[0] - from[0];
        final int dz = to[2] - from[2];
        final double distance = Math.sqrt(dx * dx + dz * dz);
        final Direction dir = directionFromDelta(dx, 0, dz);
        return new DirectionResult(dir, (int) distance);
    }

    /**
     * Result record for {@link #calcDirection(int[], int[])}.
     */
    public static class DirectionResult
    {
        public final Direction direction;
        public final int distance;

        public DirectionResult(final Direction direction, final int distance)
        {
            this.direction = direction;
            this.distance = distance;
        }

        public Direction getDirection() { return direction; }
        public int getDistance() { return distance; }
    }
}

