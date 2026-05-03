package com.minecolonies.core.network.messages.client.colony;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.colony.Colony;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int[] -> int x,y,z
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Add or Update a ColonyView on the client.
 */
public class ColonyViewRemoveBuildingMessage implements IMessage
{
    private int      colonyId;
    private int[] buildingId;

    /**
     * Empty constructor used when registering the
     */
    public ColonyViewRemoveBuildingMessage()
    {
        super();
    }

    /**
     * Creates an object for the building remove
     *
     * @param colony   Colony the building is in.
     * @param building AbstractBuilding that is removed.
     */
    public ColonyViewRemoveBuildingMessage(@NotNull final Colony colony, final int[] building)
    {
        this.colonyId = colony.getID();
        this.buildingId = building;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        colonyId = buf.readInt();
        buildingId = buf.readBlockPos();
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(colonyId);
        buf.writeBlockPos(buildingId);
    }

    @Nullable
    @Override
    public Boolean getExecutionSide()
    {
        return Boolean.FALSE;
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        if (Minecraft.getInstance().World != null)
        {
            IColonyManager.getInstance().handleColonyViewRemoveBuildingMessage(colonyId, buildingId, Minecraft.getInstance().World.dimension());
        }
    }
}



