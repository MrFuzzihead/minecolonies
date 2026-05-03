package net.minecraft.world.item;

/**
 * [1.7.10] Compatibility shim for 1.21 Item.Properties (item property builder).
 */
public class Properties
{
    public Properties() {}

    public Properties stacksTo(final int size)
    {
        return this;
    }

    public Properties tab(final Object tab)
    {
        return this;
    }

    public Properties durability(final int durability) { return this; }
    public Properties setNoRepair() { return this; }
    public Properties rarity(Rarity rarity) { return this; }
    public Properties fireResistant() { return this; }
    public Properties food(Object food) { return this; }
    public Properties craftRemainder(Object item) { return this; }
    public Properties defaultDurability(int durability) { return this; }
}

