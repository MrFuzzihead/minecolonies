package com.minecolonies.core.items;
import net.minecraft.world.item.Properties;

import net.minecraft.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.SwordItem;

/**
 * Class handling the Scimitar item.
 */
public class ItemIronScimitar extends SwordItem
{
    /**
     * Constructor method for the Scimitar Item
     *
     * @param properties the properties.
     */
    public ItemIronScimitar(final Properties properties)
    {
        super(Tiers.IRON, 3, -2.4f, properties);
    }
}

