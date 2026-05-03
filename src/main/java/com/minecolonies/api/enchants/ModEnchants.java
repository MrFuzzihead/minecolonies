package com.minecolonies.api.enchants;

// [1.7.10 BACKPORT] DeferredRegister/RegistryObject/ForgeRegistries do not exist in 1.7.10.
import net.minecraft.enchantment.Enchantment;

/**
 * All mod enchants.
 */
public class ModEnchants
{
    private ModEnchants()
    {
        // Intentionally left empty
    }

    /**
     * Raider damage enchant, gives extra damage against raiders
     */
    public static Enchantment raiderDamage;
}
