package com.minecolonies.core.entity.mobs.registry;
import net.minecraft.world.entity.player.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.minecolonies.api.entity.ai.IStateAI;
import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesRaider;
import com.minecolonies.api.entity.mobs.IArcherMobEntity;
import com.minecolonies.api.entity.mobs.IRangedMobEntity;
import com.minecolonies.api.entity.mobs.drownedpirate.AbstractDrownedEntityPirateRaider;
import com.minecolonies.api.entity.mobs.registry.IMobAIRegistry;
import com.minecolonies.core.entity.ai.minimal.EntityAIInteractToggleAble;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import com.minecolonies.core.entity.mobs.aitasks.*;
import com.minecolonies.core.util.MultimapCollector;
import net.minecraft.entity.Entity;
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.minecolonies.core.colony.events.raid.RaiderConstants.*;
import static com.minecolonies.core.entity.ai.minimal.EntityAIInteractToggleAble.FENCE_TOGGLE;

public class MobAIRegistry implements IMobAIRegistry
{
    private final List<TaskInformationWrapper<AbstractEntityMinecoloniesMonster, Goal>>     mobAiTasks       = Lists.newArrayList();
    private final List<TaskInformationWrapper<AbstractEntityMinecoloniesMonster, Goal>>     mobAiTargetTasks = Lists.newArrayList();
    private final List<TaskInformationWrapper<AbstractEntityMinecoloniesMonster, IStateAI>> mobStateAITasks  = Lists.newArrayList();

    public MobAIRegistry()
    {
        setupMobAiTasks(this);
    }

    /**
     * Method setups the AI task logic for mobs. Replaces the old MobSpawnUtils.setAi(EntityCreature)
     *
     * @param registry The registry to register the AI tasks to.
     */
    private static void setupMobAiTasks(final IMobAIRegistry registry)
    {
        registry
          .registerNewAiTaskForMobs(PRIORITY_ZERO, FloatGoal::new, EntityCreature -> !(EntityCreature instanceof AbstractDrownedEntityPirateRaider))
          .registerNewAiTargetTaskForMobs(PRIORITY_THREE, EntityCreature -> new EntityAIInteractToggleAble(EntityCreature, FENCE_TOGGLE))
          .registerNewAiTargetTaskForMobs(PRIORITY_THREE, EntityCreature -> new EntityAIBreakDoor(EntityCreature))
          .registerNewAiTaskForMobs(PRIORITY_FIVE, EntityCreature -> new LookAtPlayerGoal(EntityCreature, Player.class, MAX_WATCH_DISTANCE))
          .registerNewAiTaskForMobs(PRIORITY_SIX, EntityCreature -> new LookAtPlayerGoal(EntityCreature, EntityCitizen.class, MAX_WATCH_DISTANCE))
          .registerNewStateAI(EntityCreature -> new RaiderMeleeAI<>(EntityCreature, EntityCreature.getAI()), EntityCreature -> !(EntityCreature instanceof IArcherMobEntity))
          .registerNewStateAI(EntityCreature -> new RaiderRangedAI(EntityCreature, EntityCreature.getAI()), EntityCreature -> EntityCreature instanceof IRangedMobEntity)
          .registerNewStateAI(EntityCreature -> new RaiderWalkAI((AbstractEntityMinecoloniesRaider) EntityCreature, EntityCreature.getAI()), EntityCreature -> EntityCreature instanceof AbstractEntityMinecoloniesRaider)
          .registerNewStateAI(EntityCreature -> new CampWalkAI(EntityCreature, EntityCreature.getAI()), EntityCreature -> !(EntityCreature instanceof AbstractEntityMinecoloniesRaider));
    }

    @NotNull
    @Override
    public Multimap<Integer, Goal> getEntityAiTasksForMobs(final AbstractEntityMinecoloniesMonster EntityCreature)
    {
        return mobAiTasks.stream().filter(wrapper -> wrapper.entityPredicate.test(EntityCreature)).collect(MultimapCollector.toMultimap(
          TaskInformationWrapper::getPriority,
          wrapper -> wrapper.getAiTaskProducer().apply(EntityCreature)
          )
        );
    }

