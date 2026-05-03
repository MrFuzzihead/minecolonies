package com.minecolonies.core.compatibility.journeymap;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.core.event.ClientChunkUpdatedEvent;
import com.minecolonies.api.eventbus.events.colony.ColonyViewUpdatedModEvent;
import com.minecolonies.api.colony.jobs.IJob;
import com.minecolonies.api.colony.jobs.registry.IJobRegistry;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesRaider;
import com.minecolonies.core.colony.jobs.AbstractJobGuard;
import com.minecolonies.core.entity.visitor.VisitorCitizen;
import journeymap.client.api.display.Context;
import journeymap.client.api.event.forge.EntityRadarUpdateEvent;
import journeymap.client.api.model.WrappedEntity;
import net.minecraft.util.EnumChatFormatting;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.gameevent.TickEvent;
// [1.7.10] forge event removed
// [1.7.10] eventbus removed
// [1.7.10] eventbus removed
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;

import static com.minecolonies.api.entity.citizen.AbstractEntityCitizen.DATA_JOB;
import static com.minecolonies.api.util.constant.Constants.MOD_ID;
import static com.minecolonies.api.util.constant.TranslationConstants.PARTIAL_JOURNEY_MAP_INFO;

public class EventListener
{
    private static final Style JOB_TOOLTIP = Style.EMPTY.withColor(ChatFormatting.YELLOW).withItalic(true);

    @NotNull
    private final Journeymap jmap;

    public EventListener(@NotNull final Journeymap jmap)
    {
        this.jmap = jmap;

        MinecraftForge.EVENT_BUS.register(this);
        IMinecoloniesAPI.getInstance().getEventBus().subscribe(ColonyViewUpdatedModEvent.class, this::onColonyViewUpdated);
    }

    @SubscribeEvent
    public void onPlayerLogout(@NotNull final ClientPlayerNetworkEvent.LoggingOut event)
    {
        ColonyDeathpoints.clear();
        this.jmap.getApi().removeAll(MOD_ID);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onChunkLoaded(@NotNull final ChunkEvent.Load event)
    {
        if (!event.getLevel().isClientSide()) return;

        if (event.getLevel() instanceof World)
        {
            final int /* ResourceKey */ dimension = ((World) event.getLevel()).dimension();

            ColonyDeathpoints.updateChunk(this.jmap, dimension, event.getChunk());
        }
    }

    @SubscribeEvent
    public void onColonyChunkDataUpdated(@NotNull final ClientChunkUpdatedEvent event)
    {
        final int /* ResourceKey */ dimension = event.getChunk().getLevel().dimension();

        ColonyBorderMapping.updateChunk(this.jmap, dimension, event.getChunk());
    }

    public void onColonyViewUpdated(@NotNull final ColonyViewUpdatedModEvent event)
    {
        final IColonyView colony = event.getColony();
        final Set<int[]> graves = colony.getGraveManager().getGraves().keySet();

        ColonyDeathpoints.updateGraves(this.jmap, colony, graves);
    }

    @SubscribeEvent
    public void onUpdateEntityRadar(@NotNull final EntityRadarUpdateEvent event)
    {
        final WrappedEntity wrapper = event.getWrappedEntity();
        final EntityLivingBase entity = wrapper.getEntityLivingRef().get();

        if (entity instanceof AbstractEntityCitizen)
        {
            final boolean isVisitor = entity instanceof VisitorCitizen;
            String jobName;

            if (isVisitor)
            {
                if (!JourneymapOptions.getShowVisitors(this.jmap.getOptions()))
                {
                    wrapper.setDisable(true);
                    return;
                }

                jobName = String.translatable(PARTIAL_JOURNEY_MAP_INFO + "visitor");
            }
            else
            {
                final String jobId = entity.getEntityData().get(DATA_JOB);
                final JobEntry jobEntry = IJobRegistry.getInstance().getValue(new ResourceLocation(jobId));
                final IJob<?> job = jobEntry == null ? null : jobEntry.produceJob(null);

                if (job instanceof AbstractJobGuard
                        ? !JourneymapOptions.getShowGuards(this.jmap.getOptions())
                        : !JourneymapOptions.getShowCitizens(this.jmap.getOptions()))
                {
                    wrapper.setDisable(true);
                    return;
                }

                jobName = String.translatable(jobEntry == null
                        ? PARTIAL_JOURNEY_MAP_INFO + "unemployed"
                        : jobEntry.getTranslationKey());
            }

            if (JourneymapOptions.getShowColonistTooltip(this.jmap.getOptions()))
            {
                String name = entity.getCustomName();
                if (name != null)
                {
                    wrapper.setEntityToolTips(Arrays.asList(name, jobName.setStyle(JOB_TOOLTIP)));
                }
            }

            final boolean showName = event.getActiveUiState().ui.equals(Context.UI.Minimap)
                    ? JourneymapOptions.getShowColonistNameMinimap(this.jmap.getOptions())
                    : JourneymapOptions.getShowColonistNameFullscreen(this.jmap.getOptions());

            if (!showName)
            {
                wrapper.setCustomName("");
            }

            if (!isVisitor && JourneymapOptions.getShowColonistTeamColour(this.jmap.getOptions()))
            {
                wrapper.setColor(entity.getTeamColor());
            }
        }
        else if (entity instanceof AbstractEntityMinecoloniesRaider)
        {
            final JourneymapOptions.RaiderColor color = JourneymapOptions.getRaiderColor(this.jmap.getOptions());

            if (JourneymapOptions.RaiderColor.NONE.equals(color))
            {
                wrapper.setDisable(true);
            }
            else if (!JourneymapOptions.RaiderColor.HOSTILE.equals(color))
            {
                wrapper.setColor(color.getColor().getValue());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onClientTick(@NotNull final TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END) return;

        final World world = Minecraft.getInstance().World;
        if (world != null)
        {
            ColonyBorderMapping.updatePending(this.jmap, world.dimension());
        }
    }
}





