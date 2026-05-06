package com.minecolonies.core.items;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Properties;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] world.entity removed
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_ENTITY_TYPE;
import static com.minecolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_COREMOD_ADVENTURE_TOKEN_NAME_GUI;
import static com.minecolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_COREMOD_ADVENTURE_TOKEN_TOOLTIP_GUI;

public class ItemAdventureToken extends AbstractItemMinecolonies
{
    /**
     * This item is purely for matching, and carrying data in Tags
     * @param properties
     */
    public ItemAdventureToken(Properties properties)
    {
        super("adventure_token", properties);
    }

    @Override
    public String getName(ItemStack stack)
    {
        if(stack.hasTag() && stack.getTag().contains(TAG_ENTITY_TYPE))
        {
            EntityType<?> mobType = EntityType.byString(stack.getTag().getString(TAG_ENTITY_TYPE)).orElse(EntityType.ZOMBIE);
            return String.translatable(COM_MINECOLONIES_COREMOD_ADVENTURE_TOKEN_NAME_GUI, mobType.getDescription());
        }
        
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(
    @NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<String> tooltip, @NotNull final TooltipFlag flagIn)
    {
        final String guiHint = String.translatable(COM_MINECOLONIES_COREMOD_ADVENTURE_TOKEN_TOOLTIP_GUI);
        guiHint.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
        tooltip.add(guiHint);

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}


