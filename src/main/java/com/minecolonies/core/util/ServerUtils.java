package com.minecolonies.core.util;

import com.minecolonies.api.colony.permissions.ColonyPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Utility for server related stuff.
 * <p>
 * Here you can query players on the server.
 *
 * @since 0.2
 */
public final class ServerUtils
{

    /**
     * Private constructor to hide the implicit public one.
     */
    private ServerUtils()
    {
    }

    /**
     * Returns the online PlayerEntity with the given UUID.
     *
     * @param world world the player is in
     * @param id    the player's UUID
     * @return the Player
     */
    @Nullable
    public static EntityPlayer getPlayerFromUUID(@NotNull final World world, @NotNull final UUID id)
    {
        for (int i = 0; i < world.playerEntities.size(); ++i)
        {
            final EntityPlayer p = (EntityPlayer) world.playerEntities.get(i);
            if (id.equals(p.getGameProfile().getId()))
            {
                return p;
            }
        }
        return null;
    }

    /**
     * Returns a list of online players whose UUID's match the ones provided.
     *
     * @param world the world the players are in.
     * @param ids   List of UUIDs
     * @return list of PlayerEntitys
     */
    @NotNull
    public static List<EntityPlayer> getPlayersFromUUID(@Nullable final World world, @NotNull final Collection<UUID> ids)
    {
        if (world == null)
        {
            return Collections.emptyList();
        }
        @NotNull final List<EntityPlayer> players = new ArrayList<>();

        for (final Object o : world.playerEntities)
        {
            if (o instanceof EntityPlayer)
            {
                @NotNull final EntityPlayer player = (EntityPlayer) o;
                if (ids.contains(player.getGameProfile().getId()))
                {
                    players.add(player);
                    if (players.size() == ids.size())
                    {
                        return players;
                    }
                }
            }
        }
        return players;
    }

    /**
     * Returns a list of players from a list of {@link ColonyPlayer}.
     *
     * @param players The list of players to convert.
     * @param world   an instance of the world.
     * @return A list of {@link EntityPlayer}s
     */
    @NotNull
    public static List<EntityPlayer> getPlayersFromPermPlayer(@NotNull final List<EntityPlayer> players, @NotNull final World world)
    {
        @NotNull final List<EntityPlayer> playerList = new ArrayList<>();

        for (@NotNull final EntityPlayer player : players)
        {
            playerList.add(ServerUtils.getPlayerFromPermPlayer(player, world));
        }

        return playerList;
    }

    /**
     * Retrieves a Player from {@link ColonyPlayer}.
     *
     * @param player The {@link ColonyPlayer} to convert
     * @param world  an instance of the world.
     * @return The {@link EntityPlayer} reference.
     */
    @Nullable
    public static EntityPlayer getPlayerFromPermPlayer(@NotNull final EntityPlayer player, @NotNull final World world)
    {
        return ServerUtils.getPlayerFromUUID(player.getGameProfile().getId(), world);
    }

    /**
     * Finds a player by his UUID
     *
     * @param uuid  the uuid to search for
     * @param world an instance of the world.
     * @return The player the player if found or null
     */
    @Nullable
    public static EntityPlayer getPlayerFromUUID(@Nullable final UUID uuid, @NotNull final World world)
    {
        if (uuid == null)
        {
            return null;
        }
        for (final Object o : world.playerEntities)
        {
            if (o instanceof EntityPlayerMP)
            {
                final EntityPlayerMP player = (EntityPlayerMP) o;
                if (player.getGameProfile().getId().equals(uuid))
                {
                    return player;
                }
            }
        }
        return null;
    }
}
