package com.minecolonies.api.creativetab;

// [1.7.10 BACKPORT] Ported from 1.21 DeferredRegister<CreativeModeTab> to 1.7.10 CreativeTabs.
// Spawn-egg display removed — ForgeSpawnEggItem does not exist in 1.7.10.

import com.minecolonies.api.blocks.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/**
 * Class used to handle the creative tabs of MineColonies.
 */
public final class ModCreativeTabs
{
    /** Creative tab for all hut/building blocks. */
    public static final CreativeTabs HUTS = new CreativeTabs("minecolonies.huts")
    {
        @Override
        public ItemStack getIconItemStack()
        {
            return new ItemStack(ModBlocks.blockHutTownHall);
        }
    };

    /** Creative tab for general MineColonies items and blocks. */
    public static final CreativeTabs GENERAL = new CreativeTabs("minecolonies.general")
    {
        @Override
        public ItemStack getIconItemStack()
        {
            return new ItemStack(ModBlocks.blockRack);
        }
    };

    /** Creative tab for MineColonies food items and crop blocks. */
    public static final CreativeTabs FOOD = new CreativeTabs("minecolonies.food")
    {
        @Override
        public ItemStack getIconItemStack()
        {
            return new ItemStack(ModBlocks.blockTomato);
        }
    };

    private ModCreativeTabs()
    {
        /*
         * Intentionally left empty.
         */
    }
}
