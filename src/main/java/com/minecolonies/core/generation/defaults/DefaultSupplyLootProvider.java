package com.minecolonies.core.generation.defaults;

import com.minecolonies.api.items.ModItems;
import com.minecolonies.core.generation.SimpleLootTableProvider;
// [1.7.10] data removed
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
// [1.7.10] world.World.storage removed
// [1.7.10] world.World.storage removed
// [1.7.10] world.World.storage removed
// [1.7.10] world.World.storage removed
// [1.7.10] world.World.storage removed
// [1.7.10] world.World.storage removed
// [1.7.10] world.World.storage removed
// [1.7.10] world.World.storage removed
// [1.7.10] world.World.storage removed
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.Constants.*;

/**
 * Loot table generator for supply camp/ship
 */
public class DefaultSupplyLootProvider extends SimpleLootTableProvider
{
    public DefaultSupplyLootProvider(@NotNull PackOutput packOutput)
    {
        super(packOutput);
    }

    @NotNull
    @Override
    public String getName()
    {
        return "Supplies Loot Table Provider";
    }

    @Override
    protected void registerTables(@NotNull final LootTableRegistrar registrar)
    {
        final NBTTagCompound instantTag = new NBTTagCompound();
        instantTag.putString(PLACEMENT_NBT, INSTANT_PLACEMENT);

        registrar.register(new ResourceLocation(MOD_ID, "chests/supplycamp"), LootContextParamSets.CHEST,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(ModItems.supplyCamp)
                                    .when(LootItemRandomChanceCondition.randomChance(0.01f))
                                        .apply(SetNbtFunction.setTag(instantTag))
                                        .apply(SetNameFunction.setName(String.translatable("item.minecolonies.supply.free", ModItems.supplyCamp.getDescription()))))
                                .add(LootItem.lootTableItem(ModItems.scrollBuff)
                                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4))))
                        ));

        registrar.register(new ResourceLocation(MOD_ID, "chests/supplyship"), LootContextParamSets.CHEST,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(ModItems.supplyChest)
                                    .when(LootItemRandomChanceCondition.randomChance(0.01f))
                                        .apply(SetNbtFunction.setTag(instantTag))
                                        .apply(SetNameFunction.setName(String.translatable("item.minecolonies.supply.free", ModItems.supplyChest.getDescription()))))
                                .add(LootItem.lootTableItem(ModItems.scrollBuff)
                                    .when(LootItemRandomChanceCondition.randomChance(0.1f))
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4))))
                        ));
    }
}





