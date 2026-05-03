package com.minecolonies.api.entity;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import org.jetbrains.annotations.NotNull;

/**
 * A simplified goal selector wrapper for 1.7.10.
 * Wraps the vanilla EntityAITasks to provide addGoal/removeGoal API
 * used by the MineColonies AI registry.
 */
public class CustomGoalSelector
{
    /**
     * The wrapped vanilla task system.
     */
    private final EntityAITasks tasks;

    /**
     * Create a new CustomGoalSelector wrapping the given EntityAITasks.
     *
     * @param tasks the entity's tasks to wrap.
     */
    public CustomGoalSelector(@NotNull final EntityAITasks tasks)
    {
        this.tasks = tasks;
    }

    /**
     * Add a task at the given priority.
     *
     * @param priority the priority (lower = higher priority).
     * @param task     the AI task to add.
     */
    public void addGoal(final int priority, final EntityAIBase task)
    {
        this.tasks.addTask(priority, task);
    }

    /**
     * Remove a task from the selector.
     *
     * @param task the task to remove.
     */
    public void removeGoal(final EntityAIBase task)
    {
        this.tasks.removeTask(task);
    }

    /**
     * Get the underlying EntityAITasks.
     *
     * @return the underlying tasks.
     */
    public EntityAITasks getTasks()
    {
        return tasks;
    }
}
