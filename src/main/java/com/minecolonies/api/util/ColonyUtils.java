package com.minecolonies.api.util;

// [1.7.10 BACKPORT] Ported capability accesses to ColonyChunkDataHandler.getColonyTagCapability(chunk).
import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.storage.ClientFutureProcessor;
import com.ldtteam.structurize.storage.ServerFutureProcessor;
import com.ldtteam.structurize.storage.StructurePacks;
import com.ldtteam.structurize.util.IOPool;
import com.ldtteam.structurize.util.RotationMirror;
import com.minecolonies.api.colony.IColonyTagCapability;
import com.minecolonies.core.colony.ColonyChunkDataHandler;
import com.minecolonies.api.util.Tuple;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.minecolonies.api.util.constant.ColonyManagerConstants.NO_COLONY_ID;

/**
 * Contains colony specific utility.
 */
public final class ColonyUtils
{
    private ColonyUtils()
    {
        /*
         * Intentionally left empty.
         */
    }

    /**
     * Queues a blueprint load to the right side.
     */
    public static CompletableFuture<Blueprint> queueBlueprintLoad(final World world, final String structurePack, final String structurePath, final Consumer<Blueprint> afterLoad)
    {
        return queueBlueprintLoad(world, structurePack, structurePath, afterLoad, e -> Log.getLogger().warn(e));
    }

    /**
     * Queues a blueprint load to the right side.
     */
    public static CompletableFuture<Blueprint> queueBlueprintLoad(
        final World world,
        final String structurePack,
        final String structurePath,
        final Consumer<Blueprint> afterLoad,
        final Consumer<String> errorHandler)
    {
        // [1.7.10 BACKPORT] FMLEnvironment.production â†’ true (always production in 1.7.10)
        final CompletableFuture<Blueprint> future =
            CompletableFuture.supplyAsync(() -> StructurePacks.getBlueprint(structurePack, structurePath, true), IOPool.getExecutor());
        if (world.isRemote)
        {
            ClientFutureProcessor.queueBlueprint(new ClientFutureProcessor.BlueprintProcessingData(future,
                (blueprint ->
                {
                    if (blueprint == null)
                    {
                        errorHandler.accept("Couldn't find structure with name: " + structurePack + " in: " + structurePath + ". Aborting loading procedure");
                    }
                    else
                    {
                        afterLoad.accept(blueprint);
                    }
                })));
            return future;
        }
        else
        {
            ServerFutureProcessor.queueBlueprint(new ServerFutureProcessor.BlueprintProcessingData(future, world,
                (blueprint ->
                {
                    if (blueprint == null)
                    {
                        errorHandler.accept("Couldn't find structure with name: " + structurePack + " in: " + structurePath + ". Aborting loading procedure");
                    }
                    else
                    {
                        afterLoad.accept(blueprint);
                    }
                })));
            return future;
        }
    }

    /**
     * Calculated the corner of a building. Also rotates the blueprint accordingly.
     *
     * @param posX       the central position X.
     * @param posY       the central position Y.
     * @param posZ       the central position Z.
     * @param world      the world.
     * @param blueprint  the structureWrapper.
     * @param rotation   the rotation.
     * @param isMirrored if its mirrored.
     * @return a tuple with the required corners as int[3] arrays {x,y,z}.
     */
    public static Tuple<int[], int[]> calculateCorners(
      final int posX, final int posY, final int posZ,
      final World world,
      final Blueprint blueprint,
      final int rotation,
      final boolean isMirrored)
    {
        if (blueprint == null)
        {
            return new Tuple<>(new int[]{posX, posY, posZ}, new int[]{posX, posY, posZ});
        }

        // [1.7.10 BACKPORT] Mirror enum differs between versions â€” use RotationMirror directly
        blueprint.setRotationMirror(RotationMirror.of(BlockPosUtil.getRotationFromRotations(rotation), isMirrored ? com.ldtteam.structurize.util.Mirror.FRONT_BACK : com.ldtteam.structurize.util.Mirror.NONE), world);
        // [1.7.10 BACKPORT] blueprint.getPrimaryBlockOffset() returns int[] {x,y,z} in 1.7.10
        final int[] offset = blueprint.getPrimaryBlockOffset();
        final int zx = posX - offset[0];
        final int zy = posY - offset[1];
        final int zz = posZ - offset[2];

        return new Tuple<>(
          new int[]{zx, zy, zz},
          new int[]{zx + blueprint.getSizeX() - 1, zy + blueprint.getSizeY() - 1, zz + blueprint.getSizeZ() - 1}
        );
    }

    /**
     * Reports the block corners from a bounding box.
     *
     * @param minX min X
     * @param minY min Y
     * @param minZ min Z
     * @param maxX max X
     * @param maxY max Y
     * @param maxZ max Z
     * @return    the corners as int[3] arrays.
     */
    public static Tuple<int[], int[]> calculateCorners(
      final double minX, final double minY, final double minZ,
      final double maxX, final double maxY, final double maxZ)
    {
        return new Tuple<>(
          new int[]{(int) Math.floor(minX), (int) Math.floor(minY), (int) Math.floor(minZ)},
          new int[]{(int) Math.ceil(maxX), (int) Math.ceil(maxY), (int) Math.ceil(maxZ)}
        );
    }

    /**
     * Get the owning colony from a chunk.
     *
     * @param chunk the chunk to check.
     * @return the colony id.
     */
    public static int getOwningColony(final Chunk chunk)
    {
        final IColonyTagCapability cap = ColonyChunkDataHandler.getColonyTagCapability(chunk);
        return cap == null ? NO_COLONY_ID : cap.getOwningColony();
    }

    /**
     * Get all claiming buildings from the chunk.
     *
     * @param chunk the chunk they are at.
     * @return the map from colony to building claims â€” keys are colony IDs, values are sets of [x,y,z] int arrays.
     */
    public static Map<Integer, Set<long[]>> getAllClaimingBuildings(final Chunk chunk)
    {
        final IColonyTagCapability cap = ColonyChunkDataHandler.getColonyTagCapability(chunk);
        return cap == null ? new HashMap<>() : cap.getAllClaimingBuildings();
    }

    /**
     * Get all static claims from a chunk.
     *
     * @param chunk the chunk to get it from.
     * @return the list.
     */
    public static List<Integer> getStaticClaims(final Chunk chunk)
    {
        final IColonyTagCapability cap = ColonyChunkDataHandler.getColonyTagCapability(chunk);
        return cap == null ? new ArrayList<>() : cap.getStaticClaimColonies();
    }

    /**
     * Get comprehensive chunk ownership data.
     *
     * @param chunk the chunk to get it from.
     * @return the ownership data.
     */
    @Nullable
    public static ChunkCapData getChunkCapData(final Chunk chunk)
    {
        final IColonyTagCapability cap = ColonyChunkDataHandler.getColonyTagCapability(chunk);
        return cap == null
          ? new ChunkCapData(chunk.xPosition, chunk.zPosition)
          : new ChunkCapData(chunk.xPosition, chunk.zPosition, cap.getOwningColony(), cap.getStaticClaimColonies(), cap.getAllClaimingBuildings());
    }
}
