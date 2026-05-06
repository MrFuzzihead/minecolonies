package net.minecraftforge.client.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** [1.7.10 bridge] PlayerInteractEvent - wraps 1.7.10 PlayerInteractEvent with 1.12+ inner class API */
public class PlayerInteractEvent
{
    public final EntityPlayer entityPlayer;
    public final World world;

    public PlayerInteractEvent(EntityPlayer player, World world)
    {
        this.entityPlayer = player;
        this.world = world;
    }

    public EntityPlayer getEntity() { return entityPlayer; }

    public static class RightClickItem extends PlayerInteractEvent
    {
        private final ItemStack hand;

        public RightClickItem(EntityPlayer player, World world, ItemStack hand)
        {
            super(player, world);
            this.hand = hand;
        }

        public ItemStack getItemStack() { return hand; }
    }
}
