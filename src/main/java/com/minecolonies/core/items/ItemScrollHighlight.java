package com.minecolonies.core.items;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.InteractionResult;
import net.minecraft.world.item.Properties;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import com.minecolonies.api.util.SoundUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
// [1.7.10] effect removed
// [1.7.10] effect removed
// [1.7.10] block.entity removed
// [1.7.10] InteractionResult -> boolean
// [1.7.10] sounds removed

import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

import static com.minecolonies.api.util.constant.Constants.TICKS_SECOND;
import static com.minecolonies.api.util.constant.translation.ToolTranslationConstants.TOOL_GENERIC_SCROLL_HIGHLIGHT_DESCRIPTION;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText

/**
 * Magic scroll which highlights and speedbuffs workers of the building it is applied to
 */
public class ItemScrollHighlight extends AbstractItemScroll
{
    /**
     * Sets the name, creative tab, and registers the item.
     *
     * @param properties the properties.
     */
    public ItemScrollHighlight(final Properties properties)
    {
        super("scroll_highlight", properties);
    }

    @Override
    @NotNull
    public InteractionResult useOn(UseOnContext ctx)
    {
        // Right click on block
        if (ctx.getLevel().isClientSide || ctx.getPlayer() == null || !ctx.getPlayer().isShiftKeyDown())
        {
            return InteractionResult.PASS;
        }

        final BlockEntity te = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
        if (te instanceof TileEntityColonyBuilding)
        {
            ctx.getItemInHand().shrink(1);

            if (ctx.getLevel().random.nextInt(10) == 0)
            {
                ctx.getPlayer()
                  .displayClientMessage(String.translatable(
                    "minecolonies.scroll.failed" + (ctx.getLevel().random.nextInt(FAIL_RESPONSES_TOTAL) + 1)).setStyle(Style.EMPTY.withColor(
                    ChatFormatting.GOLD)), true);
                ctx.getPlayer().addEffect(new MobEffectInstance(MobEffects.GLOWING, TICKS_SECOND * 300));
                SoundUtils.playSoundForPlayer((EntityPlayerMP) ctx.getPlayer(), SoundEvents.ENDER_CHEST_OPEN, 0.3f, 1.0f);
                return InteractionResult.SUCCESS;
            }

            final TileEntityColonyBuilding building = (TileEntityColonyBuilding) te;
            final Set<ICitizenData> citizens = building.getColony().getServerBuildingManager().getBuilding(ctx.getClickedPos()).getAllAssignedCitizen();

            for (final ICitizenData citizenData : citizens)
            {
                if (citizenData.getEntity().isPresent())
                {
                    citizenData.getEntity().get().addEffect(new MobEffectInstance(MobEffects.GLOWING, TICKS_SECOND * 120));
                    citizenData.getEntity().get().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, TICKS_SECOND * 120));
                }
            }

            SoundUtils.playSoundForPlayer((EntityPlayerMP) ctx.getPlayer(), SoundEvents.PLAYER_LEVELUP, 0.3f, 1.0f);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected boolean needsColony()
    {
        return false;
    }

    @Override
    protected ItemStack onItemUseSuccess(final ItemStack itemStack, final World world, final EntityPlayerMP player)
    {
        return itemStack;
    }

    @Override
    public void appendHoverText(
      @NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<String> tooltip, @NotNull final TooltipFlag flagIn)
    {
        final String guiHint = String.translatable(TOOL_GENERIC_SCROLL_HIGHLIGHT_DESCRIPTION);
        guiHint.setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN));
        tooltip.add(guiHint);
    }
}




