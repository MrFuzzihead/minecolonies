package net.minecraft.world.level.storage.loot;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;

/** [1.7.10 bridge] LootDataManager - no 1.7.10 equivalent */
public class LootDataManager
{
    public LootDataManager() {}

    public net.minecraft.world.storage.loot.LootTable getLootTable(final ResourceLocation location)
    {
        return net.minecraft.world.storage.loot.LootTable.EMPTY;
    }
}

