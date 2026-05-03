package com.minecolonies.core.blocks.schematic;

import com.minecolonies.api.blocks.AbstractBlockMinecolonies;
import com.minecolonies.api.util.constant.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

/**
 * This block is a waypoint, which makes citizens path to it.
 */
public class BlockWaypoint extends AbstractBlockMinecolonies<BlockWaypoint>
{
    /**
     * The hardness this block has.
     */
    private static final float BLOCK_HARDNESS = 0.0F;

    /**
     * This blocks name.
     */
    private static final String BLOCK_NAME = "blockwaypoint";

    /**
     * The resistance this block has.
     */
    private static final float RESISTANCE = 1F;

    /**
     * Constructor for the waypoint. Sets the creative tab, as well as the resistance and the hardness.
     */
    public BlockWaypoint()
    {
        super(Material.wood);
        setHardness(BLOCK_HARDNESS);
        setResistance(RESISTANCE);
        setStepSound(Block.soundTypeWood);
        setLightOpacity(0);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return new ResourceLocation(Constants.MOD_ID, BLOCK_NAME);
    }
}
