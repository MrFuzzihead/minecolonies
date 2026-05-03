package com.minecolonies.api.colony.managers.interfaces;

import org.jetbrains.annotations.NotNull;
import net.minecraft.entity.passive.EntityAnimal;

import com.minecolonies.api.colony.IAnimalData;
import com.minecolonies.api.util.constant.CitizenConstants;

// [1.7.10] broken import removed
// [1.7.10] world.entity removed

public interface IManagedAnimal<T extends EntityAnimal>
{   
    /**
     * @return the backing entity
     */
    T getEntity();

    /**
     * Get the accessor for the colony ID.
     */
    public @NotNull Object /* <> removed */ getColonyIdAccessor();

    /**
     * Get the accessor for the citizen ID.
     */
    public @NotNull Object /* <> removed */ getAnimalIdAccessor();

    /**
     * Get the unique ID of this managed EntityAnimal.
     */
    int getManagedAnimalId();

    /**
     * Set the unique ID of this managed EntityAnimal.
     */
    void setManagedAnimalId(final int id);

    /**
     * Get the colony associated with this managed EntityAnimal.
     */
    int getColonyId();

    /**
     * Set the colony associated with this managed EntityAnimal.
     */
    void setColonyId(final int id);

    /**
     * Get the EntityAnimal data associated with this managed EntityAnimal.
     */
    IAnimalData getAnimalData();

    /**
     * Get the EntityAnimal data view associated with this managed EntityAnimal.
     */
    IAnimalDataView getAnimalDataView();

    /**
     * Set the EntityAnimal data associated with this managed EntityAnimal.
     */
    void setAnimalData(final IAnimalData data);

    /**
     * Get the offset ticks for this managed EntityAnimal.
     */
    default int getOffsetTicks()
    {
        return this.getEntity().tickCount + CitizenConstants.OFFSET_TICK_MULTIPLIER * this.getManagedAnimalId();
    }
}




