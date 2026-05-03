package com.minecolonies.core.util;

// [1.7.10 BACKPORT] Ported capability accesses to ChunkAPI / WorldSavedData replacements.
// - world.getCapability(CHUNK_STORAGE_UPDATE_CAP, ...) â†’ ChunkManagerWorldSavedData.getOrCreate(world).getCapability()
// - chunk.getCapability(CLOSE_COLONY_CAP, ...) â†’ ColonyChunkDataHandler.getColonyTagCapability(chunk)
// - LevelChunk â†’ Chunk
// - World â†’ World
// - int[] â†’ int x/y/z (stored in ChunkCoordinates where needed)
// - ChunkPos â†’ int chunkX, chunkZ
// - SectionPos.blockToSectionCoord(x) â†’ x >> 4
// - world.dimension().location() â†’ world.provider.dimensionId (as int, stored as String for now)
// - ChunkPos.asLong(x,z) â†’ (long)x << 32 | (z & 0xFFFFFFFFL)

import com.minecolonies.api.colony.IChunkmanagerCapability;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyTagCapability;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.util.*;
import com.minecolonies.core.MineColonies;
import com.minecolonies.core.Network;
import com.minecolonies.core.colony.ChunkManagerWorldSavedData;
import com.minecolonies.core.colony.ColonyChunkDataHandler;
import com.minecolonies.core.network.messages.client.UpdateChunkCapabilityMessage;
import com.minecolonies.api.util.Tuple;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.ColonyManagerConstants.UNABLE_TO_FIND_WORLD_CAP_TEXT;
import static com.minecolonies.api.util.constant.Constants.BLOCKS_PER_CHUNK;
import static com.minecolonies.api.util.constant.TranslationConstants.COLONY_SIZE_CHANGE;
import static com.minecolonies.core.MineColonies.*;

/**
 * Class to take care of chunk data helper.
 */
public final class ChunkDataHelper
{
    /** Encode a chunk position as a long, matching ChunkPos.asLong(x,z) from 1.21. */
    public static long chunkPosAsLong(final int chunkX, final int chunkZ)
    {
        return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
    }

    private ChunkDataHelper()
    {
        /*
         * Intentionally left empty.
         */
    }

    /**
     * Load the colony info for a certain chunk.
     *
     * @param chunk the chunk.
     * @param world the world.
     */
    public static void loadChunk(final Chunk chunk, final World world)
    {
        final int distanceToDelete = MineColonies.getConfig().getServer().maxColonySize.get() * BLOCKS_PER_CHUNK * 2 * 5;

        final IChunkmanagerCapability chunkManager = ChunkManagerWorldSavedData.getOrCreate(world).getCapability();
        if (chunkManager == null)
        {
            Log.getLogger().error(UNABLE_TO_FIND_WORLD_CAP_TEXT, new Exception());
            return;
        }

        if (!chunkManager.getAllChunkStorages().isEmpty())
        {
            final ChunkLoadStorage existingStorage = chunkManager.getChunkStorage(chunk.xPosition, chunk.zPosition);
            if (existingStorage != null)
            {
                addStorageToChunk(chunk, existingStorage);
            }
        }

        final int closeColony = ColonyUtils.getOwningColony(chunk);
        if (closeColony != 0)
        {
            final IColony colony = IColonyManager.getInstance().getColonyByDimension(closeColony, world.provider.dimensionId);
            if (colony != null)
            {
                colony.addLoadedChunk(chunkPosAsLong(chunk.xPosition, chunk.zPosition), chunk);
            }
        }
    }

    /**
     * Called when a chunk is unloaded.
     *
     * @param world the world it is unloading in.
     * @param chunk the chunk that is unloading.
     */
    public static void unloadChunk(final Chunk chunk, final World world)
    {
        final int closeColony = ColonyUtils.getOwningColony(chunk);
        if (closeColony != 0)
        {
            final IColony colony = IColonyManager.getInstance().getColonyByDimension(closeColony, world.provider.dimensionId);
            if (colony != null)
            {
                colony.removeLoadedChunk(chunkPosAsLong(chunk.xPosition, chunk.zPosition));
            }
        }
    }

    /**
     * Add a chunk storage to a chunk.
     *
     * @param chunk   the chunk to add it to.
     * @param storage the said storage.
     */
    public static void addStorageToChunk(final Chunk chunk, final ChunkLoadStorage storage)
    {
        if (chunk.xPosition == 0 && chunk.zPosition == 0)
        {
            Log.getLogger().warn("Trying to claim zero chunk!", new Exception());
        }

        final IColonyTagCapability cap = ColonyChunkDataHandler.getColonyTagCapability(chunk);
        storage.applyToCap(cap, chunk);

        if (cap != null)
        {
            Network.getNetwork().sendToEveryone(new UpdateChunkCapabilityMessage(cap, chunk.xPosition, chunk.zPosition));
        }
    }

