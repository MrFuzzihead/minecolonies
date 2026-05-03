package com.minecolonies.api.colony.managers.interfaces;

import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import net.minecraft.entity.passive.EntityAnimal;
import com.minecolonies.api.colony.IAnimalData;
import com.minecolonies.api.colony.IColony;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayerMP;
// [1.7.10] world.entity removed

/**
 * The interface for managed animals, such as Cavalry horses.
 * Note that this does not include farm animals.
 */
public interface IAnimalManager
{
    /**
     * Get the current number of managed animals.
     */
    public int getCurrentAnimalCount();

    /**
     * Register a civilian entity with the colony
     *
     * @param entity civilian to register
     */
    void registerAnimal(IManagedAnimal <? extends EntityAnimal> entity);

    /**
     * Get the EntityAnimal data by ID.
     *
     * @param id The EntityAnimal ID.
     * @return The EntityAnimal data, or null if not found.
     */
    public IAnimalData getAnimal(final int id);

    /**
     * Get all managed animals.
     */
    public List<IAnimalData> getAnimals();

    /**
     * The colony this manager belongs to.
     *
     * @return The colony.
     */
    public IAnimalData createAndRegisterAnimalData(IManagedAnimal<? extends EntityAnimal> entity);

    /**
     * Read the EntityAnimal information from nbt.
     *
     * @param compound the compound to read it from.
     */
    void read(@NotNull NBTTagCompound compound);

    /**
     * Write the EntityAnimal information to nbt.
     *
     * @param compoundNBT the compound to write it to.
     */
    void write(@NotNull NBTTagCompound compoundNBT);

    /**
     * Actions to execute on a colony tick.
     *
     * @param colony the event.
     */
    void onColonyTick(IColony colony);

    /**
     * Actions to execute on a tick.
     *
     * @param tickRate the event.
     */
    public boolean tickAnimalData(final int tickRate);
    
    /**
     * Mark this manager dirty, and in need of syncing / saving.
     */
    public void markDirty();

    /**
     * Clear the dirty flag.
     */
    public void clearDirty();

    /**
     * Send the necessary packets to subscribers.
     *
     * @param closeSubscribers players that were subscribed but are now out of range
     * @param newSubscribers   players that have just come into range and need data
     */
    public void sendPackets(@NotNull final Set<EntityPlayerMP> closeSubscribers, @NotNull final Set<EntityPlayerMP> newSubscribers);
}




