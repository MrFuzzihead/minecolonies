package net.minecraft.data.loot;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import java.util.function.BiConsumer;

/** [1.7.10 bridge] LootTableSubProvider - no 1.7.10 equivalent */
@FunctionalInterface
public interface LootTableSubProvider
{
    void generate(BiConsumer<ResourceLocation, LootTable.Builder> builder);
}

