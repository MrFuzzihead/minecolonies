package com.minecolonies.core.entity.ai.workers.util;

import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.util.PlacementSettings;
import com.minecolonies.api.util.LoadOnlyStructureHandler;
import com.minecolonies.core.colony.buildings.AbstractBuildingStructureBuilder;
import com.minecolonies.core.colony.jobs.AbstractJobStructure;
import com.minecolonies.core.entity.ai.workers.AbstractEntityAIStructure;
// [1.7.10] BlockState -> int metadata
import net.minecraft.world.level.block.state.BlockState;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Load only structure handler just to get dimensions etc from structures, not for placement specific for worker usage.
 */
public final class WorkerLoadOnlyStructureHandler<J extends AbstractJobStructure<?, J>, B extends AbstractBuildingStructureBuilder> extends LoadOnlyStructureHandler
{
    /**
     * The structure AI handling this task.
     */
    private final AbstractEntityAIStructure<J, B> structureAI;

    /**
     * The minecolonies specific worker load only structure placer.
     *
     * @param world          the world.
     * @param pos            the pos it is placed at.
     * @param blueprint      the blueprint.
     * @param settings       the placement settings.
     * @param fancyPlacement if fancy or complete.
     */
    public WorkerLoadOnlyStructureHandler(
      final World world, final int[] pos, final Blueprint blueprint, final PlacementSettings settings,
      final AbstractEntityAIStructure<J, B> entityAIStructure)
    {
        super(world, pos, blueprint, settings);
        this.structureAI = entityAIStructure;
    }

    @Override
    public BlockState getSolidBlockForPos(final int[] blockPos)
    {
        return structureAI.getSolidSubstitution(blockPos);
    }

    @Override
    public BlockState getSolidBlockForPos(final int[] worldPos, @Nullable final Function<int[], BlockState> virtualBlocks)
    {
        return structureAI.getSolidSubstitution(worldPos);
    }
}




