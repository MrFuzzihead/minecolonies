package com.minecolonies.api.blocks.decorative;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.blocks.interfaces.IBlockMinecolonies;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.core.tileentities.TileEntityColonyFlag;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Represents the common functions of both the wall and floor colony flag banner blocks
 */
public abstract class AbstractColonyFlagBanner<B extends AbstractColonyFlagBanner<B>> extends Block implements IBlockMinecolonies<AbstractColonyFlagBanner<B>>
{
    public static final String REGISTRY_NAME = "colony_banner";
    public static final String REGISTRY_NAME_WALL = "colony_wall_banner";

    public AbstractColonyFlagBanner()
    {
        super(Material.wood);
        this.setHardness(1.0F);
    }

    @Override
    public boolean hasTileEntity(final int metadata)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(final World world, final int metadata)
    {
        return new TileEntityColonyFlag();
    }

    @Override
    public void onBlockPlacedBy(final World world, final int x, final int y, final int z,
        final EntityLivingBase placer, final ItemStack stack)
    {
        if (world.isRemote) return;

        final TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityColonyFlag)
        {
            final TileEntityColonyFlag flagTe = (TileEntityColonyFlag) te;
            final int[] pos = new int[] {x, y, z};
            IColony colony = IColonyManager.getInstance().getIColony(world, pos);

            // Allow the player to place their own beyond the colony
            if (colony == null && placer instanceof EntityPlayer)
            {
                colony = IColonyManager.getInstance().getIColonyByOwner(world, (EntityPlayer) placer);
            }

            if (colony != null)
            {
                flagTe.colonyId = colony.getID();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public B registerBlock()
    {
        return (B) this;
    }
}
