package com.minecolonies.api.util;

import com.minecolonies.api.util.constant.ColonyConstants;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class to search for fisher ponds.
 * [1.7.10] Ported: removed MutableBlockPos/spiralAround/FluidTags/BlockState; uses simple int iteration and block checks.
 */
public final class Pond
{
    /*
     * Possible pond states, with SUBOPTIMAL introduced to recognize "flowing water" ponds
     * where it appears valid, but the water blocks below the surface are not source blocks.
     */
    public enum PondState {
        INVALID,
        SUBOPTIMAL,
        VALID
    }

    /**
     * The minimum pond requirements.
     */
    public static final int WATER_POOL_WIDTH_REQUIREMENT  = 5;
    public static final int WATER_DEPTH_REQUIREMENT       = 2;

    /**
     * Checks if on position "water" really is water, if the water is connected to land and if the pond is big enough (bigger then 20).
     *
     * @param world The world the player is in.
     * @param water The coordinate to check [x, y, z].
     * @param problematicPosition Will contain position of problematic block (if not null && pond was not found) — unused in 1.7.10.
     * @return the pond state.
     */
    public static PondState checkPond(@NotNull final IBlockAccess world, @NotNull final int[] water, @Nullable final int[] problematicPosition)
    {
        PondState worstPondState = PondState.VALID;
        final int radius = (WATER_POOL_WIDTH_REQUIREMENT - 1) / 2;

        for (int dx = -radius; dx <= radius; dx++)
        {
            for (int dz = -radius; dz <= radius; dz++)
            {
                final int cx = water[0] + dx;
                final int cz = water[2] + dz;

                for (int y = 0; y < WATER_DEPTH_REQUIREMENT; y++)
                {
                    final PondState pondState = checkWaterForFishing(world, new int[]{cx, water[1] - y, cz});

                    if (pondState == PondState.INVALID)
                    {
                        if (problematicPosition != null)
                        {
                            problematicPosition[0] = cx;
                            problematicPosition[1] = water[1] - y;
                            problematicPosition[2] = cz;
                        }
                        return PondState.INVALID;
                    }
                    else if (pondState == PondState.SUBOPTIMAL)
                    {
                        worstPondState = PondState.SUBOPTIMAL;
                    }

                    // 70% chance to check, to on avg prefer cleared areas
                    if (ColonyConstants.rand.nextInt(100) < 30)
                    {
                        break;
                    }
                }
            }
        }

        return worstPondState;
    }

    /**
     * Checks if the water is fine for fishing.
     *
     * @param world the block access
     * @param pos   int[]{x, y, z}
     * @return the pond state
     */
    public static PondState checkWaterForFishing(final IBlockAccess world, final int[] pos)
    {
        final Block block = world.getBlock(pos[0], pos[1], pos[2]);
        final int meta = world.getBlockMetadata(pos[0], pos[1], pos[2]);

        if (block == Blocks.water)
        {
            // meta 0 = source block; meta > 0 = flowing
            return (meta == 0) ? PondState.VALID : PondState.SUBOPTIMAL;
        }
        if (block == Blocks.flowing_water)
        {
            return PondState.SUBOPTIMAL;
        }
        return PondState.INVALID;
    }
}
