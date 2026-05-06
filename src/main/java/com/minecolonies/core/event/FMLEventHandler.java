package com.minecolonies.core.event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.core.datalistener.*;
import com.minecolonies.core.entity.pathfinding.Pathfinding;
import com.minecolonies.core.util.BackUpHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.world.LevelTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
// [1.7.10] eventbus removed
import org.jetbrains.annotations.NotNull;

/**
 * Event handler used to catch various forge events.
 */
public class FMLEventHandler
{
    @SubscribeEvent
    public static void onServerTick(final TickEvent.ServerTickEvent event)
    {
        IColonyManager.getInstance().onServerTick(event);
    }

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event)
    {
        IColonyManager.getInstance().onClientTick(event);
    }

    @SubscribeEvent
    public static void onPlayerLogin(@NotNull final PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() instanceof EntityPlayerMP)
        {
            // This automatically reloads the owner of the colony if failed.
            IColonyManager.getInstance().getIColonyByOwner(event.getEntity().World, event.getEntity());
            //ColonyManager.syncAllColoniesAchievements();
        }
    }

    @SubscribeEvent
    public static void onAddReloadListenerEvent(@NotNull final AddReloadListenerEvent event)
    {
        event.addListener(new CrafterRecipeListener());
        event.addListener(new ResearchListener());
        event.addListener(new CustomVisitorListener());
        event.addListener(new CitizenNameListener());
        event.addListener(new QuestJsonListener());
        event.addListener(new ItemNbtListener());
        event.addListener(new StudyItemListener());
        event.addListener(new DiseasesListener());
        event.addListener(new RecruitmentItemsListener());
    }

    @SubscribeEvent
    public static void onWorldTick(final LevelTickEvent event)
    {
        IColonyManager.getInstance().onWorldTick(event);
    }

    @SubscribeEvent
    public static void onServerAboutToStart(@NotNull final ServerAboutToStartEvent event)
    {
        IColonyManager.getInstance().getRecipeManager().reset();
    }

    @SubscribeEvent
    public static void onServerStopped(@NotNull final ServerStoppingEvent event)
    {
        Pathfinding.shutdown();
    }

    @SubscribeEvent
    public static void onServerStarted(@NotNull final ServerStartedEvent event)
    {
        BackUpHelper.loadMissingColonies();
    }
}




