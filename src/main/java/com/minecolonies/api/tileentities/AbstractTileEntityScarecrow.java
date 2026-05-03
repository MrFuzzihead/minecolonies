package com.minecolonies.api.tileentities;

import com.minecolonies.api.colony.IColony;
import net.minecraft.tileentity.TileEntity;

/**
 * The abstract implementation for farmer field tile entities.
 */
public abstract class AbstractTileEntityScarecrow extends TileEntity
{
    /**
     * Default constructor.
     */
    protected AbstractTileEntityScarecrow()
    {
        super();
    }

    /**
     * Returns the type of the scarecrow (Important for the rendering).
     *
     * @return the enum type.
     */
    public abstract ScareCrowType getScarecrowType();

    /**
     * The colony this field is located in.
     *
     * @return the colony instance.
     */
    public abstract IColony getCurrentColony();
}
