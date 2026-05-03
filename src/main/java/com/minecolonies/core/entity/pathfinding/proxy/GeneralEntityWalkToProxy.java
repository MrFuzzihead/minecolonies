package com.minecolonies.core.entity.pathfinding.proxy;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
// [1.7.10] world.entity removed
// [1.7.10] int[] -> int x,y,z
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

/**
 * General walkToProxy for all entities.
 */
public class GeneralEntityWalkToProxy extends AbstractWalkToProxy
{

    /**
     * Creates a walkToProxy for a certain worker.
     *
     * @param entity the entity.
     */
    public GeneralEntityWalkToProxy(final EntityCreature entity)
    {
        super(entity);
    }

    @Override
    public Set<int[]> getWayPoints()
    {
        final EntityLivingBase living = getEntity();
        final int[] pos = living.blockPosition();
        final IColony colony = IColonyManager.getInstance().getClosestColony(living.getCommandSenderWorld(), pos);

        if (colony == null || !colony.isCoordInColony(living.getCommandSenderWorld(), pos))
        {
            return Collections.emptySet();
        }

        return colony.getWayPoints().keySet();
    }

    @Override
    public boolean careAboutY()
    {
        return false;
    }

    @Nullable
    @Override
    public int[] getSpecializedProxy(final int[] target, final double distanceToPath)
    {
        return null;
    }
}



