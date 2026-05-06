package com.minecolonies.api.items;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.blocks.AbstractColonyBlock;
import com.minecolonies.api.colony.IColonyView;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_COLONY_ID;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_OTHER_LEVEL;

/**
 * A custom item class for hut blocks.
 *
 * [1.7.10 BACKPORT] Ported from BlockItem (1.21) to ItemBlock (1.7.10).
 * Tooltip API: appendHoverText → addInformation; String → String; EnumChatFormatting.
 */
@SuppressWarnings("unchecked")
public class ItemBlockHut extends ItemBlock
{
    /**
     * This items block.
     */
    private final AbstractColonyBlock<?> block;

    /**
     * Creates a new ItemBlockHut representing the item form of the given {@link AbstractBlockHut}.
     *
     * @param block the {@link AbstractColonyBlock} this item represents.
     */
    public ItemBlockHut(final AbstractColonyBlock<?> block)
    {
        super(block);
        this.block = block;
    }

    @Override
    public void addInformation(@NotNull final ItemStack stack,
                               final EntityPlayer player,
                               @NotNull final List tooltip,
                               final boolean advanced)
    {
        super.addInformation(stack, player, tooltip, advanced);

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_OTHER_LEVEL))
        {
            final int World = stack.getTagCompound().getInteger(TAG_OTHER_LEVEL);
            tooltip.add(EnumChatFormatting.GREEN + "item.minecolonies.hut.World: "
                + EnumChatFormatting.GOLD + World);
        }

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_COLONY_ID) && player != null)
        {
            final World world = player.worldObj;
            // [1.7.10 BACKPORT] dimension() → world.provider.dimensionId
            final IColonyView colony = IMinecoloniesAPI.getInstance()
                .getColonyManager()
                .getColonyView(stack.getTagCompound().getInteger(TAG_COLONY_ID), world.provider.dimensionId);
            final String name = colony != null ? colony.getName() : "item.minecolonies.hut.unknowncolony";
            tooltip.add(EnumChatFormatting.ITALIC + "item.minecolonies.hut.colony: " + name);
        }
    }
}

