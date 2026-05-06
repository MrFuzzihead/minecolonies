package net.minecraft.world.item.enchantment;
/** [1.7.10 bridge] EnchantmentInstance */
public class EnchantmentInstance {
    public final Enchantment enchantment;
    public final int level;
    public EnchantmentInstance(Enchantment e, int level) { this.enchantment = e; this.level = level; }
}