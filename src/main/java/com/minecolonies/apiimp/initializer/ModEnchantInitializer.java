package com.minecolonies.apiimp.initializer;

import com.minecolonies.api.enchants.ModEnchants;
import com.minecolonies.core.enchants.RaiderDamageEnchant;

/**
 * Enchants initializer.
 * [1.7.10] Enchantments are registered by their constructor (auto-registers via enchantmentsList).
 */
public class ModEnchantInitializer
{
    static
    {
        // [1.7.10] Enchantment constructor auto-registers; just instantiate and assign.
        ModEnchants.raiderDamage = new RaiderDamageEnchant();
    }

    /** Init this (triggers class loading). */
    public static void init()
    {
        // Class load.
    }
}
