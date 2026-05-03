package com.minecolonies.api.colony.managers.interfaces;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Interface for grave managers.
 */
public interface IGraveManager
{
    /**
     * Read the graves from NBT.
     *
     * @param compound the compound.
     */
    void read(@NotNull final NBTTagCompound compound);

    /**
     * Write the graves to NBT.
     *
     * @param compound the compound.
     */
    void write(@NotNull final NBTTagCompound compound);

    /**
     * Tick the graves on colony tick.
     *
     * @param colony the event.
     */
    void onColonyTick(IColony colony);

    /**
     * Reserve a grave
     *
     * @param pos the id of the grave.
     * @return is the grave successfully reserved.
     */
    boolean reserveGrave(int[] pos);

    /**
     * Un-Reserve a grave
     *
     * @param pos the id of the grave.
     */
    void unReserveGrave(int[] pos);

    /**
     * Reserve the next free grave
     *
     * @return the grave successfully reserved or null if none available
     */
    int[] reserveNextFreeGrave();

    /**
     * Attempt to create a TileEntityGrave at @pos containing the specific @citizenData
     *
     * On failure: drop all the citizen inventory on the ground.
     *
     * @param world        The world.
     * @param pos          The position where to spawn a grave
     * @param citizenData  The citizenData
     * @return position if a grave was created.
     */
    int[] createCitizenGrave(final World world, final int[] pos, final ICitizenData citizenData);

    /**
     * Returns a map with all graves within the colony. Key is ID (Coordinates), value is isReserved boolean.
     *
     * @return Map with ID (coordinates) as key, value is isReserved boolean.
     */
    @NotNull
    Map<int[], Boolean> getGraves();

    /**
     * Add a grave from the Colony.
     *
     * @param pos    position of the TileEntityGrave to add.
     * @return the grave that was created and added.
     */
    boolean addNewGrave(@NotNull final int[] pos);

    /**
     * Remove a TileEntityGrave from the Colony (when it is destroyed).
     *
     * @param pos    position of the TileEntityGrave to remove.
     */
    void removeGrave(@NotNull final int[] pos);
}



