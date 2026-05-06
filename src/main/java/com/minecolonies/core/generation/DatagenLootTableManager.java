package com.minecolonies.core.generation;

import com.google.gson.Gson;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a LootTableManager that's populated on-demand during datagen, so that we
 * can look up other tables for {@link com.minecolonies.core.colony.crafting.LootTableAnalyzer}.
 */
public class DatagenLootTableManager extends LootDataManager
{
    private static final Gson GSON = Deserializers.createLootTableSerializer().create();
    private final ExistingFileHelper               existingFileHelper;
    private final Map<ResourceLocation, LootTable> tables = new HashMap<>();

    public DatagenLootTableManager(@NotNull final ExistingFileHelper existingFileHelper)
    {
        super();
        this.existingFileHelper = existingFileHelper;
    }

    @NotNull
    @Override
    public LootTable getLootTable(@NotNull final ResourceLocation location)
    {
        // [1.7.10] loot tables not supported; always return empty
        return LootTable.EMPTY;
    }
}
