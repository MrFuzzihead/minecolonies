package com.minecolonies.core.enchants;

// [1.7.10] net.minecraft.enchantment.Enchantment replaces 1.21 net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;

/**
 * Enchant for adding extra damage against raiders.
 * [1.7.10] Ported from 1.21 — uses 1.7.10 Enchantment constructor (id, weight, type).
 */
public class RaiderDamageEnchant extends Enchantment
{
    /**
     * Enchantment ID for raider damage (use an unused high ID).
     * [1.7.10] Enchantments are registered by ID in the static enchantmentsList array.
     */
    private static final int ENCHANT_ID = 220;

    public RaiderDamageEnchant()
    {
        super(ENCHANT_ID, 1, EnumEnchantmentType.weapon);
        this.setName("raiderDamage");
    }

    @Override
    public int getMinEnchantability(int level)
    {
        return 10;
    }

    @Override
    public int getMaxEnchantability(int level)
    {
        return 50;
    }

    @Override
    public int getMaxLevel()
    {
        return 2;
    }
}
