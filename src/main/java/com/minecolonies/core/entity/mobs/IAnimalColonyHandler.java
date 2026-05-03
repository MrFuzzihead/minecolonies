package com.minecolonies.core.entity.mobs;

import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.colony.IColony;

// [1.7.10] broken import removed

public interface IAnimalColonyHandler 
{
    /**
     * Server-specific update for the EntityAnimal.
     * 
     * @param colonyID  the id of the colony.
     * @param citizenID the id of the citizen.
     */
    public void registerWithColony(final int colonyID, final int citizenID);

    /**
     * Getter for the colony.
     * 
     * @return the colony.
     */
    public @Nullable IColony getColony();

    /**
     * Getter for the colony id.
     *
     * @return the colony id.
     */
    public int getColonyId();

    /**
     * Setter for the colony id.
     *
     * @param colonyId the new colonyId.
     */
    public void setColonyId(final int colonyId);

    /**
     * Update the client side of the citizen entity.
     */
    public void updateColonyClient();

    /**
     * Called when the entity's data is updated from the server.
     * 
     * @param dataAccessor The data accessor which contains the updated data.
     */
    public void onSyncedDataUpdated(final Object /* EntityDataAccessor<?> */ data);

}



