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

// [1.7.10 BACKPORT] DirectionalBlock does not exist in 1.7.10.
// Directional facing (all 6 directions) is encoded in block metadata bits 0-5:
//   0 = DOWN, 1 = UP, 2 = NORTH, 3 = SOUTH, 4 = WEST, 5 = EAST
// Subclasses must override getMetaFromBlock()/getBlockFromMeta() equivalents and
// use MathHelper / ForgeDirection to derive facing from the entity's pitch+yaw.
//
// [1.7.10 BACKPORT] Removed imports:
//   net.minecraft.world.level.block.DirectionalBlock  — no 1.7.10 equivalent
//   net.minecraftforge.registries.IForgeRegistry      — replaced by GameRegistry

public abstract class AbstractBlockMinecoloniesDirectional<B extends AbstractBlockMinecoloniesDirectional<B>> extends Block implements IBlockMinecolonies<B>
{
    // [1.7.10 BACKPORT] Metadata constants for the six facing directions.
    // These mirror the ordinal values of net.minecraftforge.common.util.ForgeDirection.
    public static final int META_DOWN  = 0;
    public static final int META_UP    = 1;
    public static final int META_NORTH = 2;
    public static final int META_SOUTH = 3;
    public static final int META_WEST  = 4;
    public static final int META_EAST  = 5;

    // [1.7.10 BACKPORT] In 1.21 this would have been:
    //   public static final DirectionProperty FACING = DirectionalBlock.FACING;
    // In 1.7.10 the facing is stored in metadata — see META_* constants above.

    public AbstractBlockMinecoloniesDirectional(final Material material)
    {
        super(material);
    }

    // [1.7.10 BACKPORT] Original constructor took Block.Properties:
    //   public AbstractBlockMinecoloniesDirectional(final Properties properties) { super(properties); }

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


