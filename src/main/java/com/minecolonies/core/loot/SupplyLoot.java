package com.minecolonies.core.loot;

// [1.7.10 BACKPORT] Global loot modifiers / LootModifier do not exist in 1.7.10. Class stubbed.
// TODO: no 1.7.10 equivalent — supply camp/ship loot should be handled via IWorldGenerator chest filling.

import net.minecraft.util.ResourceLocation;

import static com.minecolonies.api.util.constant.Constants.MOD_ID;

/**
 * Helper class for supply camp loot.
 * Stubbed — loot modifiers are not supported in 1.7.10.
 */
public class SupplyLoot
{
    public static final ResourceLocation SUPPLY_CAMP_LT = new ResourceLocation(MOD_ID, "chests/supplycamp");
    public static final ResourceLocation SUPPLY_SHIP_LT = new ResourceLocation(MOD_ID, "chests/supplyship");

    private SupplyLoot() {}

    /** No-op in 1.7.10. */
    public static void init() { /* no-op */ }
}
