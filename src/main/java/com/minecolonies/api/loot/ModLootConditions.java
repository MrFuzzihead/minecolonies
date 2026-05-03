package com.minecolonies.api.loot;

// [1.7.10 BACKPORT] Loot condition types do not exist in 1.7.10. Class stubbed.
// TODO: no 1.7.10 equivalent — re-implement if custom loot is needed via IWorldGenerator or loot JSON fallback.

import net.minecraft.util.ResourceLocation;

import static com.minecolonies.api.util.constant.Constants.MOD_ID;

/** Stub — custom loot condition types are not supported in 1.7.10. */
public final class ModLootConditions
{
    public static final ResourceLocation ENTITY_IN_BIOME_TAG_ID = new ResourceLocation(MOD_ID, "entity_in_biome_tag");
    public static final ResourceLocation RESEARCH_UNLOCKED_ID   = new ResourceLocation(MOD_ID, "research_unlocked");

    public static void init() { /* no-op */ }

    private ModLootConditions()
    {
        throw new IllegalStateException("Tried to initialize: ModLootConditions but this is a Utility class.");
    }
}
