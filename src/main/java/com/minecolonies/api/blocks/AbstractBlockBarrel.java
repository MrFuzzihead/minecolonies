package com.minecolonies.api.blocks;
import net.minecraft.world.level.block.state.BlockState;

import com.minecolonies.api.blocks.interfaces.ITickableBlockMinecolonies;
import com.minecolonies.api.blocks.types.BarrelType;
import com.minecolonies.api.tileentities.AbstractTileEntityBarrel;
import net.minecraft.block.material.Material;

/**
 * Abstract barrel block — ported to 1.7.10.
 * There is no BlockState property system in 1.7.10.
 * FACING is encoded in metadata bits 0-1 by the parent class.
 * VARIANT (barrel fill World) is encoded in metadata bits 2-4.
 */
public abstract class AbstractBlockBarrel<B extends AbstractBlockBarrel<B>> extends AbstractBlockMinecoloniesHorizontal<B> implements ITickableBlockMinecolonies
{
    /**
     * Constructor.
     *
     * @param material the block material.
     */
    public AbstractBlockBarrel(final Material material)
    {
        super(material);
    }

    /**
     * Compute the new metadata value based on the barrel fullness.
     *
     * @param te          the tile entity.
     * @param currentMeta the current metadata.
     * @return updated metadata.
     */
    public static int changeMetaOverFullness(final AbstractTileEntityBarrel te, final int currentMeta)
    {
        BarrelType type = BarrelType.byMetadata((int) Math.round(te.getItems() / 12.8));

        if (type.equals(BarrelType.ZERO) && te.getItems() > 0)
        {
            type = BarrelType.TWENTY;
        }
        else if (te.getItems() == AbstractTileEntityBarrel.MAX_ITEMS)
        {
            type = BarrelType.WORKING;
        }
        if (te.isDone())
        {
            type = BarrelType.DONE;
        }

        // preserve facing in lower 2 bits, store variant in upper bits
        return (currentMeta & 0x3) | (type.getMetadata() << 2);
    }

    /**
     * Get the barrel type from metadata.
     *
     * @param meta the metadata.
     * @return the barrel type.
     */
    public static BarrelType getVariantFromMeta(final int meta)
    {
        return BarrelType.byMetadata((meta >> 2) & 0xF);
    }
}

