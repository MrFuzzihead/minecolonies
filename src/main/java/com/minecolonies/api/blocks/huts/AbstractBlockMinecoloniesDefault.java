package com.minecolonies.api.blocks.huts;

import com.minecolonies.api.blocks.AbstractBlockMinecoloniesContainer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import cpw.mods.fml.common.registry.GameRegistry;

// [1.7.10 BACKPORT] Removed HorizontalDirectionalBlock, DirectionProperty, IForgeRegistry, BlockItem

public abstract class AbstractBlockMinecoloniesDefault<B extends AbstractBlockMinecoloniesDefault<B>> extends AbstractBlockMinecoloniesContainer<B>
{
    // [1.7.10] FACING stored via metadata
    /** Hardness of the block. */
    public static final float  HARDNESS         = 10F;
    /** Resistance of the block. */
    public static final float  RESISTANCE       = 10F;
    /** Start of the collision box at y. */
    public static final double BOTTOM_COLLISION = 0.0;
    /** Start of the collision box at x and z. */
    public static final double START_COLLISION  = 0.1;
    /** End of the collision box. */
    public static final double END_COLLISION    = 0.9;
    /** Height of the collision box. */
    public static final double HEIGHT_COLLISION = 2.2;
    /** Registry name for this block. */
    public static final String REGISTRY_NAME    = "blockhutfield";

    public AbstractBlockMinecoloniesDefault(final Material material)
    {
        super(material);
        this.setHardness(HARDNESS);
        this.setResistance(RESISTANCE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public B registerBlock(final Object registry)
    {
        // [1.7.10] registration handled via GameRegistry elsewhere
        return (B) this;
    }
}