    /**
     * Notify all chunks in the range of the colony about the colony.
     *
     * @param world  the world.
     * @param add    if add or remove.
     * @param id     the colony id.
     * @param centerX center block X.
     * @param centerY center block Y.
     * @param centerZ center block Z.
     */
    public static void claimColonyChunks(final World world, final boolean add, final int id, final int centerX, final int centerY, final int centerZ)
    {
        final int range = getConfig().getServer().initialColonySize.get();
        staticClaimInRange(id, add, centerX, centerY, centerZ, add ? range : range * 2, world, false);
    }

    /**
     * Notify all chunks in the range of the colony about the colony.
     * --- Only for dynamic claiming ---
     *
     * @param colony  the colony to claim for
     * @param add     if add or remove.
     * @param centerX center X of the building.
     * @param centerY center Y of the building.
     * @param centerZ center Z of the building.
     * @param range   the range to claim.
     * @param corners also (un)claim all chunks intersecting this box (if not null)
     */
    public static void claimBuildingChunks(
      final IColony colony, final boolean add,
      final int centerX, final int centerY, final int centerZ,
      final int range,
      @Nullable final Tuple<int[], int[]> corners)
    {
        buildingClaimInRange(colony, add, range, centerX, centerY, centerZ, false);

        if (corners != null)
        {
            buildingClaimBox(colony, centerX, centerY, centerZ, add, corners);
        }
    }

    /**
     * Check if all chunks within a certain range can be claimed.
     *
     * @param w     the world.
     * @param posX  center block X.
     * @param posY  center block Y.
     * @param posZ  center block Z.
     * @param range the range to check.
     * @return true if possible.
     */
    public static boolean canClaimChunksInRange(final World w, final int posX, final int posY, final int posZ, final int range)
    {
        final IChunkmanagerCapability worldCapability = ChunkManagerWorldSavedData.getOrCreate(w).getCapability();
        if (worldCapability == null)
        {
            return true;
        }
        final Chunk centralChunk = (Chunk) w.getChunkFromBlockCoords(posX, posZ);
        final int chunkX = centralChunk.xPosition;
        final int chunkZ = centralChunk.zPosition;

        for (int i = chunkX - range; i <= chunkX + range; i++)
        {
            for (int j = chunkZ - range; j <= chunkZ + range; j++)
            {
                final Chunk chunk = (Chunk) w.getChunkFromChunkCoords(i, j);
                final IColonyTagCapability colonyCap = ColonyChunkDataHandler.getColonyTagCapability(chunk);
                if (colonyCap == null)
                {
                    return true;
                }
                final ChunkLoadStorage storage = worldCapability.getChunkStorage(chunk.xPosition, chunk.zPosition);
                if (storage != null)
                {
                    storage.applyToCap(colonyCap, chunk);
                }
                if (colonyCap.getOwningColony() != 0)
                {
                    return false;
                }
            }
        }
        return true;
    }

    private static void buildingClaimInRange(
      final IColony colony, final boolean add, final int range,
      final int centerX, final int centerY, final int centerZ,
      final boolean force)
    {
        final World world = colony.getWorld();
        final IChunkmanagerCapability chunkManager = ChunkManagerWorldSavedData.getOrCreate(world).getCapability();
        if (chunkManager == null)
        {
            Log.getLogger().error(UNABLE_TO_FIND_WORLD_CAP_TEXT, new Exception());
            return;
        }

        final int colonyCenterChunkX = colony.getCenter().getX() >> 4;
        final int colonyCenterChunkZ = colony.getCenter().getZ() >> 4;

        final int chunkX = centerX >> 4;
        final int chunkZ = centerZ >> 4;

        final int maxColonySize = getConfig().getServer().maxColonySize.get();

        for (int i = chunkX - range; i <= chunkX + range; i++)
        {
            for (int j = chunkZ - range; j <= chunkZ + range; j++)
            {
                final int posX = i * BLOCKS_PER_CHUNK;
                final int posZ = j * BLOCKS_PER_CHUNK;

                if (!force && maxColonySize != 0)
                {
                    final int dx = i - colonyCenterChunkX;
                    final int dz = j - colonyCenterChunkZ;
                    if (dx * dx + dz * dz > maxColonySize * maxColonySize)
                    {
                        Log.getLogger().debug(
                          "Tried to claim chunk at pos X:" + posX + " Z:" + posZ
                            + " too far away from the colony:" + colony.getID() + " center:" + colony.getCenter()
                            + " max is config workingRangeTownHall ^2");
                        continue;
                    }
                }

                tryClaimBuilding(world, posX, centerY, posZ, add, colony, centerX, centerY, centerZ, chunkManager);
            }
        }

        if (add && range > 0)
        {
            final IBuilding building = colony.getServerBuildingManager().getBuilding(centerX, centerY, centerZ);
            MessageUtils.format(COLONY_SIZE_CHANGE, range, building.getSchematicName()).sendTo(colony).forManagers();
        }
    }

