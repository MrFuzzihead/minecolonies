package com.minecolonies.api.compatibility.resourcefulbees;

// [1.7.10] int[] -> int x,y,z
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public interface IBeehiveCompat
{
    /**
     * Get comps from a hive at the given position
     *
     * @param pos    TE pos
     * @param world  world
     * @param amount comb amount
     * @return list of drops
     */
    default List<ItemStack> getCombsFromHive(int[] pos, World world, int amount)
    {
        // [1.7.10] HONEYCOMB does not exist in vanilla 1.7.10 – return empty list as default
        return new ArrayList<>();
    }
}


