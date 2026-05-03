package com.minecolonies.core.items;

import com.minecolonies.api.blocks.decorative.AbstractBlockGate;
import com.minecolonies.api.util.constant.TranslationConstants;
import net.minecraft.util.IChatComponent;
import net.minecraft.block.Block;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.item.ItemStack;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Item for gates
 */
public class ItemGate extends BlockItem
{
    public ItemGate(
      @NotNull final String name,
      final Block block,
      final Properties properties)
    {
        super(block, properties);
    }

    @Override
    public void appendHoverText(
      @NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<String> tooltip, @NotNull final TooltipFlag flagIn)
    {
        if (getBlock() instanceof final AbstractBlockGate gate)
        {
            final String guiHint2 = String.translatable(TranslationConstants.GATE_PLACEMENT_TOOLTIP,
                    gate.getMaxWidth(), gate.getMaxHeight(), gate.getMaxWidth() * gate.getMaxHeight());
            guiHint2.setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA));
            tooltip.add(guiHint2);
        }
    }
}



