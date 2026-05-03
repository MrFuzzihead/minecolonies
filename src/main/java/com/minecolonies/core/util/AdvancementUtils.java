package com.minecolonies.core.util;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.permissions.ColonyPlayer;
import com.minecolonies.api.colony.permissions.Rank;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class AdvancementUtils
{

    public static void TriggerAdvancementPlayersForColony(final IColony colony, Consumer<EntityPlayerMP> playerConsumer)
    {
        MinecraftServer minecraftServer = colony.getWorld().getServer();
        if (minecraftServer != null)
        {
            final Predicate<Rank> predicate = Rank::isColonyManager;

            for (final ColonyPlayer player : colony.getPermissions().getFilteredPlayers(predicate))
            {
                final EntityPlayerMP playerEntity = minecraftServer.getPlayerList().getPlayer(player.getID());
                if (playerEntity != null)
                {
                    playerConsumer.accept(playerEntity);
                }
            }
        }
    }
}

