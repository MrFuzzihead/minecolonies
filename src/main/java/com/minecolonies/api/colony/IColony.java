package com.minecolonies.api.colony;

import com.minecolonies.api.colony.buildings.modules.ICommonSettingsModule;
import com.minecolonies.api.colony.connections.IColonyConnectionManager;
import com.minecolonies.api.colony.managers.interfaces.*;
import com.minecolonies.api.colony.permissions.IPermissions;
import com.minecolonies.api.colony.requestsystem.manager.IRequestManager;
import com.minecolonies.api.colony.requestsystem.requester.IRequester;
import com.minecolonies.api.colony.workorders.IWorkManager;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.quests.IQuestManager;
import com.minecolonies.api.research.IResearchManager;
import net.minecraft.util.EnumChatFormatting;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.Block;
// [1.7.10] BlockState -> int metadata
import net.minecraft.world.chunk.Chunk;
// [1.7.10 BACKPORT] Removed:
//   net.minecraftforge.common.capabilities.Capability      — no capabilities in 1.7.10
//   net.minecraftforge.common.capabilities.CapabilityManager — no capabilities in 1.7.10
//   net.minecraftforge.common.capabilities.CapabilityToken  — no capabilities in 1.7.10
//   CLOSE_COLONY_CAP static field — replaced by ColonyChunkDataHandler.getColonyTagCapability()
import cpw.mods.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Interface of the Colony and ColonyView which will have to implement the following methods.
 */
public interface IColony
{
    // [1.7.10 BACKPORT] CLOSE_COLONY_CAP removed — it was a Forge capability field:
    //   Capability<IColonyTagCapability> CLOSE_COLONY_CAP = CapabilityManager.get(new CapabilityToken<>() {});
    // It is replaced by the static accessor:
    //   ColonyChunkDataHandler.getColonyTagCapability(chunk)
    // All former   chunk.getCapability(CLOSE_COLONY_CAP, null).resolve().orElse(null)
    // calls should become: ColonyChunkDataHandler.getColonyTagCapability(chunk)

    void onWorldLoad(@NotNull World w);

    void onWorldUnload(@NotNull World w);

    void onServerTick(@NotNull TickEvent.ServerTickEvent event);

    @NotNull
    IWorkManager getWorkManager();

    void onWorldTick(@NotNull TickEvent.WorldTickEvent event);

    /**
     * Returns the position of the colony.
     *
     * @return pos of the colony.
     */
    int[] getCenter();

    /**
     * Returns the name of the colony.
     *
     * @return Name of the colony.
     */
    String getName();

    void setName(String n);

    /**
     * Returns the permissions of the colony.
     *
     * @return {@link IPermissions} of the colony.
     */
    IPermissions getPermissions();

    /**
     * Determine if a given chunk coordinate is considered to be within the colony's bounds.
     *
     * @param w   World to check.
     * @param pos Block Position.
     * @return True if inside colony, otherwise false.
     */
    boolean isCoordInColony(World w, int[] pos);

    /**
     * Returns the squared (x, z) distance to the center.
     *
     * @param pos Block Position.
     * @return Squared distance to the center in (x, z) direction.
     */
    long getDistanceSquared(int[] pos);

    /**
     * returns this colonies unique id.
     *
     * @return an int representing the id.
     */
    int getID();

    /**
     * Getter for the team colony color.
     *
     * @return the color.
     */
    EnumChatFormatting getTeamColonyColor();

    /**
     * Returns this colony's banner patterns, as a List
     *
     * @return a list of pattern-color pairs
     */
    NBTTagList getColonyFlag();

    /**
     * Whether it is day for the colony
     *
     * @return true if it is day
     */
    boolean isDay();

    /**
     * Get the last contact of a EntityPlayer to the colony in hours.
     *
     * @return an integer with a describing value.
     */
    int getLastContactInHours();

    /**
     * Method to get the World this colony is in.
     *
     * @return the World the colony is in.
     */
    World getWorld();

