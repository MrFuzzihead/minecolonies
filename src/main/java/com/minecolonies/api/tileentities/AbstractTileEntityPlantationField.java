package com.minecolonies.api.tileentities;

import com.ldtteam.structurize.blockentities.interfaces.IBlueprintDataProviderBE;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries.BuildingExtensionEntry;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * The abstract implementation for plantation field tile entities.
 */
public abstract class AbstractTileEntityPlantationField extends TileEntity implements IBlueprintDataProviderBE
{
    /**
     * Default constructor.
     */
    protected AbstractTileEntityPlantationField()
    {
        super();
    }

    /**
     * The field type of this plantation.
     *
     * @return the field type.
     */
    public abstract Set<BuildingExtensionEntry> getPlantationFieldTypes();

    /**
     * The working positions stored in this field (as [x, y, z] int arrays).
     *
     * @param NBTBase the NBTBase to search for.
     * @return a list of working positions.
     */
    public abstract List<int[]> getWorkingPositions(String NBTBase);

    /**
     * The colony this field is located in.
     *
     * @return the colony instance.
     */
    public abstract IColony getCurrentColony();

    /**
     * Get the dimension this plantation field is placed in.
     *
     * @return the dimension ID.
     */
    @Nullable
    public abstract Integer getDimension();

    /**
     * Get the rotation of the controller (0-3).
     *
     * @return the placed rotation.
     */
    public abstract int getRotation();

    /**
     * Get the mirroring setting of the controller.
     *
     * @return true if mirrored.
     */
    public abstract boolean getMirror();
}

