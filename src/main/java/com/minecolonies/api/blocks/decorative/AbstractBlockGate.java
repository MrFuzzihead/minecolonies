package com.minecolonies.api.blocks.decorative;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.blocks.AbstractBlockMinecolonies;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Abstract gate block — stubbed for 1.7.10 backport.
 * TODO: Port gate logic (multi-block door) to 1.7.10 API.
 *       Original used DoorBlock + BlockState properties; 1.7.10 requires manual metadata
 *       tracking and multi-block management via TileEntity or alternate approach.
 */
public abstract class AbstractBlockGate extends AbstractBlockMinecolonies<AbstractBlockGate>
{
    public static final String IRON_GATE   = "gate_iron";
    public static final String WOODEN_GATE = "gate_wood";

    private final int maxWidth;
    private final int maxHeight;
    private final float hardness;

    protected AbstractBlockGate(final int maxWidth, final int maxHeight, final float hardness)
    {
        super(Material.wood);
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.hardness = hardness;
        setHardness(hardness);
    }

    @Override
    public boolean onBlockActivated(final World world, final int x, final int y, final int z,
        final EntityPlayer player, final int side, final float hitX, final float hitY, final float hitZ)
    {
        // TODO: implement gate toggle in 1.7.10
        return false;
    }

    public int getMaxWidth() { return maxWidth; }

    public int getMaxHeight() { return maxHeight; }
}