    /**
     * Get the current {@link IRequestManager} for this Colony. Returns null if the current Colony does not support the request system.
     *
     * @return the {@link IRequestManager} for this colony, null if not supported.
     */
    @NotNull
    IRequestManager getRequestManager();

    /**
     * Called to mark this colony dirty, and in need of syncing / saving.
     */
    void markDirty();

    /**
     * Called to check if the colony can be deleted by an automatic cleanup.
     *
     * @return true if so.
     */
    boolean canBeAutoDeleted();

    /**
     * Method used to get a {@link IRequester} from a given Position. Is always a Building.
     *
     * @param pos The position to get the Building that acts as a requester.
     * @return The {@link IRequester} from the position, or null.
     */
    @Nullable
    IRequester getRequesterBuildingForPosition(@NotNull final int[] pos);

    /**
     * Remove a visiting EntityPlayer.
     *
     * @param EntityPlayer the EntityPlayer.
     */
    void removeVisitingPlayer(final EntityPlayer EntityPlayer);

    /**
     * Get the players in the colony which should receive the message.
     *
     * @return list of players
     */
    @NotNull
    List<EntityPlayer> getMessagePlayerEntities();

    @NotNull
    default List<int[]> getWayPoints(@NotNull int[] position, @NotNull int[] target)
    {
        final List<int[]> tempWayPoints = new ArrayList<>();
        tempWayPoints.addAll(getWayPoints().keySet());
        tempWayPoints.addAll(getServerBuildingManager().getBuildings().keySet());

        final double maxX = Math.max(position.getX(), target.getX());
        final double maxZ = Math.max(position.getZ(), target.getZ());

        final double minX = Math.min(position.getX(), target.getX());
        final double minZ = Math.min(position.getZ(), target.getZ());

        final Iterator<int[]> iterator = tempWayPoints.iterator();
        while (iterator.hasNext())
        {
            final int[] p = iterator.next();
            final int x = p.getX();
            final int z = p.getZ();
            if (x < minX || x > maxX || z < minZ || z > maxZ)
            {
                iterator.remove();
            }
        }

        return tempWayPoints;
    }

    double getOverallHappiness();

    Map<int[], BlockState> getWayPoints();

    String getStructurePack();

    void setStructurePack(String style);

    IRegisteredStructureManager getServerBuildingManager();

    ICommonRegisteredStructureManager getCommonBuildingManager();

    ICitizenManager getCitizenManager();

    IGraveManager getGraveManager();

    /**
     * Gets the visitor manager
     *
     * @return manager
     */
    IVisitorManager getVisitorManager();

    /**
     * Get the animal manager of the colony.
     *
     * @return the animal manager.
     */
    IAnimalManager getAnimalManager();

    IRaiderManager getRaiderManager();

    /**
     * Get the event manager of the colony.
     *
     * @return the event manager.
     */
    IEventManager getEventManager();

    /**
     * Get the reproduction manager of the colony.
     *
     * @return the reproduction manager.
     */
    IReproductionManager getReproductionManager();

    /**
     * Get the event description manager of the colony.
     *
     * @return the event description manager.
     */
    IEventDescriptionManager getEventDescriptionManager();

    /**
     * The colony networking packaging manager.
     *
     * @return The packaging manager.
     */
    IColonyPackageManager getPackageManager();

    /**
     * Get the travelling manager of the colony.
     *
     * @return the travelling manager.
     */
    ITravellingManager getTravellingManager();

    /**
     * Get the connection manager of the colony.
     * @return the connection manager.
     */
    IColonyConnectionManager getConnectionManager();

    /**
     * Add a visiting EntityPlayer.
     *
     * @param EntityPlayer the EntityPlayer.
     */
    void addVisitingPlayer(final EntityPlayer EntityPlayer);

    /**
     * Get the colony dimension.
     *
     * @return the dimension id.
     */
    int /* ResourceKey */ getDimension();

