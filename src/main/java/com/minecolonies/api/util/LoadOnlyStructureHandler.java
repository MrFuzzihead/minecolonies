package com.minecolonies.api.util;
import net.minecraft.world.level.block.state.BlockState;

import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.placement.structure.CreativeStructureHandler;
import com.ldtteam.structurize.util.PlacementSettings;
import com.minecolonies.api.blocks.ModBlocks;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] tags removed
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Load only structure handler just to get dimensions etc from structures, not for placement.
 */
public class LoadOnlyStructureHandler extends CreativeStructureHandler
{
    /**
     * The minecolonies specific creative structure placer.
     *
     * @param world          the world.
     * @param pos            the pos it is placed at.
     * @param blueprintFuture  the future of the structure.
     * @param settings       the placement settings.
     * @param fancyPlacement if fancy or complete.
     */
    public LoadOnlyStructureHandler(final World world, final int[] pos, final Future<Blueprint> blueprintFuture, final PlacementSettings settings)
    {
        super(world, pos, blueprintFuture, settings, true);
    }

    /**
     * The minecolonies specific creative structure placer.
     *
     * @param world          the world.
     * @param pos            the pos it is placed at.
     * @param blueprint      the blueprint.
     * @param settings       the placement settings.
     * @param fancyPlacement if fancy or complete.
     */
    public LoadOnlyStructureHandler(final World world, final int[] pos, final Blueprint blueprint, final PlacementSettings settings)
    {
        super(world, pos, blueprint, settings, true);
    }

    @Override
    public void triggerSuccess(final int[] pos, final List<ItemStack> list, final boolean placement)
    {
        // DO nothing
    }

    @Override
    public boolean isCreative()
    {
        return false;
    }

    @Override
    public boolean isStackFree(@Nullable final ItemStack itemStack)
    {
        return itemStack == null
                 || itemStack.isEmpty()
                 || itemStack.is(ItemTags.LEAVES)
                 || itemStack.getItem() == new ItemStack(ModBlocks.blockDecorationPlaceholder, 1).getItem();
    }
}



