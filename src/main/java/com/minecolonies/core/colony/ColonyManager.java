package com.minecolonies.core.colony;
import net.minecraft.util.Direction;
import net.minecraft.world.level.chunk.LevelChunk;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

// [1.7.10 BACKPORT]
// - world.getCapability(COLONY_MANAGER_CAP, ...) → ColonyManagerWorldSavedData.getOrCreate(world).getCapability()
// - int /* ResourceKey */ → int (dimension ID)
// - LevelChunk → Chunk
// - World → World
// - int[] → int x/y/z
// - EntityPlayerMP → EntityPlayerMP
// - Player → EntityPlayer
// - NBTTagCompound → NBTTagCompound
// - PacketBuffer → PacketBuffer
// - ServerLifecycleHooks.getCurrentServer() → MinecraftServer.getServer()
// - server.getAllLevels() → server.worldServers (WorldServer[])
// - server.getLevel(key) → server.worldServerForDimension(dimId)
// - world.isClientSide → world.isRemote
// - Minecraft.getInstance().World → Minecraft.getMinecraft().theWorld
// - TickEvent.LevelTickEvent → TickEvent.WorldTickEvent
// - world.dimension() → world.provider.dimensionId

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.permissions.ColonyPlayer;
import com.minecolonies.api.compatibility.CompatibilityManager;
import com.minecolonies.api.compatibility.ICompatibilityManager;
import com.minecolonies.api.crafting.IRecipeManager;
import com.minecolonies.api.eventbus.events.ColonyManagerLoadedModEvent;
import com.minecolonies.api.eventbus.events.ColonyManagerUnloadedModEvent;
import com.minecolonies.api.eventbus.events.colony.ColonyDeletedModEvent;
import com.minecolonies.api.eventbus.events.colony.ColonyViewUpdatedModEvent;
import com.minecolonies.api.sounds.SoundManager;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.ColonyUtils;
import com.minecolonies.api.util.DamageSourceKeys;
import com.minecolonies.api.util.Log;
import com.minecolonies.core.MineColonies;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.WindowReactivateBuilding;
import com.minecolonies.core.colony.requestsystem.management.manager.StandardRecipeManager;
import com.minecolonies.core.network.messages.client.colony.ColonyViewRemoveMessage;
import com.minecolonies.core.util.BackUpHelper;
import com.minecolonies.core.util.ChunkDataHelper;
import io.netty.buffer.ByteBuf;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
// [1.7.10] SubscribeEvent package is cpw.mods.fml in 1.7.10
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.Direction;
import net.minecraft.world.level.chunk.LevelChunk;
import cpw.mods.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.minecolonies.api.util.constant.ColonyManagerConstants.*;
import static com.minecolonies.api.util.constant.Constants.BLOCKS_PER_CHUNK;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_COMPATABILITY_MANAGER;
import static com.minecolonies.core.MineColonies.getConfig;

/**
 * Singleton class that links colonies to minecraft.
 */
@SuppressWarnings("PMD.ExcessiveClassLength")
public final class ColonyManager implements IColonyManager
{
    /**
     * The list of colony views, keyed by dimension ID.
     * [1.7.10 BACKPORT] int (ResourceKey removed) → int (dimension ID)
     */
    @NotNull
    private final Map<Integer, ColonyList<IColonyView>> colonyViews = new HashMap<>();

    /**
     * Recipemanager of this server.
     */
    private final IRecipeManager recipeManager = new StandardRecipeManager();

    /**
     * Creates a new compatibilityManager.
     */
    private final ICompatibilityManager compatibilityManager = new CompatibilityManager();

    /**
     * Indicate if a schematic have just been downloaded. Client only
     */
    private boolean schematicDownloaded = false;

    /**
     * If the manager finished loading already.
     */
    private boolean capLoaded = false;

    /**
     * Client side sound manager.
     */
    private SoundManager clientSoundManager;

