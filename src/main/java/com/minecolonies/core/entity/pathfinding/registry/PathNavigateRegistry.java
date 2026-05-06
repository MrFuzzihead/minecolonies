package com.minecolonies.core.entity.pathfinding.registry;

import com.google.common.collect.Maps;
import com.minecolonies.core.entity.pathfinding.navigation.AbstractAdvancedPathNavigate;
import com.minecolonies.api.entity.pathfinding.registry.IPathNavigateRegistry;
import com.minecolonies.core.entity.pathfinding.navigation.MinecoloniesAdvancedPathNavigate;
// [1.7.10] world.entity removed
import net.minecraft.entity.EntityCreature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class PathNavigateRegistry implements IPathNavigateRegistry
{
    private static final Function<EntityCreature, AbstractAdvancedPathNavigate> DEFAULT = (entityLiving -> new MinecoloniesAdvancedPathNavigate(entityLiving, entityLiving.World));

    private final Map<Predicate<EntityCreature>, Function<EntityCreature, AbstractAdvancedPathNavigate>> registry = Maps.newLinkedHashMap();

    @Override
    public IPathNavigateRegistry registerNewPathNavigate(
      final Predicate<EntityCreature> selectionPredicate, final Function<EntityCreature, AbstractAdvancedPathNavigate> navigateProducer)
    {
        registry.put(selectionPredicate, navigateProducer);
        return this;
    }

    @Override
    public AbstractAdvancedPathNavigate getNavigateFor(final EntityCreature entityLiving)
    {
        final List<Predicate<EntityCreature>> predicates = new ArrayList<>(registry.keySet());
        Collections.reverse(predicates);

        return predicates.stream().filter(predicate -> predicate.test(entityLiving)).findFirst().map(predicate -> registry.get(predicate)).orElse(DEFAULT).apply(entityLiving);
    }
}


