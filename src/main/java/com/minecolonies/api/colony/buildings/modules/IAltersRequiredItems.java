package com.minecolonies.api.colony.buildings.modules;

import net.minecraft.item.ItemStack;
// [1.7.10] log4j TriConsumer not available, use custom functional interface

import java.util.function.Predicate;

/**
 * Module type to register specific blocks to a building (beds, workstations, etc).
 */
public interface IAltersRequiredItems extends IBuildingModule
{
    /**
     * Functional interface replacing org.apache.logging.log4j.util.TriConsumer.
     */
    @FunctionalInterface
    interface TriConsumer<A, B, C>
    {
        void accept(A a, B b, C c);
    }

    /**
     * Check if additional items have to be kept and add to map if necessary.
     * @param consumer consumer that adds items to it.
     */
    void alterItemsToBeKept(final TriConsumer<Predicate<ItemStack>, Integer, Boolean> consumer);
}