    @Override
    public IColony createColony(@NotNull final World w, final int posX, final int posY, final int posZ,
                                @NotNull final EntityPlayer player, @NotNull final String colonyName, @NotNull final String pack)
    {
        final IColonyManagerCapability cap = ColonyManagerWorldSavedData.getOrCreate(w).getCapability();
        if (cap == null)
        {
            Log.getLogger().warn(MISSING_WORLD_CAP_MESSAGE);
            return null;
        }

        final IColony colony = cap.createColony(w, posX, posY, posZ);
        colony.setStructurePack(pack);
        colony.setName(colonyName);
        colony.getPermissions().setOwner(player);

        colony.getPackageManager().addImportantColonyPlayer((EntityPlayerMP) player);
        colony.getPackageManager().addCloseSubscriber((EntityPlayerMP) player);

        Log.getLogger().info(String.format("New Colony Id: %d by %s", colony.getID(), player.getCommandSenderName()));

        if (colony.getWorld() == null)
        {
            Log.getLogger().error("Unable to claim chunks because of the missing world in the colony, please report this to the mod authors!", new Exception());
            return null;
        }

        final int cx = colony.getCenter().getX();
        final int cy = colony.getCenter().getY();
        final int cz = colony.getCenter().getZ();
        ChunkDataHelper.claimColonyChunks(colony.getWorld(), true, colony.getID(), cx, cy, cz);
        return colony;
    }

    @Override
    public void deleteColonyByWorld(final int id, final boolean canDestroy, final World world)
    {
        deleteColony(getColonyByWorld(id, world), canDestroy);
    }

    @Override
    public void deleteColonyByDimension(final int id, final boolean canDestroy, final int dimension)
    {
        deleteColony(getColonyByDimension(id, dimension), canDestroy);
    }

    /**
     * Delete a colony and purge all buildings and citizens.
     *
     * @param iColony    the colony to destroy.
     * @param canDestroy if the building outlines should be destroyed as well.
     */
    private void deleteColony(@Nullable final IColony iColony, final boolean canDestroy)
    {
        if (!(iColony instanceof Colony))
        {
            return;
        }

        final Colony colony = (Colony) iColony;
        final int id = colony.getID();
        final World world = colony.getWorld();

        if (world == null)
        {
            Log.getLogger().warn("Deleting Colony " + id + " errored: World is Null");
            return;
        }

        try
        {
            final int cx = colony.getCenter().getX();
            final int cy = colony.getCenter().getY();
            final int cz = colony.getCenter().getZ();
            ChunkDataHelper.claimColonyChunks(world, false, id, cx, cy, cz);
            Log.getLogger().info("Removing citizens for " + id);
            for (final ICitizenData citizenData : new ArrayList<>(colony.getCitizenManager().getCitizens()))
            {
                Log.getLogger().info("Kill Citizen " + citizenData.getName());
                // [1.7.10 BACKPORT] damageSources().source() → net.minecraft.util.DamageSource.generic or custom
                citizenData.getEntity().ifPresent(entityCitizen -> entityCitizen.attackEntityFrom(
                  net.minecraft.util.net.minecraft.util.DamageSource.generic, Float.MAX_VALUE));
            }

            Log.getLogger().info("Removing buildings for " + id);
            for (final IBuilding building : new ArrayList<>(colony.getServerBuildingManager().getBuildings().values()))
            {
                try
                {
                    final int bx = building.getPosition().getX();
                    final int by = building.getPosition().getY();
                    final int bz = building.getPosition().getZ();
                    Log.getLogger().info("Delete Building at " + bx + "," + by + "," + bz);
                    if (canDestroy)
                    {
                        building.deconstruct();
                    }
                    building.destroy();
                    // [1.7.10 BACKPORT] world.getBlockState(pos).getBlock() → world.getBlock(x,y,z)
                    if (world.getBlock(bx, by, bz) instanceof AbstractBlockHut)
                    {
                        Log.getLogger().info("Found Block, deleting " + world.getBlock(bx, by, bz));
                        world.setBlockToAir(bx, by, bz);
                    }
                }
                catch (final Exception ex)
                {
                    Log.getLogger().warn("Something went wrong deleting a building while deleting the colony!", ex);
                }
            }

            try
            {
                MinecraftForge.EVENT_BUS.unregister(colony.getEventHandler());
            }
            catch (final NullPointerException e)
            {
                Log.getLogger().warn("Can't unregister the event handler twice");
            }

            Log.getLogger().info("Deleting colony: " + colony.getID());

            final IColonyManagerCapability cap = ColonyManagerWorldSavedData.getOrCreate(world).getCapability();
            if (cap == null)
            {
                Log.getLogger().warn(MISSING_WORLD_CAP_MESSAGE);
                return;
            }

            IMinecoloniesAPI.getInstance().getEventBus().post(new ColonyDeletedModEvent(colony));
            cap.deleteColony(id);
            BackUpHelper.markColonyDeleted(colony.getID(), colony.getDimension());
            colony.getImportantMessageEntityPlayers()
              .forEach(player -> Network.getNetwork().sendToPlayer(
                new ColonyViewRemoveMessage(colony.getID(), colony.getDimension()), (EntityPlayerMP) player));
            Log.getLogger().info("Successfully deleted colony: " + id);
        }
        catch (final RuntimeException e)
        {
            Log.getLogger().warn("Deleting Colony " + id + " errored:", e);
        }
    }

