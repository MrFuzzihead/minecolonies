package com.minecolonies.core.network.messages.server.colony.building.fields;
import net.minecraft.util.Direction;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.colony.buildingextensions.FarmField;
import com.minecolonies.core.tileentities.TileEntityScarecrow;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Direction -> net.minecraft.util.EnumFacing
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] block.entity removed

import java.util.Arrays;

import static com.minecolonies.core.colony.buildingextensions.FarmField.MAX_RANGE;

/**
 * Message to change the farmer field plot size.
 */
public class FarmFieldPlotResizeMessage implements IMessage
{
    /**
     * The new radius of the field plot.
     */
    private int size;

    /**
     * The specified direction for the new radius.
     */
    private Direction direction;

    /**
     * The field position.
     */
    private int[] position;

    /**
     * Forge default constructor
     */
    public FarmFieldPlotResizeMessage()
    {
        super();
    }

    /**
     * @param size      the new radius of the field plot
     * @param direction the specified direction for the new radius
     * @param position  the field position.
     */
    public FarmFieldPlotResizeMessage(int size, Direction direction, int[] position)
    {
        super();
        this.size = size;
        this.direction = direction;
        this.position = position;
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        final BlockEntity fieldBlock = ctx.getServerHandler().playerEntity.World.getBlockEntity(position);
        if (fieldBlock instanceof TileEntityScarecrow scarecrow)
        {
            final int currentSum = Arrays.stream(scarecrow.getFieldSize()).sum();
            final int currentDirSize = scarecrow.getFieldSize()[direction.get2DDataValue()];

            if (size < 0 || (size > currentDirSize && currentSum - currentDirSize + size > MAX_RANGE))
            {
                return;
            }

            scarecrow.setFieldSize(direction, size);
            final IColony colony = scarecrow.getCurrentColony();
            if (colony != null)
            {
                colony.getServerBuildingManager()
                    .getMatchingBuildingExtension(f -> f.getBuildingExtensionType().equals(BuildingExtensionRegistries.farmField.get()) && f.getPosition().equals(position))
                    .map(m -> (FarmField) m)
                    .ifPresent(field -> field.setRadius(direction, size));
            }
        }
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeInt(size);
        buf.writeInt(direction.get2DDataValue());
        buf.writeBlockPos(position);
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        size = buf.readInt();
        direction = Direction.from2DDataValue(buf.readInt());
        position = buf.readBlockPos();
    }
}



