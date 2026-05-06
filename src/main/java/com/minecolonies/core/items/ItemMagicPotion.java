package com.minecolonies.core.items;
import net.minecraft.world.item.Properties;

/**
 * Class describing the magic potion item.
 */
public class ItemMagicPotion extends AbstractItemMinecolonies
{
    /**
     * Sets the name, creative tab, and registers the magic potion item.
     *
     * @param properties the properties.
     */
    public ItemMagicPotion(String name, Properties properties)
    {
        super(name, properties.stacksTo(16));
    }
}
