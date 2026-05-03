package com.minecolonies.api.items;

// [1.7.10 BACKPORT] NBTBase system (TagKey, BlockTags, ItemTags, etc.) does not exist in 1.7.10. Class stubbed.
// TODO: no 1.7.10 equivalent — use OreDictionary or manual block/item lists instead.

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NBTBase definitions for MineColonies.
 * Stubbed — the 1.21 TagKey system does not exist in 1.7.10.
 * Use OreDictionary entries or explicit block/item lists as replacements.
 */
public class ModTags
{
    /**
     * Flag to check if tags are already loaded.
     */
    public static boolean tagsLoaded = false;

    // [1.7.10 BACKPORT] All TagKey<Block>, TagKey<Item>, TagKey<EntityType<?>>, TagKey<Biome> fields commented out.
    // public static final TagKey<Block> decorationItems = ...
    // public static final TagKey<Block> pathingBlocks   = ...
    // ... (all NBTBase fields — see original 1.21 source for full list)

    // Crafter rule maps — kept but empty; populated via initCrafterRules() which is now a no-op.
    public static final Map<String, Object> crafterProduct              = new HashMap<>();
    public static final Map<String, Object> crafterProductExclusions    = new HashMap<>();
    public static final Map<String, Object> crafterIngredient           = new HashMap<>();
    public static final Map<String, Object> crafterIngredientExclusions = new HashMap<>();
    public static final Map<String, Object> crafterDoIngredient         = new HashMap<>();

    // Crop biome tags list — empty stub
    public static final List<Object> cropBiomeTags = List.of();

    /** No-op in 1.7.10 — tags are not registered via this method. */
    public static void init()
    {
        // TODO: populate crafter rule maps using OreDictionary lookups if needed
    }

    private ModTags()
    {
        throw new IllegalStateException("Can not instantiate an instance of: ModTags. This is a utility class");
    }
}

