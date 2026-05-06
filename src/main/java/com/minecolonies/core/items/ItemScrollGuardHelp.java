package com.minecolonies.core.items;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.InteractionResult;
import net.minecraft.world.item.Properties;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.entity.ai.statemachine.AIOneTimeEventTarget;
import com.minecolonies.api.entity.ai.statemachine.states.AIWorkerState;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.SoundUtils;
import com.minecolonies.core.Network;
import com.minecolonies.core.colony.buildings.AbstractBuildingGuards;
import com.minecolonies.core.colony.buildings.modules.settings.GuardFollowModeSetting;
import com.minecolonies.core.colony.buildings.modules.settings.GuardTaskSetting;
import com.minecolonies.core.colony.jobs.AbstractJobGuard;
import com.minecolonies.core.entity.ai.workers.guard.AbstractEntityAIGuard;
import com.minecolonies.core.network.messages.client.VanillaParticleMessage;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import net.minecraft.util.EnumChatFormatting;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.entity.player.EntityPlayerMP;
// [1.7.10] sounds removed
// [1.7.10] InteractionResult -> boolean
import net.minecraft.entity.Entity;
// [1.7.10] world.entity removed
import net.minecraft.entity.EntityLivingBase;
// [1.7.10] world.entity removed
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.World;
// [1.7.10] block.entity removed
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.minecolonies.api.util.constant.Constants.TICKS_SECOND;
import static com.minecolonies.api.util.constant.translation.ToolTranslationConstants.TOOL_GUARD_SCROLL_NO_GUARD_BUILDING;

/**
 * Magic scroll which summons guards to the users aid, with a limited duration. Only works within the same world as the colony.
 */
public class ItemScrollGuardHelp extends AbstractItemScroll
{
    /**
     * Sets the name, creative tab, and registers the item.
     *
     * @param properties the properties.
     */
    public ItemScrollGuardHelp(final Properties properties)
    {
        super("scroll_guard_help", properties);
    }

    @Override
    protected ItemStack onItemUseSuccess(
      final ItemStack itemStack, final World world, final EntityPlayerMP player)
    {
        final IColony colony = getColony(itemStack);
        final int[] buildingPos = BlockPosUtil.read(itemStack.getTag(), TAG_BUILDING_POS);
        final IBuilding building = colony.getServerBuildingManager().getBuilding(buildingPos);
        if (!(building instanceof AbstractBuildingGuards))
        {
            MessageUtils.format(TOOL_GUARD_SCROLL_NO_GUARD_BUILDING).sendTo(player);
            return itemStack;
        }

        itemStack.shrink(1);
        final List<ICitizenData> guards = new ArrayList<>(building.getAllAssignedCitizen());

        if (world.rand.nextInt(10) == 0 || colony.getWorld() != world)
        {
            // Fail
            final Llama entity = EntityType.LLAMA.create(world);
            entity.setPos(player.getX(), player.getY(), player.getZ());
            world.addFreshEntity(entity);

            player.displayClientMessage(String.translatable("minecolonies.scroll.failed" + (world.rand.nextInt(FAIL_RESPONSES_TOTAL) + 1)).setStyle(Style.EMPTY.withColor(
              ChatFormatting.GOLD)), true);

            SoundUtils.playSoundForPlayer(player, SoundEvents.EVOKER_CAST_SPELL, 0.5f, 1.0f);
            return itemStack;
        }
        else
        {
            for (final ICitizenData citizenData : guards)
            {
                final AbstractJobGuard job = citizenData.getJob(AbstractJobGuard.class);
                if (job != null && job.getWorkerAI() != null && !((AbstractEntityAIGuard) job.getWorkerAI()).hasTool())
                {
                    continue;
                }

                if (citizenData.getEntity().isPresent())
                {
                    if (citizenData.getCitizenDiseaseHandler().isSick())
                    {
                        continue;
                    }

                    citizenData.getEntity().get().discard();
                }

                colony.getCitizenManager().spawnOrCreateCivilian(citizenData, world, List.of(player.blockPosition()), true);
                citizenData.setNextRespawnPosition(buildingPos);

                building.getSetting(AbstractBuildingGuards.GUARD_TASK).set(GuardTaskSetting.FOLLOW);
                ((AbstractBuildingGuards) building).setPlayerToFollow(player);
                final GuardFollowModeSetting grouping = building.getSetting(AbstractBuildingGuards.FOLLOW_MODE);
                if (grouping.getValue().equals(GuardFollowModeSetting.LOOSE))
                {
                    grouping.trigger();
                }

                citizenData.setSaturation(100);

                colony.getPackageManager().addCloseSubscriber(player);

                if (job != null && job.getWorkerAI() != null)
                {
                    final long spawnTime = world.getTotalWorldTime() + TICKS_SECOND * 900;

                    // Timed despawn
                    job.getWorkerAI().registerTarget(new AIOneTimeEventTarget(() ->
                    {
                        if (world.getTotalWorldTime() - spawnTime > 0)
                        {
                            ((AbstractBuildingGuards) building).getSetting(AbstractBuildingGuards.GUARD_TASK).set(GuardTaskSetting.PATROL);
                            citizenData.getEntity().ifPresent(e -> e.remove(Entity.RemovalReason.DISCARDED));
                            colony.getPackageManager().removeCloseSubscriber(player);
                            return true;
                        }
                        return false;
                    }
                      , AIWorkerState.DECIDE));
                }
            }

            SoundUtils.playSoundForPlayer(player, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.3f, 1.0f);
        }


        return itemStack;
    }

    @Override
    protected boolean needsColony()
    {
        return true;
    }

    @Override
    @NotNull
    public InteractionResult useOn(UseOnContext ctx)
    {
        final InteractionResult result = super.useOn(ctx);

        if (ctx.getLevel().isClientSide)
        {
            return result;
        }

        final BlockEntity te = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
        if (te instanceof TileEntityColonyBuilding && ctx.getPlayer() != null)
        {
            final IBuilding building = ((TileEntityColonyBuilding) te).getColony().getServerBuildingManager().getBuilding(ctx.getClickedPos());
            if (!(building instanceof AbstractBuildingGuards))
            {
                MessageUtils.format(TOOL_GUARD_SCROLL_NO_GUARD_BUILDING).sendTo(ctx.getPlayer());
            }
        }

        return result;
    }

    @Override
    public void onUseTick(World worldIn, EntityLivingBase entity, ItemStack stack, int count)
    {
        if (!worldIn.isClientSide && worldIn.getGameTime() % 5 == 0)
        {
            Network.getNetwork()
              .sendToTrackingEntity(new VanillaParticleMessage(entity.getX(), entity.getY(), entity.getZ(), ParticleTypes.ENCHANT),
                entity);
            Network.getNetwork()
              .sendToPlayer(new VanillaParticleMessage(entity.getX(), entity.getY(), entity.getZ(), ParticleTypes.ENCHANT),
                (EntityPlayerMP) entity);
        }
    }

    @Override
    public void appendHoverText(
      @NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<String> tooltip, @NotNull final TooltipFlag flagIn)
    {
        final String guiHint = String.translatable("item.minecolonies.scroll_guard_help.tip");
        guiHint.setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN));
        tooltip.add(guiHint);

        String colonyDesc = String.translatable("item.minecolonies.scroll.colony.none").getString();

        final IColony colony = getColonyView(stack);
        if (colony != null)
        {
            colonyDesc = colony.getName();
        }

        final String guiHint2 = String.translatable("item.minecolonies.scroll.colony.tip", colonyDesc);
        guiHint2.setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
        tooltip.add(guiHint2);
    }
}




