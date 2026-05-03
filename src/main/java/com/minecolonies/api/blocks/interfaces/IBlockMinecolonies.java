package com.minecolonies.api.blocks.interfaces;

// [1.7.10 BACKPORT] net.minecraft.resources.ResourceLocation -> net.minecraft.util.ResourceLocation
import net.minecraft.util.ResourceLocation;

// [1.7.10 BACKPORT] Removed IForgeRegistry<Block> and IForgeRegistry<Item> - no equivalent in 1.7.10.
// Registration is now done via GameRegistry.registerBlock() inside registerBlock().
// Removed Item.Properties - no equivalent in 1.7.10; creative tab and other properties are set on the block/item directly.

public interface IBlockMinecolonies<B extends IBlockMinecolonies<B>>
{
    /**
     * Register this block (and its associated ItemBlock) with the game registry.
     * In 1.7.10, both the block and its ItemBlock are registered in a single
     * {@code GameRegistry.registerBlock()} call, so there is no separate item-registry step.
     *
     * @return the block itself (fluent API, matches original intent).
     */
    B registerBlock();

    /**
     * No-op in the 1.7.10 backport.
     *
     * <p>In 1.21, item registration was a separate event-driven step using
     * {@code IForgeRegistry<Item>}. In 1.7.10 the ItemBlock is registered together with
     * the block inside {@link #registerBlock()}, so this method is intentionally empty.
     * It is kept in the interface so that call-sites in {@code ModBlocksInitializer} do
     * not need to be deleted — they simply become no-ops.</p>
     */
    // TODO: [1.21 BACKPORT] Original signature:
    //   void registerBlockItem(IForgeRegistry<Item> registry, Item.Properties properties);
    default void registerBlockItem() {}

    /**
     * Returns the namespaced registry key for this block, e.g.
     * {@code minecolonies:blockHutTownHall}.
     *
     * @return the block's {@link ResourceLocation} registry name.
     */
    ResourceLocation getRegistryName();
}
