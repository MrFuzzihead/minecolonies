package net.minecraft.world.item;

/** [1.7.10 stub] EnchantedBookItem */
public class EnchantedBookItem extends net.minecraft.item.Item
{
    public EnchantedBookItem(Properties properties) {}

    public static net.minecraft.item.ItemStack getEnchantedBook(net.minecraft.world.item.enchantment.Enchantment ench, int level)
    {
        return new net.minecraft.item.ItemStack(net.minecraft.init.Items.enchanted_book);
    }
}

