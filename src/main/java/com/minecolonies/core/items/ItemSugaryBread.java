package com.minecolonies.core.items;

import com.minecolonies.api.util.constant.TranslationConstants;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.entity.EntityLivingBase;
// [1.7.10] food removed
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
// [1.7.10] effect removed
// [1.7.10] effect removed
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Sweet Bread, made by the baker. Adds speed, removes poison
 */
public class ItemSugaryBread extends ItemFood
{

    /**
     * Setup the food definition
     */
    private static FoodProperties sweetBread = (new FoodProperties.Builder())
                                        .nutrition(6)
                                        .saturationMod(0.7F)
                                        .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600), 1.0F)
                                        .build(); 

    /**
     * Sets the name, creative tab, and registers the Sweet Bread item.
     *
     * @param properties the properties.
     */
    public ItemSugaryBread(final Properties properties)
    {
        super((new Item.Properties()).food(sweetBread), 1);
    }

   /**
    * Remove the poison effect
    */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {

        if (!worldIn.isClientSide)
        {
            entityLiving.removeEffect(MobEffects.POISON);
        }
  
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }    
    
    @Override
    public void appendHoverText(
    @NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<String> tooltip, @NotNull final TooltipFlag flagIn)
    {
        final String guiHint = String.translatable(TranslationConstants.COM_MINECOLONIES_COREMOD_SUGARY_BREAD_TOOLTIP_GUI);
        guiHint.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
        tooltip.add(guiHint);

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}