    @NotNull
    @Override
    public IMobAIRegistry registerNewAiTaskForMobs(
      final int priority, final Function<AbstractEntityMinecoloniesMonster, Goal> aiTaskProducer, final Predicate<AbstractEntityMinecoloniesMonster> applyPredicate)
    {
        mobAiTasks.add(new TaskInformationWrapper<>(priority, aiTaskProducer, applyPredicate));
        return this;
    }

    @NotNull
    @Override
    public IMobAIRegistry registerNewStateAI(
      final Function<AbstractEntityMinecoloniesMonster, IStateAI> aiTaskProducer, final Predicate<AbstractEntityMinecoloniesMonster> applyPredicate)
    {
        mobStateAITasks.add(new TaskInformationWrapper<>(0, aiTaskProducer, applyPredicate));
        return this;
    }

    @NotNull
    @Override
    public void applyToMob(final AbstractEntityMinecoloniesMonster EntityCreature)
    {
        for (final TaskInformationWrapper<AbstractEntityMinecoloniesMonster, IStateAI> task : mobStateAITasks)
        {
            if (task.entityPredicate.test(EntityCreature))
            {
                task.aiTaskProducer.apply(EntityCreature);
            }
        }

        for (final TaskInformationWrapper<AbstractEntityMinecoloniesMonster, Goal> task : mobAiTargetTasks)
        {
            if (task.entityPredicate.test(EntityCreature))
            {
                EntityCreature.goalSelector.addGoal(task.priority, task.aiTaskProducer.apply(EntityCreature));
            }
        }

        for (final TaskInformationWrapper<AbstractEntityMinecoloniesMonster, Goal> task : mobAiTasks)
        {
            if (task.entityPredicate.test(EntityCreature))
            {
                EntityCreature.goalSelector.addGoal(task.priority, task.aiTaskProducer.apply(EntityCreature));
            }
        }
    }

    @NotNull
    @Override
    public Multimap<Integer, Goal> getEntityAiTargetTasksForMobs(final AbstractEntityMinecoloniesMonster EntityCreature)
    {
        return mobAiTargetTasks.stream().filter(wrapper -> wrapper.getEntityPredicate().test(EntityCreature)).collect(MultimapCollector.toMultimap(
          TaskInformationWrapper::getPriority,
          wrapper -> wrapper.getAiTaskProducer().apply(EntityCreature)
          )
        );
    }

    @NotNull
    @Override
    public IMobAIRegistry registerNewAiTargetTaskForMobs(
      final int priority, final Function<AbstractEntityMinecoloniesMonster, Goal> aiTaskProducer, final Predicate<AbstractEntityMinecoloniesMonster> applyPredicate)
    {
        mobAiTargetTasks.add(new TaskInformationWrapper<>(priority, aiTaskProducer, applyPredicate));
        return this;
    }

    /**
     * Class that holds registered AI task information.
     *
     * @param <M> The EntityCreature type.
     */
    private static final class TaskInformationWrapper<M extends Entity, G>
    {
        private final int                                            priority;
        private final Function<AbstractEntityMinecoloniesMonster, G> aiTaskProducer;
        private final Predicate<M>                                   entityPredicate;

        TaskInformationWrapper(
          final int priority,
          final Function<AbstractEntityMinecoloniesMonster, G> aiTaskProducer, final Predicate<M> entityPredicate)
        {
            this.priority = priority;
            this.aiTaskProducer = aiTaskProducer;
            this.entityPredicate = entityPredicate;
        }

        public int getPriority()
        {
            return priority;
        }

        public Function<AbstractEntityMinecoloniesMonster, G> getAiTaskProducer()
        {
            return aiTaskProducer;
        }

        public Predicate<M> getEntityPredicate()
        {
            return entityPredicate;
        }
    }
}



