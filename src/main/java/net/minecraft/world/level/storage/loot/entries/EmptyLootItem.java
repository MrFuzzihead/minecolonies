package net.minecraft.world.level.storage.loot.entries;

/** [1.7.10 bridge] EmptyLootItem */
public class EmptyLootItem extends LootPoolSingletonContainer
{
    public static LootPoolSingletonContainer.Builder<?> emptyItem()
    {
        return new LootPoolSingletonContainer.Builder<LootPoolSingletonContainer.Builder<?>>() {};
    }
}

