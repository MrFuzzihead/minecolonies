package com.minecolonies.api.blocks;

import com.minecolonies.api.blocks.interfaces.IBlockMinecolonies;
// [1.7.10 BACKPORT] net.minecraft.world.level.block.Block -> net.minecraft.block.Block
import net.minecraft.block.Block;
// [1.7.10 BACKPORT] net.minecraft.block.material.Material replaces Block.Properties/MapColor
import net.minecraft.block.material.Material;
// [1.7.10 BACKPORT] net.minecraft.item.ItemBlock replaces net.minecraft.world.item.BlockItem
import net.minecraft.item.ItemBlock;
// [1.7.10 BACKPORT] net.minecraft.util.ResourceLocation (was net.minecraft.resources.ResourceLocation)
import net.minecraft.util.ResourceLocation;
// [1.7.10 BACKPORT] GameRegistry replaces IForgeRegistry
import cpw.mods.fml.common.registry.GameRegistry;

// [1.7.10 BACKPORT] Removed imports:
//   net.minecraft.world.item.BlockItem         — replaced by net.minecraft.item.ItemBlock
//   net.minecraft.world.item.Item              — not needed at this World
//   net.minecraftforge.registries.IForgeRegistry — replaced by GameRegistry

public abstract class AbstractBlockMinecolonies<B extends AbstractBlockMinecolonies<B>> extends Block implements IBlockMinecolonies<B>
{
    // [1.7.10 BACKPORT] Constructor now takes Material instead of Block.Properties.
    // Subclasses that previously used Properties.of().mapColor(...).strength(...) etc.
    // should instead call setHardness(), setResistance(), setStepSound() etc. in their
    // own constructors after calling super(material).
    public AbstractBlockMinecolonies(final Material material)
    {
        super(material);
    }

    // [1.7.10 BACKPORT] Previous constructor accepted Block.Properties:
    //   public AbstractBlockMinecolonies(final Properties properties) { super(properties); }
    // This is no longer needed; kept as a comment for reference.

    /**
     * Registers this block and its associated {@link ItemBlock} with the Forge
     * {@link GameRegistry}.
     *
     * <p>In 1.7.10, a block and its ItemBlock are registered together in a single call.
     * Subclasses that need a custom ItemBlock should override {@link #getItemClass()}.</p>
     */
    @Override
    @SuppressWarnings("unchecked")
    public B registerBlock()
    {
        GameRegistry.registerBlock(this, getItemClass(), getRegistryName().getResourcePath());
        return (B) this;
    }

    /**
     * Returns the {@link ItemBlock} class to use when registering this block.
     * Override in subclasses that require a custom ItemBlock (e.g. {@code ItemBlockHut}).
     *
     * @return the ItemBlock class, defaults to {@link ItemBlock}.
     */
    protected Class<? extends ItemBlock> getItemClass()
    {
        return ItemBlock.class;
    }

    // [1.7.10 BACKPORT] registerBlockItem() inherited as a no-op from IBlockMinecolonies.
    // In 1.21 this registered the ItemBlock separately via IForgeRegistry<Item>.
    // In 1.7.10, ItemBlock registration is handled in registerBlock() above.

    // [1.7.10 BACKPORT] getRegistryName() remains abstract here — concrete subclasses
    // (or AbstractColonyBlock) must implement it, just as in 1.21.
}

