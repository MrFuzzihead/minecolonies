package com.minecolonies.api.entity.pathfinding.registry;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.core.entity.pathfinding.navigation.AbstractAdvancedPathNavigate;
// [1.7.10] world.entity removed
import net.minecraft.entity.EntityCreature;

import java.util.function.Function;
import java.util.function.Predicate;

public interface IPathNavigateRegistry
{

    static IPathNavigateRegistry getInstance()
    {
        return IMinecoloniesAPI.getInstance().getPathNavigateRegistry();
    }

    IPathNavigateRegistry registerNewPathNavigate(Predicate<EntityCreature> selectionPredicate, Function<EntityCreature, AbstractAdvancedPathNavigate> navigateProducer);

    AbstractAdvancedPathNavigate getNavigateFor(EntityCreature entityLiving);
}


