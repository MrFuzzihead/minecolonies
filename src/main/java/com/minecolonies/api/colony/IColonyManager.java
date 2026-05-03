package com.minecolonies.api.colony;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.compatibility.ICompatibilityManager;
import com.minecolonies.api.crafting.IRecipeManager;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface IColonyManager
{

    static IColonyManager getInstance()
    {
        return IMinecoloniesAPI.getInstance().getColonyManager();
    }

    /**
     * Create a new Colony in the given world and at that location.
     *
     * @param w          World of the colony.
     * @param pos        Coordinate of the center of the colony.
     * @param EntityPlayer     the EntityPlayer that creates the colony - owner.
     * @param colonyName the initial colony name.
     * @param pack       the default pack of the colony.
     * @return the created colony instance.
     */
    @Nullable
    IColony createColony(@NotNull World w, int[] pos, @NotNull EntityPlayer EntityPlayer, @NotNull String colonyName, @NotNull String pack);

    /**
     * Delete the colony in a world.
     *
     * @param id         the id of it.
     * @param canDestroy if can destroy the buildings.
     * @param world      the world.
     */
    void deleteColonyByWorld(int id, boolean canDestroy, World world);

    /**
     * Delete the colony by dimension.
     *
     * @param id         the id of it.
     * @param canDestroy if can destroy the buildings.
     * @param dimension  the dimension.
     */
    void deleteColonyByDimension(int id, boolean canDestroy, int /* ResourceKey */ dimension);

    /**
     * Removes a colony view
     *
     * @param id        the id of the colony.
     * @param dimension the dimension it is in.
     */
    void removeColonyView(int id, int /* ResourceKey */ dimension);

    /**
     * Get Colony by UUID.
     *
     * @param id    ID of colony.
     * @param world the world it is in.
     * @return Colony with given ID.
     */
    @Nullable
    IColony getColonyByWorld(int id, World world);

    /**
     * Get Colony by UUID.
     *
     * @param id        ID of colony.
     * @param dimension the dimension it is in.
     * @return Colony with given ID.
     */
    @Nullable
    IColony getColonyByDimension(int id, int /* ResourceKey */ dimension);

    /**
     * Get a AbstractBuilding by a World and coordinates.
     *
     * @param w   World.
     * @param pos Block position.
     * @return AbstractBuilding at the given location.
     */
    IBuilding getBuilding(@NotNull World w, @NotNull int[] pos);

    /**
     * Get colony that contains a given coordinate from world.
     *
     * @param w   World.
     * @param pos coordinates.
     * @return Colony at the given location.
     */
    @Nullable
    IColony getColonyByPosFromWorld(@NotNull World w, @NotNull int[] pos);

    /**
     * Get colony that contains a given coordinate from dimension.
     *
     * @param dim the dimension.
     * @param pos coordinates.
     * @return Colony at the given location.
     */
    IColony getColonyByPosFromDim(int /* ResourceKey */ dim, @NotNull int[] pos);

    /**
     * Check if a position is too close to another colony to found a new colony.
     *
     * @param w   World.
     * @param pos coordinates.
     * @return true if so.
     */
    boolean isFarEnoughFromColonies(@NotNull World w, @NotNull int[] pos);

    /**
     * Get all colonies in this world.
     *
     * @param w World.
     * @return a list of colonies.
     */
    @NotNull
    List<IColony> getColonies(@NotNull World w);

    /**
     * Get all colonies in all worlds.
     *
     * @return a list of colonies.
     */
    @NotNull
    List<IColony> getAllColonies();

    /**
     * Get all colonies in all worlds.
     *
     * @param abandonedSince time in hours since the last contact.
     * @return a list of colonies.
     */
    @NotNull
    List<IColony> getColoniesAbandonedSince(int abandonedSince);

    /**
     * Get a AbstractBuilding by position.
     *
     * @param pos       Block position.
     * @param dimension the dimension it is in.
     * @return Returns the view belonging to the building at (x, y, z).
     */
    IBuildingView getBuildingView(final int /* ResourceKey */ dimension, int[] pos);

    /**
     * Get all colonies in this world.  (Side neutral; on clients it returns the subset of views known to the EntityPlayer.)
     *
     * @param w World.
     * @return a list of colonies.
     */
    @NotNull
    List<IColony> getIColonies(@NotNull World w);

    /**
     * Side neutral method to get colony. On clients it returns the view. On servers it returns the colony itself.
     *
     * @param w   World.
     * @param pos coordinates.
     * @return View of colony or colony itself depending on side.
     */
    @Nullable
    IColony getIColony(@NotNull World w, @NotNull int[] pos);

    /**
     * Get all colony views in this world known to the current EntityPlayer.
     *
     * @param w World.
     * @return a list of colony views.
     */
    @NotNull
    List<IColonyView> getColonyViews(@NotNull World w);

    /**
     * Gets the colony view aat the given position
     *
     * @param w
     * @param pos
     * @return IColonyView
     */
    IColonyView getColonyView(@NotNull World w, @NotNull int[] pos);

    /**
     * Side neutral method to get colony. On clients it returns the view. On servers it returns the colony itself. {@link #getClosestColony(World, blockPos)}
     *
     * @param w   World.
     * @param pos Block position.
     * @return View of colony or colony itself depending on side, closest to coordinates.
     */
    @Nullable
    IColony getClosestIColony(@NotNull World w, @NotNull int[] pos);

    /**
     * Returns the closest view {@link #getColonyView}.
     *
     * @param w   World.
     * @param pos Block Position.
     * @return View of the closest colony.
     */
    // TODO: Check usages, some of the probably want to avoid getting a distant colony and use getColonyView at pos instead
    @Nullable
    IColonyView getClosestColonyView(@Nullable World w, @Nullable int[] pos);

    /**
     * Get closest colony by x,y,z.
     *
     * @param w   World.
     * @param pos coordinates.
     * @return Colony closest to coordinates.
     */
    IColony getClosestColony(@NotNull World w, @NotNull int[] pos);

    /**
     * Side neutral method to get colony. On clients it returns the view. On servers it returns the colony itself.
     * <p>
     * Returns a colony or view with the given EntityPlayer as owner.
     *
     * @param w     World.
     * @param owner Entity EntityPlayer.
     * @return IColony belonging to specific EntityPlayer.
     */
    @Nullable
    IColony getIColonyByOwner(@NotNull World w, @NotNull EntityPlayer owner);

    /**
     * Side neutral method to get colony. On clients it returns the view. On servers it returns the colony itself.
     * <p>
     * Returns a colony or view with given EntityPlayer as owner.
     *
     * @param w     World
     * @param owner UUID of the owner.
     * @return IColony belonging to specific EntityPlayer.
     */
    @Nullable
    IColony getIColonyByOwner(@NotNull World w, UUID owner);

    /**
     * Returns the minimum distance between two town halls, to not make colonies collide.
     *
     * @return Minimum town hall distance.
     */
    int getMinimumDistanceBetweenTownHalls();

    /**
     * On server tick, tick every Colony. NOTE: Review this for performance.
     *
     * @param event {@link net.minecraftforge.event.TickEvent.ServerTickEvent}
     */
    void onServerTick(@NotNull TickEvent.ServerTickEvent event);

    /**
     * Write colonies to NBT data for saving.
     *
     * @param compound NBT-NBTBase.
     */
    void write(@NotNull NBTTagCompound compound);

    /**
     * Read Colonies from saved NBT data.
     *
     * @param compound NBT NBTBase.
     */
    void read(@NotNull NBTTagCompound compound);

    /**
     * On Client tick, clears views when EntityPlayer left.
     *
     * @param event {@link TickEvent.ClientTickEvent}.
     */
    void onClientTick(@NotNull TickEvent.ClientTickEvent event);

    /**
     * On world tick, tick every Colony in that world. NOTE: Review this for performance.
     */
    void onWorldTick(@NotNull TickEvent.WorldTickEvent event);

    /**
     * When a world is loaded, Colonies in that world need to grab the reference to the World. Additionally, when loading the first world, load the manager data.
     *
     * @param world World.
     */
    void onWorldLoad(@NotNull World world);

    /**
     * Sets the cap for this world to loaded
     */
    void setCapLoaded();

    /**
     * When a world unloads, all colonies in that world are informed. Additionally, when the last world is unloaded, delete all colonies.
     *
     * @param world World.
     */
    void onWorldUnload(@NotNull World world);

    /**
     * Sends view message to the right view.
     *
     * @param colonyId          ID of the colony.
     * @param colonyData        {@link PacketBuffer} with colony data.
     * @param isNewSubscription whether this is a new subscription or not.
     * @param dim               the dimension.
     * @param world             the world it is in.
     */
    void handleColonyViewMessage(int colonyId, @NotNull PacketBuffer colonyData, @NotNull World world, boolean isNewSubscription, int /* ResourceKey */ dim);

    /**
     * Get IColonyView by ID.
     *
     * @param id        ID of colony.
     * @param dimension the dimension id.
     * @return The IColonyView belonging to the colony.
     */
    IColonyView getColonyView(int id, final int /* ResourceKey */ dimension);

    /**
     * Returns result of {@link IColonyView#handlePermissionsViewMessage(PacketBuffer)} if {@link #getColonyView(int, ResourceKey)}. gives a not-null result. If {@link #getColonyView(int,
     * ResourceKey)} is null, returns null.
     *
     * @param colonyID ID of the colony.
     * @param data     {@link PacketBuffer} with colony data.
     * @param dim      the dimension.
     */
    void handlePermissionsViewMessage(int colonyID, @NotNull PacketBuffer data, int /* ResourceKey */ dim);

    /**
     * Returns result of {@link IColonyView#handleColonyViewCitizensMessage(int, PacketBuffer)} if {@link #getColonyView(int, ResourceKey)} gives a not-null result. If {@link
     * #getColonyView(int, ResourceKey)} is null, returns null.
     *
     * @param colonyId  ID of the colony.
     * @param citizenId ID of the citizen.
     * @param buf       {@link PacketBuffer} with colony data.
     * @param dim       the dimension.
     */
    void handleColonyViewCitizensMessage(int colonyId, int citizenId, PacketBuffer buf, int /* ResourceKey */ dim);

    /**
     * Returns result of {@link IColonyView#handleColonyViewWorkOrderMessage(PacketBuffer)} (int, ByteBuf)} if {@link #getColonyView(int, ResourceKey)} gives a not-null result. If {@link
     * #getColonyView(int, ResourceKey)} is null, returns null.
     *
     * @param colonyId ID of the colony.
     * @param buf      {@link PacketBuffer} with colony data.
     * @param dim      the dimension.
     */
    void handleColonyViewWorkOrderMessage(int colonyId, PacketBuffer buf, int /* ResourceKey */ dim);

    /**
     * Returns result of {@link IColonyView#handleColonyViewRemoveCitizenMessage(int)} if {@link #getColonyView(int, ResourceKey)} gives a not-null result. If {@link #getColonyView(int,
     * ResourceKey)} is null, returns null.
     *
     * @param colonyId  ID of the colony.
     * @param citizenId ID of the citizen.
     * @param dim       the dimension.
     */
    void handleColonyViewRemoveCitizenMessage(int colonyId, int citizenId, int /* ResourceKey */ dim);

    /**
     * Returns result of {@link IColonyView#handleColonyBuildingViewMessage(blockPos, PacketBuffer)} if {@link #getColonyView(int, ResourceKey)} gives a not-null result. If {@link
     * #getColonyView(int, ResourceKey)} is null, returns null.
     *
     * @param colonyId   ID of the colony.
     * @param buildingId ID of the building.
     * @param buf        {@link PacketBuffer} with colony data.
     * @param dim        the dimension.
     */
    void handleColonyBuildingViewMessage(int colonyId, int[] buildingId, @NotNull PacketBuffer buf, int /* ResourceKey */ dim);

    /**
     * Returns result of {@link IColonyView#handleColonyViewRemoveBuildingMessage(blockPos)} if {@link #getColonyView(int, ResourceKey)} gives a not-null result. If {@link
     * #getColonyView(int, ResourceKey)} is null, returns null.
     *
     * @param colonyId   ID of the colony.
     * @param buildingId ID of the building.
     * @param dim        the dimension.
     */
    void handleColonyViewRemoveBuildingMessage(int colonyId, final int[] buildingId, final int /* ResourceKey */ dim);

    /**
     * Returns result of {@link IColonyView#handleColonyViewRemoveWorkOrderMessage(int)} if {@link #getColonyView(int, ResourceKey)} gives a not-null result. If {@link #getColonyView(int,
     * ResourceKey)} is null, returns null.
     *
     * @param colonyId    ID of the colony.
     * @param workOrderId ID of the workOrder.
     * @param dim         the dimension.
     */
    void handleColonyViewRemoveWorkOrderMessage(int colonyId, int workOrderId, int /* ResourceKey */ dim);

    /**
     * Whether or not a new schematic have been downloaded.
     *
     * @return True if a new schematic have been received.
     */
    boolean isSchematicDownloaded();

    /**
     * Set the schematic downloaded
     *
     * @param downloaded True if a new schematic have been received.
     */
    void setSchematicDownloaded(boolean downloaded);

    /**
     * Check if a given coordinate is inside any other colony.
     *
     * @param world the world to check in.
     * @param pos   the position to check.
     * @return true if a colony has been found.
     */
    boolean isCoordinateInAnyColony(@NotNull World world, int[] pos);

    /**
     * Get an instance of the compatibilityManager.
     *
     * @return the manager.
     */
    ICompatibilityManager getCompatibilityManager();

    /**
     * Getter for the recipeManager.
     *
     * @return an IRecipeManager.
     */
    IRecipeManager getRecipeManager();

    /**
     * Get the top colony id of all colonies.
     *
     * @return the top id.
     */
    int getTopColonyId();

    /**
     * Reset all colony views on login to new world.
     */
    void resetColonyViews();

    /**
     * Open the new reactivation window.
     * @param pos the pos to open it at.
     */
    void openReactivationWindow(final int[] pos);
}




