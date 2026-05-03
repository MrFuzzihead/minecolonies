package com.minecolonies.api.colony;

import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.NBTUtils;
// [1.7.10 BACKPORT] Package mappings:
//   net.minecraft.core.int[]         → store as 3 ints (no int[] class in 1.7.10)
//   net.minecraft.core.Direction        → removed (no equivalent needed in Storage interface)
//   net.minecraft.nbt.NBTTagCompound       → net.minecraft.nbt.NBTTagCompound
//   net.minecraft.nbt.NBTBase               → net.minecraft.nbt.NBTBase
//   net.minecraft.world.World.ChunkCoordIntPair  → net.minecraft.world.ChunkCoordIntPair
//   net.minecraft.world.World.chunk.Chunk → net.minecraft.world.chunk.Chunk
//   net.minecraftforge.common.capabilities.Capability → removed (no capabilities in 1.7.10)
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.ChunkCoordIntPair; // [1.7.10] ChunkCoordIntPair ? ChunkCoordIntPair
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

import static com.minecolonies.api.util.constant.ColonyManagerConstants.NO_COLONY_ID;
import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Capability for the colony NBTBase for chunks
 */
public interface IColonyTagCapability
{
    /**
     * Remove a colony from the list. Only relevant in non dynamic claiming.
     *
     * @param chunk the chunk to remove it from.
     * @param id    the id to remove.
     */
    void removeColony(final int id, final Chunk chunk);

    /**
     * Add a new colony to the chunk. Only relevant in non dynamic claiming.
     *
     * @param chunk the chunk to add it to.
     * @param id    the id to add.
     */
    void addColony(final int id, final Chunk chunk);

    /**
     * Get a list of colonies with a static claim.
     *
     * @return a list of their ids.
     */
    @NotNull
    List<Integer> getStaticClaimColonies();

    /**
     * Set the owning colony.
     *
     * @param chunk the chunk to set it for.
     * @param id    the id to set.
     */
    void setOwningColony(final int id, final Chunk chunk);

    /**
     * Get the owning colony.
     *
     * @return the id of it.
     */
    int getOwningColony();

    /**
     * Reset the capability.
     *
     * @param chunk the chunk to reset.
     */
    void reset(final Chunk chunk);

    /**
     * Add the building claim of a certain building.
     *
     * @param colonyId the colony id.
     * @param pos      the position of the building.
     * @param chunk    the chunk to add the claim for.
     */
    void addBuildingClaim(final int colonyId, final int[] pos, final Chunk chunk);

    /**
     * Remove the building claim of a certain building.
     *
     * @param colonyId the colony id.
     * @param pos      the position of the building.
     * @param chunk    the chunk to remove it from.
     */
    void removeBuildingClaim(final int colonyId, final int[] pos, final Chunk chunk);

    /**
     * Sets all close colonies.
     *
     * @param colonies the set of colonies.
     */
    void setStaticColonyClaim(final List<Integer> colonies);

    /**
     * Get the claiming buildings map.
     *
     * @return the entire map.
     */
    @NotNull
    Map<Integer, Set<int[]>> getAllClaimingBuildings();

    void readFromNBT(NBTTagCompound compound);

    /**
     * The implementation of the colonyTagCapability.
     */
    class Impl implements IColonyTagCapability
    {
        /**
         * The set of all close colonies. Only relevant in non dynamic claiming.
         */
        private Set<Integer> colonies = new HashSet<>();

        /**
         * The colony owning the chunk. NO_COLONY_ID If none.
         */
        private int owningColony = NO_COLONY_ID;

        /**
         * List of buildings claiming this chunk for a certain colony.
         */
        private final Map<Integer, Set<int[]>> claimingBuildings = new HashMap<>();

        @Override
        public void addColony(final int id, final Chunk chunk)
        {
            final IColony colony = IColonyManager.getInstance().getColonyByDimension(id, chunk.getLevel().dimension());
            if (colony == null)
            {
                return;
            }

            colonies.add(id);
            if (owningColony == NO_COLONY_ID || IColonyManager.getInstance().getColonyByDimension(owningColony, chunk.getLevel().dimension()) == null)
            {
                colony.addLoadedChunk(ChunkCoordIntPair.asLong(chunk.getPos().x, chunk.getPos().z), chunk);
                owningColony = id;
            }
            chunk.setUnsaved(true);
        }

