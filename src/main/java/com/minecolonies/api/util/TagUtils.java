package com.minecolonies.api.util;
import net.minecraft.tags.TagKey;

import net.minecraft.util.ResourceLocation;
// [1.7.10] tags removed
// [1.7.10] tags removed
// [1.7.10] tags removed
import net.minecraft.item.Item;
import net.minecraft.block.Block;

/**
 * Class for specific minecraft NBTBase utilities.
 * [1.7.10] Tags don't exist; methods return null.
 */
public final class TagUtils
{
    private TagUtils()
    {
        throw new IllegalStateException("Tried to initialize: TagUtils but this is a Utility class.");
    }

    /**
     * Get a tag key for items.
     * [1.7.10] Tags not available; returns null.
     */
    public static Object getItem(final ResourceLocation resourceLocation)
    {
        // [1.7.10] TagKey<Item> not available
        return null;
    }

    /**
     * Get a tag key for blocks.
     * [1.7.10] Tags not available; returns null.
     */
    public static Object getBlock(final ResourceLocation resourceLocation)
    {
        // [1.7.10] TagKey<Block> not available
        return null;
    }
}