    @Override
    public void removeColonyView(final int id, final int dimension)
    {
        if (colonyViews.containsKey(dimension))
        {
            colonyViews.get(dimension).remove(id);
        }
    }

    @Override
    @Nullable
    public IColony getColonyByWorld(final int id, final World world)
    {
        final IColonyManagerCapability cap = ColonyManagerWorldSavedData.getOrCreate(world).getCapability();
        if (cap == null)
        {
            Log.getLogger().warn(MISSING_WORLD_CAP_MESSAGE);
            return null;
        }
        return cap.getColony(id);
    }

    @Override
    @Nullable
    public IColony getColonyByDimension(final int id, final int dimensionId)
    {
        final MinecraftServer server = MinecraftServer.getServer();
        if (server == null)
        {
            return null;
        }
        final World world = server.worldServerForDimension(dimensionId);
        if (world == null)
        {
            return null;
        }
        final IColonyManagerCapability cap = ColonyManagerWorldSavedData.getOrCreate(world).getCapability();
        if (cap == null)
        {
            Log.getLogger().warn(MISSING_WORLD_CAP_MESSAGE);
            return null;
        }
        return cap.getColony(id);
    }

    @Override
    public IBuilding getBuilding(@NotNull final World w, final int posX, final int posY, final int posZ)
    {
        @Nullable final IColony colony = getColonyByPosFromWorld(w, posX, posY, posZ);
        if (colony != null)
        {
            final IBuilding building = colony.getServerBuildingManager().getBuilding(posX, posY, posZ);
            if (building != null)
            {
                return building;
            }
        }

        //  Fallback - there might be a AbstractBuilding for this block, but it's outside its owning colony's radius.
        for (@NotNull final IColony otherColony : getColonies(w))
        {
            final IBuilding building = otherColony.getServerBuildingManager().getBuilding(posX, posY, posZ);
            if (building != null)
            {
                return building;
            }
        }

        return null;
    }

    @Override
    public IColony getColonyByPosFromWorld(@Nullable final World w, final int posX, final int posY, final int posZ)
    {
        if (w == null)
        {
            return null;
        }
        final Chunk centralChunk = (Chunk) w.getChunkFromBlockCoords(posX, posZ);
        final int id = ColonyUtils.getOwningColony(centralChunk);
        if (id == NO_COLONY_ID)
        {
            return null;
        }
        return getColonyByWorld(id, w);
    }

    @Override
    public IColony getColonyByPosFromDim(final int dimensionId, final int posX, final int posY, final int posZ)
    {
        final MinecraftServer server = MinecraftServer.getServer();
        if (server == null) return null;
        return getColonyByPosFromWorld(server.worldServerForDimension(dimensionId), posX, posY, posZ);
    }