        @Override
        public void removeColony(final int id, final Chunk chunk)
        {
            colonies.remove(id);
            claimingBuildings.remove(id);
            if (owningColony == id)
            {
                if (!claimingBuildings.isEmpty())
                {
                    owningColony = claimingBuildings.keySet().iterator().next();
                }
                else if (!colonies.isEmpty())
                {
                    owningColony = colonies.iterator().next();
                }
                else
                {
                    owningColony = NO_COLONY_ID;
                }
            }

            chunk.setUnsaved(true);
        }

        @Override
        public void setStaticColonyClaim(final List<Integer> colonies)
        {
            this.colonies = new HashSet<>(colonies);
        }

        @Override
        public void reset(final Chunk chunk)
        {
            colonies.clear();
            owningColony = NO_COLONY_ID;
            claimingBuildings.clear();
            chunk.setUnsaved(true);
        }

        @Override
        public void addBuildingClaim(final int colonyId, final int[] pos, final Chunk chunk)
        {
            if (chunk.getPos().equals(ChunkCoordIntPair.ZERO))
            {
                final IColony colony = IColonyManager.getInstance().getColonyByDimension(colonyId, chunk.getLevel().dimension());
                if (colony == null || BlockPosUtil.getDistance2D(colony.getCenter(), new int[]{0,0,0}) > 200)
                {
                    Log.getLogger().warn("Claiming id:" + colonyId + " building at zero pos!" + pos, new Exception());
                }
            }

            if (owningColony == NO_COLONY_ID)
            {
                setOwningColony(colonyId, chunk);
                final IColony colony = IColonyManager.getInstance().getColonyByDimension(colonyId, chunk.getLevel().dimension());
                if (colony != null)
                {
                    colony.addLoadedChunk(ChunkCoordIntPair.asLong(chunk.getPos().x, chunk.getPos().z), chunk);
                }
            }

            if (claimingBuildings.containsKey(colonyId))
            {
                claimingBuildings.get(colonyId).add(pos);
            }
            else
            {
                final Set<int[]> newList = new HashSet<>();
                newList.add(pos);
                claimingBuildings.put(colonyId, newList);
            }
            chunk.setUnsaved(true);
        }

