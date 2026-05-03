package com.minecolonies.core.items;

import com.minecolonies.api.util.constant.TranslationConstants;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.entity.EntityLivingBase;
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

import static com.minecolonies.api.util.constant.Constants.STACKSIZE;

/**
 * Golden Bread, made by the Baker. Heals 2 hearts 
 */
public class ItemGoldenBread extends ItemFood
{

    /**
     * Setup the food definition
     */
    private static FoodProperties goldenBread = (new FoodProperties.Builder())
                                        .nutrition(5)
                                        .saturationMod(0.6F)
                                        .build(); 

    /**
     * Sets the name, creative tab, and registers the Golden Bread item.
     *
     * @param properties the properties.
     */
    public ItemGoldenBread(final Properties properties)
    {
        super((new Item.Properties()).food(goldenBread), 1);
    }

   /**
    * Heal 2 hearts
    */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        
        if (!worldIn.isClientSide)
        {
            entityLiving.heal(4);
        }

        return super.finishUsingItem(stack, worldIn, entityLiving);
    }    

    @Override
    public void appendHoverText(
    @NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<String> tooltip, @NotNull final TooltipFlag flagIn)
    {
        final String guiHint = String.translatable(TranslationConstants.COM_MINECOLONIES_COREMOD_GOLDEN_BREAD_TOOLTIP_GUI);
        guiHint.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
        tooltip.add(guiHint);

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}