    private static void buildingClaimBox(
      final IColony colony,
      final int anchorX, final int anchorY, final int anchorZ,
      final boolean add,
      final Tuple<int[], int[]> corners)
    {
        final World world = colony.getWorld();
        final IChunkmanagerCapability chunkManager = ChunkManagerWorldSavedData.getOrCreate(world).getCapability();
        if (chunkManager == null)
        {
            Log.getLogger().error(UNABLE_TO_FIND_WORLD_CAP_TEXT, new Exception());
            return;
        }

        final int maxColonySize = getConfig().getServer().maxColonySize.get();
        final int colonyCX = colony.getCenter().getX();
        final int colonyCZ = colony.getCenter().getZ();

        final int[] min = corners.getA();
        final int[] max = corners.getB();

        final int minChunkX = min[0] >> 4;
        final int minChunkZ = min[2] >> 4;
        final int maxChunkX = max[0] >> 4;
        final int maxChunkZ = max[2] >> 4;

        for (int cx = minChunkX; cx <= maxChunkX; cx++)
        {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++)
            {
                final int posX = cx * BLOCKS_PER_CHUNK;
                final int posZ = cz * BLOCKS_PER_CHUNK;
                if (maxColonySize != 0)
                {
                    final double distSq = (posX - colonyCX) * (double)(posX - colonyCX)
                                          + (posZ - colonyCZ) * (double)(posZ - colonyCZ);
                    if (distSq > Math.pow(maxColonySize * BLOCKS_PER_CHUNK, 2))
                    {
                        Log.getLogger().debug(
                          "Tried to claim chunk at pos X:" + posX + " Z:" + posZ
                            + " too far away from the colony:" + colony.getID() + " center:" + colony.getCenter()
                            + " max is config workingRangeTownHall ^2");
                        continue;
                    }
                }
                tryClaimBuilding(world, posX, 64, posZ, add, colony, anchorX, anchorY, anchorZ, chunkManager);
            }
        }
    }

    /**
     * Claim a number of chunks in a certain range around a position.
     *
     * @param colonyId the colony id.
     * @param add      if claim or unclaim.
     * @param centerX  center block X.
     * @param centerY  center block Y.
     * @param centerZ  center block Z.
     * @param range    the range.
     * @param world    the world.
     * @param forceOwnerChange whether to force ownership change.
     */
    public static void staticClaimInRange(
      final int colonyId, final boolean add,
      final int centerX, final int centerY, final int centerZ,
      final int range, final World world, final boolean forceOwnerChange)
    {
        final IChunkmanagerCapability chunkManager = ChunkManagerWorldSavedData.getOrCreate(world).getCapability();
        if (chunkManager == null)
        {
            Log.getLogger().error(UNABLE_TO_FIND_WORLD_CAP_TEXT, new Exception());
            return;
        }

        final Chunk centralChunk = (Chunk) world.getChunkFromBlockCoords(centerX, centerZ);
        final int chunkXMax = centralChunk.xPosition;
        final int chunkZMax = centralChunk.zPosition;

        for (int chunkPosX = chunkXMax - range; chunkPosX <= chunkXMax + range; chunkPosX++)
        {
            for (int chunkPosZ = chunkZMax - range; chunkPosZ <= chunkZMax + range; chunkPosZ++)
            {
                tryClaim(world, chunkPosX * BLOCKS_PER_CHUNK, 0, chunkPosZ * BLOCKS_PER_CHUNK, add, colonyId, chunkManager, forceOwnerChange);
            }
        }
    }

    /**
     * Add the data to the chunk directly.
     *
     * @param world         the world.
     * @param chunkBlockX   block X inside the chunk.
     * @param chunkBlockY   block Y (unused for chunk lookup).
     * @param chunkBlockZ   block Z inside the chunk.
     * @param add           if add or delete.
     * @param id            the colony id.
     * @param chunkManager  the chunk manager capability.
     * @param forceOwnerChange whether to force ownership change.
     * @return true if the chunk was loaded and processed immediately.
     */
    public static boolean tryClaim(
      final World world,
      final int chunkBlockX, final int chunkBlockY, final int chunkBlockZ,
      final boolean add, final int id,
      final IChunkmanagerCapability chunkManager,
      final boolean forceOwnerChange)
    {
        if (!WorldUtil.isBlockLoaded(world, chunkBlockX, chunkBlockY, chunkBlockZ))
        {
            final ChunkLoadStorage newStorage = new ChunkLoadStorage(id, chunkPosAsLong(chunkBlockX >> 4, chunkBlockZ >> 4),
              add, String.valueOf(world.provider.dimensionId), forceOwnerChange);
            chunkManager.addChunkStorage(chunkBlockX >> 4, chunkBlockZ >> 4, newStorage);
            return false;
        }

        final Chunk chunk = (Chunk) world.getChunkFromBlockCoords(chunkBlockX, chunkBlockZ);
        final IColonyTagCapability cap = ColonyChunkDataHandler.getColonyTagCapability(chunk);
        if (cap == null)
        {
            return false;
        }

        final ChunkLoadStorage chunkLoadStorage = chunkManager.getChunkStorage(chunk.xPosition, chunk.zPosition);
        if (chunkLoadStorage != null)
        {
            chunkLoadStorage.applyToCap(cap, chunk);
        }

        if (add)
        {
            cap.addColony(id, chunk);
            if (forceOwnerChange)
            {
                cap.setOwningColony(id, chunk);
                final IColony colony = IColonyManager.getInstance().getColonyByDimension(id, world.provider.dimensionId);
                if (colony != null)
                {
                    colony.addLoadedChunk(chunkPosAsLong(chunk.xPosition, chunk.zPosition), chunk);
                }
            }
        }
        else
        {
            cap.removeColony(id, chunk);
        }

        Network.getNetwork().sendToTrackingChunk(new UpdateChunkCapabilityMessage(cap, chunk.xPosition, chunk.zPosition), chunk);
        return true;
    }

    /**
     * Add the data to the chunk directly for dynamic building claiming.
     * --- Only for dynamic claiming ---
     *
     * @param world          the world.
     * @param chunkBlockX    block X inside the chunk.
     * @param chunkBlockY    block Y.
     * @param chunkBlockZ    block Z inside the chunk.
     * @param add            if add or delete.
     * @param colony         the colony.
     * @param buildingX      building anchor X.
     * @param buildingY      building anchor Y.
     * @param buildingZ      building anchor Z.
     * @param chunkManager   the chunk manager capability.
     * @return true if the chunk was loaded and processed immediately.
     */
    public static boolean tryClaimBuilding(
      final World world,
      final int chunkBlockX, final int chunkBlockY, final int chunkBlockZ,
      final boolean add,
      final IColony colony,
      final int buildingX, final int buildingY, final int buildingZ,
      final IChunkmanagerCapability chunkManager)
    {
        if (!WorldUtil.isBlockLoaded(world, chunkBlockX, chunkBlockY, chunkBlockZ))
        {
            final ChunkLoadStorage newStorage = new ChunkLoadStorage(
              colony.getID(), chunkPosAsLong(chunkBlockX >> 4, chunkBlockZ >> 4),
              String.valueOf(world.provider.dimensionId), buildingX, buildingY, buildingZ, add);
            chunkManager.addChunkStorage(chunkBlockX >> 4, chunkBlockZ >> 4, newStorage);
            return false;
        }

        final Chunk chunk = (Chunk) world.getChunkFromBlockCoords(chunkBlockX, chunkBlockZ);
        final IColonyTagCapability cap = ColonyChunkDataHandler.getColonyTagCapability(chunk);
        if (cap == null)
        {
            return false;
        }

        if (chunk.xPosition == 0 && chunk.zPosition == 0)
        {
            if (colony == null || BlockPosUtil.getDistance2D(colony.getCenter().getX(), colony.getCenter().getZ(), 0, 0) > 200)
            {
                Log.getLogger().warn("Trying to claim at zero chunk pos!:", new Exception());
            }
        }

        final ChunkLoadStorage chunkLoadStorage = chunkManager.getChunkStorage(chunk.xPosition, chunk.zPosition);
        if (chunkLoadStorage != null)
        {
            chunkLoadStorage.applyToCap(cap, chunk);
        }

        if (add)
        {
            cap.addBuildingClaim(colony.getID(), buildingX, buildingY, buildingZ, chunk);
        }
        else
        {
            cap.removeBuildingClaim(colony.getID(), buildingX, buildingY, buildingZ, chunk);
        }

        Network.getNetwork().sendToTrackingChunk(new UpdateChunkCapabilityMessage(cap, chunk.xPosition, chunk.zPosition), chunk);
        return true;
    }
}

