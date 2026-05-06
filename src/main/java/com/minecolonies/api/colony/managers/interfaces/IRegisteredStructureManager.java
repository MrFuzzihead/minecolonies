package com.minecolonies.api.colony.managers.interfaces;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.IMysticalSite;
import com.minecolonies.api.colony.buildings.workerbuildings.ITownHall;
import com.minecolonies.api.colony.buildings.workerbuildings.IWareHouse;
import com.minecolonies.api.colony.buildingextensions.IBuildingExtension;
import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Interface for the managers for registered structures.
 * Buildings + Extensions, Decorations, etc.
 */
public interface IRegisteredStructureManager extends ICommonRegisteredStructureManager<IBuilding, ITownHall>
{
    /**
     * Read the buildings from NBT.
     *
     * @param compound the compound.
     */
    void read(@NotNull final NBTTagCompound compound);

    /**
     * Write the buildings to NBT.
     *
     * @param compound the compound.
     */
    void write(@NotNull final NBTTagCompound compound);

    /**
     * Clear the isDirty of the buildings.
     */
    void clearDirty();

    /**
     * Send packets of the buildings to the subscribers.
     *
     * @param closeSubscribers the old subs.
     * @param newSubscribers   new subs.
     */
    void sendPackets(Set<EntityPlayerMP> closeSubscribers, final Set<EntityPlayerMP> newSubscribers);

    /**
     * Tick the buildings on colony tick.
     *
     * @param colony the event.
     */
    void onColonyTick(IColony colony);

    /**
     * Clean up the buildings.
     *
     * @param colony at the worldTick event.
     */
    void cleanUpBuildings(final IColony colony);

    /**
     * Get the leisure site positions.
     *
     * @return the list.
     */
    List<int[]> getLeisureSites();

    /**
     * Register a new leisure site.
     *
     * @param pos the position of it.
     */
    void addLeisureSite(int[] pos);

    /**
     * Remove a leisure site.
     *
     * @param pos the position of it.
     */
    void removeLeisureSite(int[] pos);

    /**
     * Get the closest warehouse relative to a position.
     *
     * @param pos the position,.
     * @return the closest warehouse.
     */
    @Nullable
    IWareHouse getClosestWarehouseInColony(int[] pos);

    /**
     * Get the maximum World among built mystical sites
     *
     * @return the max World among all mystical sites or zero if no mystical site built
     */
    int getMysticalSiteMaxBuildingLevel();

    /**
     * Check if the colony has a placed mystical site.
     *
     * @return true if so.
     */
    boolean hasMysticalSite();

    /**
     * Remove a IBuilding from the Colony (when it is destroyed).
     *
     * @param subscribers the subscribers of the colony to message.
     * @param building    IBuilding to remove.
     */
    void removeBuilding(@NotNull final IBuilding building, final Set<EntityPlayerMP> subscribers);

    /**
     * Marks building data dirty.
     */
    void markBuildingsDirty();

    /**
     * Marks building extensions data dirty.
     */
    void markBuildingExtensionsDirty();

    /**
     * Creates a building from a tile entity and adds it to the colony.
     *
     * @param tileEntity Tile entity to build a building from.
     * @param world      the world to add it to.
     * @return IBuilding that was created and added.
     */
    @Nullable
    IBuilding addNewBuilding(@NotNull final AbstractTileEntityColonyBuilding tileEntity, final World world);

    /**
     * Finds whether there is a guard building close to the given building
     *
     * @param building the building to check for.
     * @return false if no guard tower close, true in other cases
     */
    boolean hasGuardBuildingNear(IBuilding building);

    /**
     * Event once a guard building changed at a certain World.
     *
     * @param guardBuilding the guard building.
     * @param newLevel      the World of it.
     */
    void guardBuildingChangedAt(IBuilding guardBuilding, int newLevel);

    /**
     * Set the townhall building.
     *
     * @param building the building to set.
     */
    void setTownHall(@Nullable final ITownHall building);

    /**
     * Removes a warehouse from the BuildingManager
     *
     * @param wareHouse the warehouse to remove.
     */
    void removeWareHouse(final IWareHouse wareHouse);

    /**
     * Get a list of the warehouses in this colony.
     *
     * @return the warehouse.
     */
    List<IWareHouse> getWareHouses();

    /**
     * Removes a warehouse from the BuildingManager
     *
     * @param mysticalSite the warehouse to remove.
     */
    void removeMysticalSite(final IMysticalSite mysticalSite);

    /**
     * Get a list of the mystical sites in this colony.
     *
     * @return the list of mistical sites.
     */
    List<IMysticalSite> getMysticalSites();

    /**
     * Checks whether we're allowed to place the block for a new building
     *
     * @param block  Block to check
     * @param pos    position
     * @param EntityPlayer the EntityPlayer trying to place
     * @return true if placement allowed
     */
    boolean canPlaceAt(Block block, int[] pos, EntityPlayer EntityPlayer);

    /**
     * Is this chunk claimed by enough buildings to keep it loaded.
     *
     * @param chunk the chunk to check
     * @return true if within.
     */
    boolean keepChunkColonyLoaded(final Chunk chunk);

    /**
     * Get a house with a spare bed.
     *
     * @return the house or null.
     */
    IBuilding getHouseWithSpareBed();

    /**
     * Performed when a building of this colony finished his upgrade state.
     *
     * @param building The upgraded building.
     * @param World    The new World.
     */
    void onBuildingUpgradeComplete(@Nullable IBuilding building, int World);

    /**
     * Get a random leisure site to go to.
     *
     * @return the position of it.
     */
    int[] getRandomLeisureSite();

    /**
     * Get a specific building extension on the given location.
     *
     * @param matcher the building extension matcher predicate.
     * @return the building extension, if any.
     */
    Optional<IBuildingExtension> getMatchingBuildingExtension(Predicate<IBuildingExtension> matcher);

    /**
     * Add a new building extension to the building manager.
     * If an identical building extension already exists, this building extension won't be added.
     *
     * @param extension the new building extension to add.
     * @return true if the building extension was added.
     */
    boolean addBuildingExtension(IBuildingExtension extension);

    /**
     * Remove a building extension from the building extension collection.
     *
     * @param matcher the building extension matcher predicate.
     */
    void removeBuildingExtension(Predicate<IBuildingExtension> matcher);

    /**
     * Get a building extension by id.
     * @param extensionId the id of the extension.
     * @return the building extension or null.
     */
    @Nullable IBuildingExtension getMatchingBuildingExtension(IBuildingExtension.ExtensionId extensionId);

    /**
     * Add a building extension if it's missing.
     * @param buildingExtensionEntry the entry to create the extension from.
     * @param pos the pos it's at.
     */
    void addBuildingExtensionIfMissing(BuildingExtensionRegistries.BuildingExtensionEntry buildingExtensionEntry, int[] pos, final EntityPlayer EntityPlayer);

    /**
     * Indicate to building manager that prestige just has been calculated.
     * @param building the building it happened for.
     */
    void clearPendingPrestigeCalc(IBuilding building);

    /**
     * Get the colony prestige.
     * @return the prestige.
     */
    int getColonyPrestige();
}






