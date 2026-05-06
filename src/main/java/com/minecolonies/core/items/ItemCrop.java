package com.minecolonies.core.items;

import com.minecolonies.api.util.constant.TranslationConstants;
import com.minecolonies.core.blocks.MinecoloniesCropBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A custom item class for crop blocks.
 * [1.7.10] Simplified from 1.21 version – biome tag/food properties removed.
 */
public class ItemCrop extends ItemBlock
{
    /**
     * Creates a new Crop item.
     *
     * @param cropBlock the {@link MinecoloniesCropBlock} this item represents.
     */
    public ItemCrop(@NotNull final MinecoloniesCropBlock cropBlock)
    {
        super(cropBlock);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(@NotNull final ItemStack stack,
                               final EntityPlayer player,
                               @NotNull final List tooltip,
                               final boolean advanced)
    {
        tooltip.add(EnumChatFormatting.GRAY + TranslationConstants.CROP_TOOLTIP);
        tooltip.add(EnumChatFormatting.DARK_AQUA + "" + EnumChatFormatting.ITALIC + TranslationConstants.CROP_TOOLTIP_HOE);
    }
}
