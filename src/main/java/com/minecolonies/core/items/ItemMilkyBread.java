package com.minecolonies.core.items;

import com.minecolonies.api.util.constant.TranslationConstants;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.entity.EntityLivingBase;
// [1.7.10] food removed
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.Items;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Milk Bread item, made by the baker, with milk bucket effect
 */
public class ItemMilkyBread extends ItemFood
{

    /**
     * Setup the food definition
     */
    private static FoodProperties milkBread = (new FoodProperties.Builder())
                                        .nutrition(5)
                                        .saturationMod(0.6F)
                                        .build(); 

    /**
     * Sets the name, creative tab, and registers the Milk Bread item.
     *
     * @param properties the properties.
     */
    public ItemMilkyBread(final Properties properties)
    {
        super((new Item.Properties()).food(milkBread), 1);
    }

   /**
    * Remove the potion effects like Milk
    */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        
        if (!worldIn.isClientSide)
        {
            entityLiving.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
        }

        return super.finishUsingItem(stack, worldIn, entityLiving);
    }    

    @Override
    public void appendHoverText(
    @NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<String> tooltip, @NotNull final TooltipFlag flagIn)
    {
        final String guiHint = String.translatable(TranslationConstants.COM_MINECOLONIES_COREMOD_MILKY_BREAD_TOOLTIP_GUI);
        guiHint.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
        tooltip.add(guiHint);

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}




