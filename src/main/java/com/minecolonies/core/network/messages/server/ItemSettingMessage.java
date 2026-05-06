package com.minecolonies.core.network.messages.server;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.items.ItemClipboard;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
// [1.7.10] int /* InteractionHand */ removed
import net.minecraft.item.ItemStack;

public class ItemSettingMessage implements IMessage
{
    public String settingName;
    public int settingValue;

    public void setSetting(String name, int value)
    {
        this.settingName = name;
        this.settingValue = value;
    }

    @Override
    public void toBytes(PacketBuffer buf)
    {
        buf.writeUtf(settingName);
        buf.writeInt(settingValue);
    }

    @Override
    public void fromBytes(PacketBuffer buf)
    {
        settingName = buf.readUtf(32767);
        settingValue = buf.readInt();
    }

    @Override
    public void onExecute(MessageContext ctx, boolean isLogicalServer)
    {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        
        if (player == null) return;

        ItemStack stack = player.getItemInHand(0 /* InteractionHand.MAIN_HAND */);

        if (stack == null || !(stack.getItem() instanceof ItemClipboard)) 
        {
            return;
        } 

        NBTTagCompound NBTBase = stack.getOrCreateTag();
        NBTBase.putInt(settingName, settingValue);
        stack.setTag(NBTBase);

        // Make sure inventories/menus notice the change
        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
    }
    
}





