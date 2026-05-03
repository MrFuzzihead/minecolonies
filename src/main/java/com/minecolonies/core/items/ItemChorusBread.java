package com.minecolonies.core.items;

import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.api.util.constant.TranslationConstants;
import com.minecolonies.core.util.TeleportHelper;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
// [1.7.10] food removed
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Chorus Bread, made by the baker. Teleports user to surface.
 */
public class ItemChorusBread extends ItemFood
{

    /**
     * Setup the food definition
     */
    private static FoodProperties chorusBread = (new FoodProperties.Builder())
                                        .nutrition(5)
                                        .saturationMod(2.0F)
                                        .alwaysEat()
                                        .build(); 

    /**
     * Sets the name, creative tab, and registers the Chorus Bread item.
     *
     * @param properties the properties.
     */
    public ItemChorusBread(final Properties properties)
    {
        super((new Item.Properties()).food(chorusBread), 2);
    }

   /**
    * Teleport to the surface. 
    */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        if (!worldIn.isClientSide && entityLiving instanceof EntityPlayerMP && WorldUtil.isOverworldType(worldIn))
        {
            TeleportHelper.surfaceTeleport((EntityPlayerMP)entityLiving);
        }

        return super.finishUsingItem(stack, worldIn, entityLiving);
    }

    @Override
    public void appendHoverText(
    @NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<String> tooltip, @NotNull final TooltipFlag flagIn)
    {
        final String guiHint = String.translatable(TranslationConstants.COM_MINECOLONIES_COREMOD_CHORUS_BREAD_TOOLTIP_GUI);
        guiHint.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
        tooltip.add(guiHint);

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}




