package com.minecolonies.api.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
// [1.7.10] world.phys removed
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An interface to be implemented by items that want to render overlays while the EntityPlayer is holding the item.
 */
public interface IBlockOverlayItem
{
    /**
     * Called client-side only.
     * @return a list of overlay boxes that should be rendered for this item.
     */
    @NotNull
    List<OverlayBox> getOverlayBoxes(@NotNull final World world, @NotNull final EntityPlayer player, @NotNull final ItemStack stack);

    /**
     * Details about the overlay box to draw.
     * [1.7.10] record replaced with regular class; AABB replaced with AxisAlignedBB.
     */
    class OverlayBox
    {
        public final AxisAlignedBB bounds;
        public final int color;
        public final float width;
        public final boolean showThroughBlocks;

        public OverlayBox(final AxisAlignedBB bounds, final int color, final float width, final boolean showThroughBlocks)
        {
            this.bounds = bounds;
            this.color = color;
            this.width = width;
            this.showThroughBlocks = showThroughBlocks;
        }
    }
}
