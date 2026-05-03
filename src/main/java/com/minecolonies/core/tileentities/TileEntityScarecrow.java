package com.minecolonies.core.tileentities;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.tileentities.AbstractTileEntityScarecrow;
import com.minecolonies.api.tileentities.ScareCrowType;
import com.minecolonies.core.Network;
import com.minecolonies.core.network.messages.server.colony.building.fields.FarmFieldRegistrationMessage;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static com.minecolonies.core.colony.buildingextensions.FarmField.*;

/**
 * The scarecrow tile entity to store extra data.
 */
public class TileEntityScarecrow extends AbstractTileEntityScarecrow
{
    /**
     * Random generator.
     */
    private final Random random = new Random();

    /**
     * The colony this field is located in.
     */
    private IColony currentColony;

    /**
     * The type of the scarecrow.
     */
    private ScareCrowType type;

    /**
     * The size of the field in all four directions (S, W, N, E).
     */
    private int[] fieldSize = {DEFAULT_RANGE, DEFAULT_RANGE, DEFAULT_RANGE, DEFAULT_RANGE};

    /**
     * Creates an instance of the tileEntity.
     */
    public TileEntityScarecrow()
    {
        super();
    }

    @Override
    public ScareCrowType getScarecrowType()
    {
        if (this.type == null)
        {
            final ScareCrowType[] values = ScareCrowType.values();
            this.type = values[this.random.nextInt(values.length)];
        }
        return this.type;
    }

    @Override
    public IColony getCurrentColony()
    {
        if (currentColony == null && worldObj != null)
        {
            this.currentColony = IColonyManager.getInstance().getIColony(worldObj, xCoord, yCoord, zCoord);
            if (currentColony instanceof IColonyView)
            {
                Network.getNetwork().sendToServer(new FarmFieldRegistrationMessage(currentColony, xCoord, yCoord, zCoord));
            }
        }
        return currentColony;
    }

    @Override
    public void writeToNBT(final NBTTagCompound NBTTagCompound)
    {
        super.writeToNBT(NBTTagCompound);
        NBTTagCompound.setIntArray(TAG_RADIUS, fieldSize);
    }

    @Override
    public void readFromNBT(final NBTTagCompound NBTTagCompound)
    {
        super.readFromNBT(NBTTagCompound);
        if (NBTTagCompound.hasKey(TAG_RADIUS))
        {
            fieldSize = NBTTagCompound.getIntArray(TAG_RADIUS);
        }
    }

    /**
     * Sets the field radius in a direction.
     *
     * @param directionIndex the direction index (0=S, 1=W, 2=N, 3=E).
     * @param radius         the number of blocks from the scarecrow.
     */
    public void setFieldSize(final int directionIndex, final int radius)
    {
        this.fieldSize[directionIndex] = Math.min(radius, MAX_RANGE);
        markDirty();
    }

    /**
     * Field size getter.
     *
     * @return the field size array.
     */
    public int[] getFieldSize()
    {
        return fieldSize;
    }
}

