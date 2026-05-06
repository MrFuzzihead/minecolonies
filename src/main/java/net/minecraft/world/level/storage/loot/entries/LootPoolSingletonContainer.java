package net.minecraft.world.level.storage.loot.entries;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/** [1.7.10 bridge] LootPoolSingletonContainer */
public abstract class LootPoolSingletonContainer
{
    public abstract static class Builder<T extends Builder<T>>
    {
        public T apply(final Object function) { return (T) this; }
    }
}