    @Override
    public boolean isFarEnoughFromColonies(@NotNull final World w, final int posX, final int posY, final int posZ)
    {
        final int blockRange = Math.max(MineColonies.getConfig().getServer().minColonyDistance.get(), getConfig().getServer().initialColonySize.get()) << 4;
        final IColony closest = getClosestColony(w, posX, posY, posZ);

        if (closest != null && BlockPosUtil.getDistance(posX, posY, posZ, closest.getCenter().getX(), closest.getCenter().getY(), closest.getCenter().getZ()) < blockRange)
        {
            return false;
        }

        return ChunkDataHelper.canClaimChunksInRange(w, posX, posY, posZ, getConfig().getServer().initialColonySize.get());
    }

    @Override
    @NotNull
    public List<IColony> getColonies(@NotNull final World w)
    {
        final IColonyManagerCapability cap = ColonyManagerWorldSavedData.getOrCreate(w).getCapability();
        if (cap == null)
        {
            Log.getLogger().warn(MISSING_WORLD_CAP_MESSAGE);
            return Collections.emptyList();
        }
        return cap.getColonies();
    }

    @Override
    @NotNull
    public List<IColony> getAllColonies()
    {
        final List<IColony> allColonies = new ArrayList<>();
        final MinecraftServer server = MinecraftServer.getServer();
        if (server == null) return allColonies;
        // [1.7.10 BACKPORT] server.worldServers is WorldServer[] in 1.7.10
        for (final net.minecraft.world.WorldServer world : server.worldServers)
        {
            if (world != null)
            {
                final IColonyManagerCapability cap = ColonyManagerWorldSavedData.getOrCreate(world).getCapability();
                if (cap != null)
                {
                    allColonies.addAll(cap.getColonies());
                }
            }
        }
        return allColonies;
    }

    @Override
    @NotNull
    public List<IColony> getColoniesAbandonedSince(final int abandonedSince)
    {
        final List<IColony> sortedList = new ArrayList<>();
        for (final IColony colony : getAllColonies())
        {
            if (colony.getLastContactInHours() >= abandonedSince)
            {
                sortedList.add(colony);
            }
        }
        return sortedList;
    }

    @Override
    public IBuildingView getBuildingView(final int dimension, final int posX, final int posY, final int posZ)
    {
        if (colonyViews.containsKey(dimension))
        {
            for (@NotNull final IColonyView colony : colonyViews.get(dimension))
            {
                final IBuildingView building = colony.getClientBuildingManager().getBuilding(posX, posY, posZ);
                if (building != null)
                {
                    return building;
                }
            }
        }
        return null;
    }

    @Override
    @NotNull
    public List<IColony> getIColonies(@NotNull final World w)
    {
        return w.isRemote ? new ArrayList<>(getColonyViews(w)) : getColonies(w);
    }

    @Override
    @Nullable
    public IColony getIColony(@NotNull final World w, final int posX, final int posY, final int posZ)
    {
        return w.isRemote ? getColonyView(w, posX, posY, posZ) : getColonyByPosFromWorld(w, posX, posY, posZ);
    }

    @Override
    public void openReactivationWindow(final int posX, final int posY, final int posZ)
    {
        new WindowReactivateBuilding(posX, posY, posZ).open();
    }

    @Override
    @NotNull
    public List<IColonyView> getColonyViews(@NotNull final World w)
    {
        // this might be a subset of colonies since it's only those known to the player right now
        final ColonyList<IColonyView> colonies = colonyViews.get(w.provider.dimensionId);
        return colonies == null ? Collections.emptyList() : new ArrayList<>(colonies.getCopyAsList());
    }

    /**
     * Get Colony that contains a given (x, y, z).
     */
    @Override
    public IColonyView getColonyView(@NotNull final World w, final int posX, final int posY, final int posZ)
    {
        final Chunk centralChunk = (Chunk) w.getChunkFromBlockCoords(posX, posZ);
        final int id = ColonyUtils.getOwningColony(centralChunk);
        if (id == 0)
        {
            return null;
        }
        return getColonyView(id, w.provider.dimensionId);
    }