    /**
     * Check if the colony is on the server or client.
     *
     * @return true if so.
     */
    boolean isRemote();

    /**
     * Get the research manager.
     *
     * @return the research manager object.
     */
    IResearchManager getResearchManager();

    /**
     * Save the time when mercenaries are used, to set a cooldown.
     */
    void usedMercenaries();

    /**
     * Get the last time mercenaries were used.
     *
     * @return the mercenary use time.
     */
    long getMercenaryUseTime();

    NBTTagCompound getColonyTag();

    boolean isColonyUnderAttack();

    boolean isValidAttackingPlayer(EntityPlayer entity);

    boolean isValidAttackingGuard(AbstractEntityCitizen entity);

    void setColonyColor(EnumChatFormatting color);

    void setColonyFlag(NBTTagList patterns);

    void addWayPoint(int[] pos, BlockState newWayPointState);

    void addGuardToAttackers(AbstractEntityCitizen entityCitizen, EntityPlayer followPlayer);

    void addFreePosition(int[] pos);

    void addFreeBlock(Block block);

    void removeFreePosition(int[] pos);

    void removeFreeBlock(Block block);

    void setCanBeAutoDeleted(boolean canBeDeleted);

    NBTTagCompound write(NBTTagCompound colonyCompound);

    void read(NBTTagCompound compound);

    /**
     * Returns a set of players receiving important messages for the colony.
     *
     * @return set of players.
     */
    @NotNull
    List<EntityPlayer> getImportantMessageEntityPlayers();

    /**
     * Tries to use a given amount of additional growth-time for childs.
     *
     * @param amount amount to use
     * @return true if used up.
     */
    boolean useAdditionalChildTime(int amount);

    /**
     * Sets whether the colony has a child.
     */
    void updateHasChilds();

    /**
     * Adds a loaded chunk to the colony list
     *
     * @param chunkPos chunk to add
     */
    void addLoadedChunk(long chunkPos, final Chunk chunk);

    /**
     * Adds a chunk from the colony list
     *
     * @param chunkPos chunk to remove
     */
    void removeLoadedChunk(long chunkPos);

    /**
     * Returns the amount of loaded chunks
     *
     * @return amount of chunks
     */
    int getLoadedChunkCount();

    Set<Long> getLoadedChunks();

    /**
     * Returns the colonies current state.
     *
     * @return the state.
     */
    ColonyState getState();

    /**
     * Is the colony active currently.
     *
     * @return true if so.
     */
    boolean isActive();

    /**
     * Get the set of chunk positions which the colony is loading through tickets
     *
     * @return set of positions
     */
    Set<Long> getTicketedChunks();

    /**
     * Set the texture style of the colony.
     *
     * @param style the style to set.
     */
    void setTextureStyle(String style);

    /**
     * Get the colony style.
     *
     * @return the string id of the style.
     */
    String getTextureStyleId();

    /**
     * Get the colony name style.
     *
     * @return the string id of the style.
     */
    String getNameStyle();

    /**
     * Set the colony name style.
     *
     * @param style the string id of the style.
     */
    void setNameStyle(final String style);

    /**
     * Get the matching citizen name file of the colony .
     *
     * @return the matching file.
     */
    CitizenNameFile getCitizenNameFile();

    /**
     * Get the statistics manager of the colony.
     *
     * @return the statistics manager.
     */
    IStatisticsManager getStatisticsManager();

    /**
     * Get the current day of the colony.
     *
     * @return the current day progress of the colony.
     */
    int getDay();

    /**
     * Get the quest manager of the colony.
     *
     * @return the quest manager.
     */
    IQuestManager getQuestManager();

    /**
     * Get citizen from colony.
     *
     * @param id the id of the cit.
     * @return the cit.
     */
    ICitizen getCitizen(int id);

    /**
     * Get the colony World settings module.
     * @return the settings module.
     */
    ICommonSettingsModule getSettings();
}






