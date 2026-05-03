package net.minecraftforge.client;

/** [1.7.10 stub] IItemDecorator - decorates items in GUI */
public interface IItemDecorator
{
    /**
     * @return true if the decorator rendered something
     */
    boolean render(Object graphics, Object font, net.minecraft.item.ItemStack stack, int xOffset, int yOffset);
}

