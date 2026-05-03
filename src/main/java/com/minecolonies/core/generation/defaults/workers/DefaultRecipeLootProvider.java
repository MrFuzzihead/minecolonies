package com.minecolonies.core.generation.defaults.workers;

import com.minecolonies.api.items.ModItems;
import com.minecolonies.core.generation.SimpleLootTableProvider;
// [1.7.10] data removed
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.item.Items;
// [1.7.10] world.World.storage removed
// [1.7.10] world.World.storage removed
// [1.7.10] world.World.storage removed
// [1.7.10] world.World.storage removed
// [1.7.10] world.World.storage removed
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.Constants.MOD_ID;

/**
 * Datagen for generic recipe loot.  (This could be done in the individual crafter gens, but they're potentially
 * useful across multiple, and there's not very many of them.)
 */
public class DefaultRecipeLootProvider extends SimpleLootTableProvider
{
    public static final ResourceLocation LOOT_TABLE_GLASS_BOTTLE = new ResourceLocation(MOD_ID, "recipes/glass_bottle");
    public static final ResourceLocation LOOT_TABLE_LARGE_BOTTLE = new ResourceLocation(MOD_ID, "recipes/large_bottle");
    public static final ResourceLocation LOOT_TABLE_GRAVEL = new ResourceLocation(MOD_ID, "recipes/gravel");

    public DefaultRecipeLootProvider(@NotNull final PackOutput packOutput)
    {
        super(packOutput);
    }

    @NotNull
    @Override
    public String getName()
    {
        return "DefaultRecipeLootProvider";
    }

    @Override
    protected void registerTables(@NotNull final LootTableRegistrar registrar)
    {
        registrar.register(LOOT_TABLE_GLASS_BOTTLE, LootContextParamSets.ALL_PARAMS, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(EmptyLootItem.emptyItem().setWeight(100).setQuality(-1))
                        .add(LootItem.lootTableItem(Items.GLASS_BOTTLE).setWeight(0).setQuality(1))));

        registrar.register(LOOT_TABLE_LARGE_BOTTLE, LootContextParamSets.ALL_PARAMS, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(EmptyLootItem.emptyItem().setWeight(100).setQuality(-1))
                        .add(LootItem.lootTableItem(ModItems.large_empty_bottle).setWeight(0).setQuality(1))));

        registrar.register(LOOT_TABLE_GRAVEL, LootContextParamSets.ALL_PARAMS, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(EmptyLootItem.emptyItem().setWeight(90))
                        .add(LootItem.lootTableItem(Items.FLINT).setWeight(10))));
    }
}