    @Override
    @Nullable
    public IColony getClosestIColony(@NotNull final World w, final int posX, final int posY, final int posZ)
    {
        return w.isRemote ? getClosestColonyView(w, posX, posY, posZ) : getClosestColony(w, posX, posY, posZ);
    }

    @Override
    @Nullable
    public IColonyView getClosestColonyView(@Nullable final World w, final int posX, final int posY, final int posZ)
    {
        if (w == null)
        {
            return null;
        }

        final Chunk chunk = (Chunk) w.getChunkFromBlockCoords(posX, posZ);
        final int owningColony = ColonyUtils.getOwningColony(chunk);
        if (owningColony != NO_COLONY_ID)
        {
            return getColonyView(owningColony, w.provider.dimensionId);
        }

        @Nullable IColonyView closestColony = null;
        long closestDist = Long.MAX_VALUE;

        if (colonyViews.containsKey(w.provider.dimensionId))
        {
            for (@NotNull final IColonyView c : colonyViews.get(w.provider.dimensionId))
            {
                if (c.getDimension() == w.provider.dimensionId && c.getCenter() != null)
                {
                    final long dist = c.getDistanceSquared(posX, posY, posZ);
                    if (dist < closestDist)
                    {
                        closestColony = c;
                        closestDist = dist;
                    }
                }
            }
        }

        return closestColony;
    }

    @Override
    public IColony getClosestColony(@NotNull final World w, final int posX, final int posY, final int posZ)
    {
        final Chunk chunk = (Chunk) w.getChunkFromBlockCoords(posX, posZ);
        final int owningColony = ColonyUtils.getOwningColony(chunk);
        if (owningColony != NO_COLONY_ID)
        {
            return getColonyByWorld(owningColony, w);
        }

        @Nullable IColony closestColony = null;
        long closestDist = Long.MAX_VALUE;

        for (@NotNull final IColony c : getColonies(w))
        {
            if (c.getDimension() == w.provider.dimensionId)
            {
                final long dist = c.getDistanceSquared(posX, posY, posZ);
                if (dist < closestDist)
                {
                    closestColony = c;
                    closestDist = dist;
                }
            }
        }

        return closestColony;
    }

    @Override
    @Nullable
    public IColony getIColonyByOwner(@NotNull final World w, @NotNull final EntityPlayer owner)
    {
        return getIColonyByOwner(w, w.isRemote ? owner.getUniqueID() : owner.getGameProfile().getId());
    }

    @Override
    @Nullable
    public IColony getIColonyByOwner(@NotNull final World w, final UUID owner)
    {
        return w.isRemote ? getColonyViewByOwner(owner, w.provider.dimensionId) : getColonyByOwner(owner);
    }

    /**
     * Returns a ColonyView with specific owner.
     *
     * @param owner     UUID of the owner.
     * @param dimension the dimension id.
     * @return ColonyView.
     */
    private IColony getColonyViewByOwner(final UUID owner, final int dimension)
    {
        if (colonyViews.containsKey(dimension))
        {
            for (@NotNull final IColonyView c : colonyViews.get(dimension))
            {
                final ColonyPlayer p = c.getPlayers().get(owner);
                if (p != null && p.getRank().equals(c.getPermissions().getRankOwner()))
                {
                    return c;
                }
            }
        }
        return null;
    }

    @Nullable
    private IColony getColonyByOwner(@Nullable final UUID owner)
    {
        if (owner == null)
        {
            return null;
        }

        for (final IColony colony : getAllColonies())
        {
            if (colony.getPermissions().getOwner().equals(owner))
            {
                return colony;
            }
        }
        return null;
    }

    @Override
    public int getMinimumDistanceBetweenTownHalls()
    {
        //  [TownHall](Radius)+(Padding)+(Radius)[TownHall]
        return getConfig().getServer().minColonyDistance.get() * BLOCKS_PER_CHUNK;
    }

