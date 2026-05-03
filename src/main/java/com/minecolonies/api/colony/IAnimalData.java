package com.minecolonies.api.colony;

import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.managers.interfaces.IManagedAnimal;

// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.network.PacketBuffer;
// [1.7.10] world.entity removed
// [1.7.10] INBTSerializable -> manual read/write

/**
 * Data interface for animals managed by the EntityAnimal Manager.
 */
public interface IAnimalData
{
    /**
     * Get the EntityAnimal data ID.
     *
     * @return the EntityAnimal data ID
    */
    public int getId();

    /**
     * Get the globally unique identifier associated with this EntityAnimal data.
     *
     * @return the globally unique identifier associated with this EntityAnimal data.
     */
    public UUID getUUID();

    /**
     * Initializes the entities values from EntityAnimal data.
     */
    public void initEntityValues();

    /**
     * Get the EntityAnimal entity.
     *
     * @return the EntityAnimal entity.
     */
    public Optional<IManagedAnimal <? extends EntityAnimal>> getManagedAnimal();

    /**
     * Set the EntityAnimal entity.
     *
     * @param entity the EntityAnimal entity.
     */
    public void setManagedAnimal(final IManagedAnimal<? extends EntityAnimal> entity);

    /**
     * Clear the dirty flag for this EntityAnimal data.
     */
    public void clearDirty();

    /**
     * Check if this EntityAnimal data is dirty and needs syncing.
     *
     * @return true if dirty, false otherwise
     */
    public boolean isDirty();

    /**
     * Mark this EntityAnimal data as dirty and in need of syncing / saving.
     */
    public void markDirty();

    /**
     * Update the EntityAnimal data.
     *
     * @param tickRate the tick rate
     */
    public void update(final int tickRate);

    /**
     * Writes the EntityAnimal data to a byte buf for transition.
     *
     * @param buf Buffer to write to.
     */
    void serializeViewNetworkData(@NotNull PacketBuffer buf);

    /**
     * Gets the home building of the EntityAnimal.
     * 
     * @return the home building, or null if the EntityAnimal does not have a home building.
     */
    public IBuilding getHomeBuilding();

    /**
     * Sets the home building of the EntityAnimal.
     * 
     * @param building the new home building of the EntityAnimal.
     */
    public void setHomeBuilding(@NotNull IBuilding building);

    /**
     * Called when a building is removed.
     * 
     * @param building the building that was removed.
     */
    public void onRemoveBuilding(final IBuilding building);

    /**
     * Sets the last position of the EntityAnimal.
     * 
     * @param lastPosition the last position of the EntityAnimal.
     */
    public void setLastPosition(final int[] lastPosition);

    /**
     * Gets the last position of the EntityAnimal.
     * 
     * @return the last position of the EntityAnimal.
     */
    public int[] getLastPosition();

    /**
     * Returns the current combat cooldown of the horse. 
     * A higher value means the horse is currently less ready for combat.
     * 
     * @return the current combat cooldown of the horse
     */
    public float getCombatCooldown();

    /**
     * Sets the combat cooldown of the horse.
     * 
     * @param newCooldown the new combat cooldown of the horse
     */
    public void setCombatCooldown(float newCooldown);
}








