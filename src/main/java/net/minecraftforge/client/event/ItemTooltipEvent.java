package net.minecraftforge.client.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

/** [1.7.10 bridge] ItemTooltipEvent - maps to net.minecraftforge.event.entity.player.ItemTooltipEvent */
public class ItemTooltipEvent
{
    public final EntityPlayer entityPlayer;
    public final ItemStack itemStack;
    public final List<String> toolTip;

    public ItemTooltipEvent(EntityPlayer player, ItemStack stack, List<String> toolTip)
    {
        this.entityPlayer = player;
        this.itemStack = stack;
        this.toolTip = toolTip;
    }

    public ItemStack getItemStack() { return itemStack; }
    public List<String> getToolTip() { return toolTip; }
}