    @Override
    public void onServerTick(final TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            for (@NotNull final IColony c : getAllColonies())
            {
                c.onServerTick(event);
            }
        }
    }

    @Override
    public void write(@NotNull final NBTTagCompound compound)
    {
        final NBTTagCompound compCompound = new NBTTagCompound();
        compatibilityManager.write(compCompound);
        compound.setTag(TAG_COMPATABILITY_MANAGER, compCompound);

        compound.setBoolean(TAG_DISTANCE, true);
        final NBTTagCompound recipeCompound = new NBTTagCompound();
        recipeManager.write(recipeCompound);
        compound.setTag(RECIPE_MANAGER_TAG, recipeCompound);
    }

    @Override
    public void read(@NotNull final NBTTagCompound compound)
    {
        if (compound.hasKey(TAG_COMPATABILITY_MANAGER))
        {
            compatibilityManager.read(compound.getCompoundTag(TAG_COMPATABILITY_MANAGER));
        }
        recipeManager.read(compound.getCompoundTag(RECIPE_MANAGER_TAG));
    }

    @Override
    public void onClientTick(final TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            // [1.7.10 BACKPORT] Minecraft.getInstance().World → Minecraft.getMinecraft().theWorld
            if (Minecraft.getMinecraft().theWorld == null && !colonyViews.isEmpty())
            {
                //  Player has left the game, clear the Colony View cache
                colonyViews.clear();
            }

            if (clientSoundManager == null)
            {
                clientSoundManager = new SoundManager();
            }
            clientSoundManager.tick();
        }
    }

    @Override
    public void onWorldTick(final TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            for (final IColony colony : getColonies(event.world))
            {
                try
                {
                    colony.onWorldTick(event);
                }
                catch (final Exception ex)
                {
                    Log.getLogger().error("Something went wrong ticking colony: " + colony.getID(), ex);
                }
            }
        }
    }

    @Override
    public void onWorldLoad(@NotNull final World world)
    {
        if (!world.isRemote)
        {
            // Late-load restore if cap was not loaded
            if (!capLoaded)
            {
                BackUpHelper.loadMissingColonies();
                BackUpHelper.loadManagerBackup();
            }
            capLoaded = false;

            for (@NotNull final IColony c : getColonies(world))
            {
                c.onWorldLoad(world);
            }

            IMinecoloniesAPI.getInstance().getEventBus().post(new ColonyManagerLoadedModEvent(this));
        }
    }

    @Override
    public void setCapLoaded()
    {
        this.capLoaded = true;
    }

    @Override
    public void onWorldUnload(@NotNull final World world)
    {
        if (!world.isRemote)
        {
            boolean hasColonies = false;
            for (@NotNull final IColony c : getColonies(world))
            {
                hasColonies = true;
                c.onWorldUnload(world);
            }

            if (hasColonies)
            {
                BackUpHelper.backupColonyData();
            }

            IMinecoloniesAPI.getInstance().getEventBus().post(new ColonyManagerUnloadedModEvent(this));
        }
    }

    @Override
    public void handleColonyViewMessage(
      final int colonyId,
      @NotNull final ByteBuf colonyData,
      @NotNull final World world,
      final boolean isNewSubscription,
      final int dim)
    {
        IColonyView view = getColonyView(colonyId, dim);
        if (view == null)
        {
            view = ColonyView.createFromNetwork(colonyId);
            if (colonyViews.containsKey(dim))
            {
                colonyViews.get(dim).add(view);
            }
            else
            {
                final ColonyList<IColonyView> list = new ColonyList<>();
                list.add(view);
                colonyViews.put(dim, list);
            }
        }
        view.handleColonyViewMessage(colonyData, world, isNewSubscription);

        IMinecoloniesAPI.getInstance().getEventBus().post(new ColonyViewUpdatedModEvent(view));
    }

    @Override
    public IColonyView getColonyView(final int id, final int dimension)
    {
        if (colonyViews.containsKey(dimension))
        {
            return colonyViews.get(dimension).get(id);
        }
        return null;
    }

    @Override
    public void handlePermissionsViewMessage(final int colonyID, @NotNull final ByteBuf data, final int dim)
    {
        final IColonyView view = getColonyView(colonyID, dim);
        if (view == null)
        {
            Log.getLogger().error(String.format("Colony view does not exist for ID #%d", colonyID), new Exception());
        }
        else
        {
            view.handlePermissionsViewMessage(data);
        }
    }

    @Override
    public void handleColonyViewCitizensMessage(final int colonyId, final int citizenId, final ByteBuf buf, final int dim)
    {
        final IColonyView view = getColonyView(colonyId, dim);
        if (view == null)
        {
            return;
        }
        view.handleColonyViewCitizensMessage(citizenId, buf);
    }

    @Override
    public void handleColonyViewWorkOrderMessage(final int colonyId, final ByteBuf buf, final int dim)
    {
        final IColonyView view = getColonyView(colonyId, dim);
        if (view == null)
        {
            return;
        }
        view.handleColonyViewWorkOrderMessage(buf);
    }

    @Override
    public void handleColonyViewRemoveCitizenMessage(final int colonyId, final int citizenId, final int dim)
    {
        final IColonyView view = getColonyView(colonyId, dim);
        if (view != null)
        {
            view.handleColonyViewRemoveCitizenMessage(citizenId);
        }
    }

    @Override
    public void handleColonyBuildingViewMessage(final int colonyId, final int buildingX, final int buildingY, final int buildingZ,
                                                @NotNull final ByteBuf buf, final int dim)
    {
        final IColonyView view = getColonyView(colonyId, dim);
        if (view != null)
        {
            view.getClientBuildingManager().handleColonyBuildingViewMessage(buildingX, buildingY, buildingZ, buf);
        }
        else
        {
            Log.getLogger().error(String.format("Colony view does not exist for ID #%d", colonyId), new Exception());
        }
    }

    @Override
    public void handleColonyViewRemoveBuildingMessage(final int colonyId, final int buildingX, final int buildingY, final int buildingZ, final int dim)
    {
        final IColonyView view = getColonyView(colonyId, dim);
        if (view != null)
        {
            view.getClientBuildingManager().handleColonyViewRemoveBuildingMessage(buildingX, buildingY, buildingZ);
        }
    }

    @Override
    public void handleColonyViewRemoveWorkOrderMessage(final int colonyId, final int workOrderId, final int dim)
    {
        final IColonyView view = getColonyView(colonyId, dim);
        if (view != null)
        {
            view.handleColonyViewRemoveWorkOrderMessage(workOrderId);
        }
    }

    @Override
    public boolean isSchematicDownloaded()
    {
        return schematicDownloaded;
    }

    @Override
    public void setSchematicDownloaded(final boolean downloaded)
    {
        schematicDownloaded = downloaded;
    }

    @Override
    public boolean isCoordinateInAnyColony(@NotNull final World world, final int posX, final int posY, final int posZ)
    {
        final Chunk centralChunk = (Chunk) world.getChunkFromBlockCoords(posX, posZ);
        return ColonyUtils.getOwningColony(centralChunk) != NO_COLONY_ID;
    }

    @Override
    public ICompatibilityManager getCompatibilityManager()
    {
        return compatibilityManager;
    }

    @Override
    public IRecipeManager getRecipeManager()
    {
        return recipeManager;
    }

    @Override
    public int getTopColonyId()
    {
        int top = 0;
        final MinecraftServer server = MinecraftServer.getServer();
        if (server == null) return top;
        for (final net.minecraft.world.WorldServer world : server.worldServers)
        {
            if (world != null)
            {
                final IColonyManagerCapability cap = ColonyManagerWorldSavedData.getOrCreate(world).getCapability();
                if (cap != null)
                {
                    final int tempTop = cap.getTopID();
                    if (tempTop > top)
                    {
                        top = tempTop;
                    }
                }
            }
        }
        return top;
    }

    @Override
    public void resetColonyViews()
    {
        colonyViews.clear();
    }
}





