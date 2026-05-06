package net.minecraft.world.level.storage.loot.entries;

import net.minecraft.item.Item;

/** [1.7.10 bridge] LootItem */
public class LootItem extends LootPoolSingletonContainer
{
    public static LootPoolSingletonContainer.Builder<?> lootTableItem(final Item item)
    {
        return new LootPoolSingletonContainer.Builder<LootPoolSingletonContainer.Builder<?>>() {};
    }
}