        @Override
        public void removeBuildingClaim(final int colonyId, final int[] pos, final Chunk chunk)
        {
            if (!claimingBuildings.containsKey(colonyId))
            {
                return;
            }

            chunk.setUnsaved(true);
            final Set<int[]> buildings = claimingBuildings.get(colonyId);
            buildings.remove(pos);

            if (buildings.isEmpty())
            {
                claimingBuildings.remove(colonyId);

                if (owningColony == colonyId && !colonies.contains(owningColony))
                {
                    if (claimingBuildings.isEmpty())
                    {
                        if (colonies.isEmpty())
                        {
                            owningColony = NO_COLONY_ID;
                        }
                        else
                        {
                            owningColony = colonies.iterator().next();
                        }
                    }
                    else
                    {
                        for (final Iterator<Map.Entry<Integer, Set<int[]>>> colonyIt = claimingBuildings.entrySet().iterator(); colonyIt.hasNext(); )
                        {
                            final Map.Entry<Integer, Set<int[]>> colonyEntry = colonyIt.next();
                            final IColony colony = IColonyManager.getInstance().getColonyByDimension(colonyEntry.getKey(), chunk.getLevel().dimension());
                            if (colony == null)
                            {
                                continue;
                            }

                            for (final Iterator<int[]> buildingIt = colonyEntry.getValue().iterator(); buildingIt.hasNext(); )
                            {
                                final int[] buildingPos = buildingIt.next();
                                if (colony.getServerBuildingManager().getBuilding(buildingPos) != null)
                                {
                                    colony.addLoadedChunk(ChunkCoordIntPair.asLong(chunk.getPos().x, chunk.getPos().z), chunk);
                                    setOwningColony(colonyEntry.getKey(), chunk);
                                    return;
                                }
                                else
                                {
                                    buildingIt.remove();
                                }
                            }

                            if (colonyEntry.getValue().isEmpty())
                            {
                                colonyIt.remove();
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void setOwningColony(final int id, final Chunk chunk)
        {
            this.owningColony = id;
            chunk.setUnsaved(true);
        }

        @Override
        public int getOwningColony()
        {
            return owningColony;
        }

        @NotNull
        @Override
        public List<Integer> getStaticClaimColonies()
        {
            return new ArrayList<>(colonies);
        }

        @NotNull
        @Override
        public Map<Integer, Set<int[]>> getAllClaimingBuildings()
        {
            return claimingBuildings;
        }

        @Override
        public void readFromNBT(final NBTTagCompound compound)
        {
            // Set owning
            owningColony = compound.getInt(TAG_ID);

            // Fill colonies list
            NBTUtils.streamCompound(compound.getList(TAG_COLONIES, NBTBase.TAG_COMPOUND))
              .map(c -> c.getInt(TAG_ID)).forEach(colonies::add);

            // Fill claim buildings list
            NBTUtils.streamCompound(compound.getList(TAG_BUILDINGS_CLAIM, NBTBase.TAG_COMPOUND)).forEach(this::readClaims);
            if (owningColony == NO_COLONY_ID && !getStaticClaimColonies().isEmpty())
            {
                owningColony = getStaticClaimColonies().get(0);
            }
        }

        /**
         * Read the position list and add it to the map.
         *
         * @param compound the compound to read it from.
         */
        private void readClaims(final NBTTagCompound compound)
        {
            final int id = compound.getInt(TAG_ID);
            NBTUtils.streamCompound(compound.getList(TAG_BUILDINGS, NBTBase.TAG_COMPOUND)).forEach(
              NBTBase -> {
                  final int[] pos = BlockPosUtil.read((NBTBase), TAG_BUILDING);
                  if (claimingBuildings.containsKey(id))
                  {
                      claimingBuildings.get(id).add(pos);
                  }
                  else
                  {
                      final Set<int[]> newList = new HashSet<>();
                      newList.add(pos);
                      claimingBuildings.put(id, newList);
                  }
              });
        }
    }

    /**
     * The storage class of the capability.
     *
     * <p><b>1.7.10 Backport:</b> The {@code Capability<IColonyTagCapability>} and
     * {@code Direction} parameters from the 1.21 signature have been removed — they do not
     * exist in 1.7.10. All call-sites pass {@code null} for the former capability param.</p>
     */
    class Storage
    {
        /** @param capability ignored in 1.7.10 (was Capability&lt;IColonyTagCapability&gt;) */
        public static NBTBase writeNBT(@SuppressWarnings("unused") final Object capability,
            @NotNull final IColonyTagCapability instance,
            @SuppressWarnings("unused") final Object side)
        {
            final NBTTagCompound compound = new NBTTagCompound();
            compound.putInt(TAG_ID, instance.getOwningColony());
            compound.put(TAG_COLONIES, instance.getStaticClaimColonies().stream().map(Storage::write).collect(NBTUtils.toListNBT()));
            compound.put(TAG_BUILDINGS_CLAIM, instance.getAllClaimingBuildings().entrySet().stream().map(Storage::writeClaims).collect(NBTUtils.toListNBT()));


            return compound;
        }

        /** @param capability ignored in 1.7.10 (was Capability&lt;IColonyTagCapability&gt;) */
        public static void readNBT(
            @SuppressWarnings("unused") final Object capability,
            @NotNull final IColonyTagCapability instance,
            @SuppressWarnings("unused") final Object side,
            @NotNull final NBTBase nbt)
        {
            if (nbt instanceof NBTTagCompound && ((NBTTagCompound) nbt).contains(TAG_ID))
            {
                instance.readFromNBT((NBTTagCompound) nbt);
            }
        }

        /**
         * Write one colony id to nbt.
         *
         * @param id the id.
         * @return the compound of it.
         */
        private static NBTTagCompound write(final int id)
        {
            final NBTTagCompound compound = new NBTTagCompound();
            compound.putInt(TAG_ID, id);
            return compound;
        }

        /**
         * Write the claims map entry to NBT.
         *
         * @param entry the entry.
         * @return the resulting compound.
         */
        private static NBTTagCompound writeClaims(@NotNull final Map.Entry<Integer, Set<int[]>> entry)
        {
            final NBTTagCompound compound = new NBTTagCompound();
            compound.putInt(TAG_ID, entry.getKey());
            compound.put(TAG_BUILDINGS, entry.getValue().stream().map(pos -> BlockPosUtil.write(new NBTTagCompound(), TAG_BUILDING, pos)).collect(NBTUtils.toListNBT()));
            return compound;
        }
    }
}





