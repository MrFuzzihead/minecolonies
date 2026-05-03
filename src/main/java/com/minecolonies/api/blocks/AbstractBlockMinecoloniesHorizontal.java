package com.minecolonies.api.blocks;

import com.minecolonies.api.blocks.interfaces.IBlockMinecolonies;
// [1.7.10 BACKPORT] net.minecraft.block.Block replaces net.minecraft.world.level.block.Block
import net.minecraft.block.Block;
// [1.7.10 BACKPORT] net.minecraft.block.material.Material replaces Block.Properties
import net.minecraft.block.material.Material;
// [1.7.10 BACKPORT] ItemBlock replaces BlockItem
import net.minecraft.item.ItemBlock;
// [1.7.10 BACKPORT] 1.7.10 ResourceLocation
import net.minecraft.util.ResourceLocation;
// [1.7.10 BACKPORT] GameRegistry replaces IForgeRegistry
import cpw.mods.fml.common.registry.GameRegistry;

// [1.7.10 BACKPORT] HorizontalDirectionalBlock does not exist in 1.7.10.
// Horizontal facing (4 directions) is encoded in block metadata bits 0-3:
//   0 = SOUTH, 1 = WEST, 2 = NORTH, 3 = EAST
// This matches vanilla 1.7.10 horizontal-facing block convention (furnace, dispenser, etc.).
// Use BlockFaceShape/MathHelper utilities in subclasses to derive facing from player yaw.
//
// [1.7.10 BACKPORT] Removed imports:
//   net.minecraft.world.level.block.HorizontalDirectionalBlock — no 1.7.10 equivalent
//   net.minecraftforge.registries.IForgeRegistry               — replaced by GameRegistry

public abstract class AbstractBlockMinecoloniesHorizontal<B extends AbstractBlockMinecoloniesHorizontal<B>> extends Block implements IBlockMinecolonies<B>
{
    // [1.7.10 BACKPORT] Metadata constants for the four horizontal facing directions.
    public static final int META_SOUTH = 0;
    public static final int META_WEST  = 1;
    public static final int META_NORTH = 2;
    public static final int META_EAST  = 3;

    // [1.7.10 BACKPORT] In 1.21 this would have been:
    //   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    // In 1.7.10 the facing is stored in metadata — see META_* constants above.

    public AbstractBlockMinecoloniesHorizontal(final Material material)
    {
        super(material);
    }

    // [1.7.10 BACKPORT] Original constructor took Block.Properties:
    //   public AbstractBlockMinecoloniesHorizontal(final Properties properties) { super(properties); }

    /**
     * Computes the horizontal-facing metadata value (0-3) from a placer entity's yaw.
     * Uses the same convention as vanilla 1.7.10 facing blocks.
     *
     * @param yaw the entity's {@code rotationYaw} value.
     * @return metadata 0 (SOUTH), 1 (WEST), 2 (NORTH), or 3 (EAST).
     */
    public static int getMetaFromYaw(final float yaw)
    {
        // MathHelper equivalent: floor(yaw * 4 / 360 + 0.5) & 3
        return net.minecraft.util.MathHelper.floor_double(yaw * 4.0F / 360.0F + 0.5D) & 3;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B registerBlock()
    {
        GameRegistry.registerBlock(this, getItemClass(), getRegistryName().getPath());
        return (B) this;
    }

    protected Class<? extends ItemBlock> getItemClass()
    {
        return ItemBlock.class;
    }

    // registerBlockItem() — no-op, inherited from IBlockMinecolonies
}

