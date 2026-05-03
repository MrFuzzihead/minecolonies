package com.minecolonies.core.items;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.SoundUtils;
import com.minecolonies.core.Network;
import com.minecolonies.core.network.messages.client.VanillaParticleMessage;
import com.minecolonies.core.util.TeleportHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.entity.player.EntityPlayerMP;
// [1.7.10] sounds removed
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static com.minecolonies.api.util.constant.translation.ToolTranslationConstants.*;

/**
 * Colony teleport scroll, which teleports the user and any nearby players to the colony, invite a friend-style
 */
public class ItemScrollColonyAreaTP extends AbstractItemScroll
{
    /**
     * Sets the name, creative tab, and registers the item.
     *
     * @param properties the properties.
     */
    public ItemScrollColonyAreaTP(final Properties properties)
    {
        super("scroll_area_tp", properties);
    }

    @Override
    public int getUseDuration(ItemStack itemStack)
    {
        return 64;
    }

    @Override
    protected ItemStack onItemUseSuccess(final ItemStack itemStack, final World world, final EntityPlayerMP player)
    {
        if (world.random.nextInt(10) == 0)
        {
            // Fail chance
            player.displayClientMessage(String.translatable(
              "minecolonies.scroll.failed" + (world.random.nextInt(FAIL_RESPONSES_TOTAL) + 1)).setStyle(Style.EMPTY.withColor(
              ChatFormatting.GOLD)), true);

            itemStack.shrink(1);
            if (!ItemStackUtils.isEmpty(itemStack))
            {
                player.drop(itemStack.copy(), true, false);
                itemStack.setCount(0);
            }

            for (final EntityPlayerMP sPlayer : getAffectedPlayers(player))
            {
                SoundUtils.playSoundForPlayer(sPlayer, SoundEvents.EVOKER_PREPARE_SUMMON, 0.3f, 1.0f);
            }
        }
        else
        {
            for (final EntityPlayerMP sPlayer : getAffectedPlayers(player))
            {
                doTeleport(sPlayer, getColony(itemStack), itemStack);
                SoundUtils.playSoundForPlayer(sPlayer, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.1f, 1.0f);
            }

            itemStack.shrink(1);
        }

        return itemStack;
    }

    @Override
    protected boolean needsColony()
    {
        return true;
    }

    /**
     * Does the teleport action
     *
     * @param player user of the item
     * @param colony colony to teleport to
     */
    protected void doTeleport(final EntityPlayerMP player, final IColony colony, final ItemStack stack)
    {
        TeleportHelper.colonyTeleport(player, colony);
    }

    @Override
    public void onUseTick(World worldIn, EntityLivingBase entity, ItemStack stack, int count)
    {
        if (!worldIn.isClientSide && worldIn.getGameTime() % 5 == 0 && entity instanceof Player)
        {
            final EntityPlayerMP sPlayer = (EntityPlayerMP) entity;
            for (final Entity player : getAffectedPlayers(sPlayer))
            {
                Network.getNetwork()
                  .sendToTrackingEntity(new VanillaParticleMessage(player.getX(), player.getY(), player.getZ(), ParticleTypes.INSTANT_EFFECT),
                    player);
            }

            Network.getNetwork()
              .sendToPlayer(new VanillaParticleMessage(sPlayer.getX(), sPlayer.getY(), sPlayer.getZ(), ParticleTypes.INSTANT_EFFECT),
                sPlayer);
        }
    }

    /**
     * Get the list of players affected by the area teleport
     */
    private List<EntityPlayerMP> getAffectedPlayers(final EntityPlayerMP user)
    {
        return user.World.getEntitiesOfClass(EntityPlayerMP.class, user.getBoundingBox().inflate(10, 2, 10));
    }

    @Override
    public void appendHoverText(
      @NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<String> tooltip, @NotNull final TooltipFlag flagIn)
    {
        final String guiHint = String.translatable(TOOL_COLONY_TELEPORT_AREA_SCROLL_DESCRIPTION);
        guiHint.setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN));
        tooltip.add(guiHint);

        String colonyDesc = String.translatable(TOOL_COLONY_TELEPORT_SCROLL_NO_COLONY);

        final IColony colony = getColonyView(stack);
        if (colony != null)
        {
            colonyDesc = String.literal(colony.getName());
        }

        final String guiHint2 = String.translatable(TOOL_COLONY_TELEPORT_SCROLL_COLONY_NAME, colonyDesc);
        guiHint2.setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
        tooltip.add(guiHint2);
    }
}




