package net.minecraft.world.item;

import net.minecraft.item.Item;

/**
 * [1.7.10] Compatibility shim for 1.21 MapItem.
 * In 1.7.10, maps are net.minecraft.item.ItemMap.
 */
public class MapItem extends Item
{
    public MapItem()
    {
        super();
    }

    public static int createNewSavedData(final Object level, final int x, final int z,
                                          final int scale, final boolean trackingPosition,
                                          final boolean unlimitedTracking, final Object dimension)
    {
        return 0;
    }
}

