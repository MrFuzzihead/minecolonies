package com.minecolonies.api.entity.mobs.registry;

import com.google.common.collect.Multimap;
import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.entity.ai.IStateAI;
import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
// [1.7.10] EntityAIBase replaced by EntityAIBase
import net.minecraft.entity.ai.EntityAIBase;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public interface IMobAIRegistry
{
    static IMobAIRegistry getInstance() { return IMinecoloniesAPI.getInstance().getMobAIRegistry(); }

    /**
     * Method to get the AI tasks registered for a given EntityCreature. Used by minecolonies to get the AIs that are required for a given EntityCreature.
     *
     * @param EntityCreature The EntityCreature that the system is initializing and requests the AI for.
     * @return The map with the entity AI tasks that are needed for the given EntityCreature, with their priorities.
     */
    @NotNull
    Multimap<Integer, EntityAIBase> getEntityAiTasksForMobs(final AbstractEntityMinecoloniesMonster EntityCreature);

    /**
     * Method used to register a entity AI task for a EntityCreature that matches the predicate.
     *
     * @param priority       The priority to register this task on.
     * @param aiTaskProducer The task producer in question to register.
     * @return The registry.
     */
    @NotNull
    default IMobAIRegistry registerNewAiTaskForMobs(final int priority, final Function<AbstractEntityMinecoloniesMonster, EntityAIBase> aiTaskProducer)
    {
        return this.registerNewAiTaskForMobs(priority, aiTaskProducer, EntityCreature -> true);
    }

    /**
     * Method used to register a entity AI task for a EntityCreature that matches the predicate.
     *
     * @param priority       The priority to register this task on.
     * @param aiTaskProducer The task producer in question to register.
     * @param applyPredicate The predicate used to indicate if the task should be applied to a given EntityCreature.
     * @return The registry.
     */
    @NotNull
    IMobAIRegistry registerNewAiTaskForMobs(
      final int priority,
      final Function<AbstractEntityMinecoloniesMonster, EntityAIBase> aiTaskProducer,
      Predicate<AbstractEntityMinecoloniesMonster> applyPredicate);

    /**
     * Method used to register a entity AI task for a EntityCreature that matches the predicate.
     *
     * @param aiTaskProducer The task producer in question to register.
     * @param applyPredicate The predicate used to indicate if the task should be applied to a given EntityCreature.
     * @return The registry.
     */
    @NotNull
    IMobAIRegistry registerNewStateAI(
      final Function<AbstractEntityMinecoloniesMonster, IStateAI> aiTaskProducer,
      Predicate<AbstractEntityMinecoloniesMonster> applyPredicate);

    /**
     * Applies the registered AI's to the given EntityCreature
     */
    @NotNull
    void applyToMob(AbstractEntityMinecoloniesMonster EntityCreature);

    /**
     * Method to get the AI target tasks registered for a given EntityCreature. Used by minecolonies to get the AIs that are required for a given EntityCreature.
     *
     * @param EntityCreature The EntityCreature that the system is initializing and requests the AI for.
     * @return The map with the entity AI tasks that are needed for the given EntityCreature, with their priorities.
     */
    @NotNull
    Multimap<Integer, EntityAIBase> getEntityAiTargetTasksForMobs(final AbstractEntityMinecoloniesMonster EntityCreature);

    /**
     * Method used to register a entity AI target task for a EntityCreature that matches the predicate.
     *
     * @param priority       The priority to register this task on.
     * @param aiTaskProducer The task producer in question to register.
     * @return The registry.
     */
    @NotNull
    default IMobAIRegistry registerNewAiTargetTaskForMobs(final int priority, final Function<AbstractEntityMinecoloniesMonster, EntityAIBase> aiTaskProducer)
    {
        return this.registerNewAiTargetTaskForMobs(priority, aiTaskProducer, EntityCreature -> true);
    }

    /**
     * Method used to register a entity AI target task for a EntityCreature that matches the predicate.
     *
     * @param priority       The priority to register this task on.
     * @param aiTaskProducer The task producer in question to register.
     * @param applyPredicate The predicate used to indicate if the task should be applied to a given EntityCreature.
     * @return The registry.
     */
    @NotNull
    IMobAIRegistry registerNewAiTargetTaskForMobs(
      final int priority,
      final Function<AbstractEntityMinecoloniesMonster, EntityAIBase> aiTaskProducer,
      Predicate<AbstractEntityMinecoloniesMonster> applyPredicate);
}


