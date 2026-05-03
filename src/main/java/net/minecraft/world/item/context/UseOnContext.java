package net.minecraft.world.item.context;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * [1.7.10] Compatibility shim for 1.21 UseOnContext (item use on block context).
 */
public class UseOnContext
{
    private final World level;
    private final EntityPlayer player;
    private final ItemStack itemInHand;
    private final int[] clickedPos;
    private final EnumFacing clickedFace;

    public UseOnContext(final World level, final EntityPlayer player, final ItemStack itemInHand,
                        final int[] clickedPos, final EnumFacing clickedFace)
    {
        this.level = level;
        this.player = player;
        this.itemInHand = itemInHand;
        this.clickedPos = clickedPos;
        this.clickedFace = clickedFace;
    }

    public World getLevel()
    {
        return level;
    }

    public EntityPlayer getPlayer()
    {
        return player;
    }

    public ItemStack getItemInHand()
    {
        return itemInHand;
    }

    public ItemStack getItemInHand(final Object hand)
    {
        return itemInHand;
    }

    public Object getHand()
    {
        return null;
    }

    public int[] getClickedPos()
    {
        return clickedPos;
    }

    public EnumFacing getClickedFace()
    {
        return clickedFace;
    }
}

