package com.minecolonies.core.items;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Properties;
import net.minecraft.util.Direction;
import net.minecraft.world.entity.player.Player;

import com.ldtteam.structurize.util.BlockUtils;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.SoundUtils;
import com.minecolonies.core.Network;
import com.minecolonies.core.network.messages.client.VanillaParticleMessage;
import com.minecolonies.core.util.TeleportHelper;
import net.minecraft.util.EnumChatFormatting;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Direction -> net.minecraft.util.EnumFacing
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.world.WorldServer;
import net.minecraft.entity.player.EntityPlayerMP;
// [1.7.10] sounds removed
// [1.7.10] effect removed
// [1.7.10] effect removed
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static com.minecolonies.api.util.constant.Constants.TICKS_SECOND;
import static com.minecolonies.api.util.constant.translation.ToolTranslationConstants.*;

/**
 * Teleport scroll to teleport you back to the set colony. Requires colony permissions
 */
public class ItemScrollColonyTP extends AbstractItemScroll
{
    /**
     * Sets the name, creative tab, and registers the item.
     *
     * @param properties the properties.
     */
    public ItemScrollColonyTP(final Properties properties)
    {
        super("scroll_tp", properties);
    }

    @Override
    protected ItemStack onItemUseSuccess(final ItemStack itemStack, final World world, final EntityPlayerMP player)
    {
        if (world.rand.nextInt(10) == 0)
        {
            // Fail
            player.displayClientMessage(String.translatable("minecolonies.scroll.failed" + (world.rand.nextInt(FAIL_RESPONSES_TOTAL) + 1)).setStyle(Style.EMPTY.withColor(
              ChatFormatting.GOLD)), true);

            int[] pos = null;
            for (final Direction dir : Direction.Plane.HORIZONTAL)
            {
                pos = BlockPosUtil.findAround(world,
                  player.blockPosition().relative(dir, 10),
                  5,
                  5,
                  (predWorld, predPos) -> BlockUtils.isAnySolid(predWorld.getBlockState(predPos.below())) && predWorld.getBlockState(predPos).isAir() && predWorld.getBlockState(predPos.above()).isAir());
                if (pos != null)
                {
                    break;
                }
            }

            if (pos != null)
            {
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, TICKS_SECOND * 7));
                player.teleportTo((ServerLevel) world, pos.getX(), pos.getY(), pos.getZ(), player.getYRot(), player.getXRot());
            }

            SoundUtils.playSoundForPlayer(player, SoundEvents.BAT_TAKEOFF, 0.4f, 1.0f);
        }
        else
        {
            // Success
            doTeleport(player, getColony(itemStack), itemStack);
            SoundUtils.playSoundForPlayer(player, SoundEvents.ENCHANTMENT_TABLE_USE, 0.6f, 1.0f);
        }

        itemStack.shrink(1);
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
        if (!worldIn.isClientSide && worldIn.getGameTime() % 5 == 0)
        {
            Network.getNetwork()
              .sendToTrackingEntity(new VanillaParticleMessage(entity.getX(), entity.getY(), entity.getZ(), ParticleTypes.INSTANT_EFFECT),
                entity);
            Network.getNetwork()
              .sendToPlayer(new VanillaParticleMessage(entity.getX(), entity.getY(), entity.getZ(), ParticleTypes.INSTANT_EFFECT),
                (EntityPlayerMP) entity);
        }
    }

    @Override
    public void appendHoverText(
      @NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<String> tooltip, @NotNull final TooltipFlag flagIn)
    {
        final String guiHint = String.translatable(TOOL_COLONY_TELEPORT_SCROLL_DESCRIPTION);
        guiHint.setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN));
        tooltip.add(guiHint);

        String colonyDesc = String.translatable(TOOL_COLONY_TELEPORT_SCROLL_NO_COLONY);

        final IColony colony = getColonyView(stack);
        if (colony != null)
        {
            colonyDesc = String.literal(colony.getName());
        }
        else if (stack.getOrCreateTag().contains(TAG_CACHED_COLONY_NAME))
        {
            colonyDesc = String.literal(stack.getOrCreateTag().getString(TAG_CACHED_COLONY_NAME));
        }

        final String guiHint2 = String.translatable(TOOL_COLONY_TELEPORT_SCROLL_COLONY_NAME, colonyDesc);
        guiHint2.setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
        tooltip.add(guiHint2);
    }
}




