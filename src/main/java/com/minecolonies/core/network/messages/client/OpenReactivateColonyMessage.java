package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.client.gui.townhall.WindowTownHallColonyReactivate;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Message to open the colony founding covenant.
 */
public class OpenReactivateColonyMessage implements IMessage
{
    /**
     * Colony pos at which we are trying to place.
     */
    private String closestName;
    private int      closestDistance;
    private int[] townHallPos;

    /**
     * Default constructor
     **/
    public OpenReactivateColonyMessage()
    {
        super();
    }

    public OpenReactivateColonyMessage(final String closestName, final int closestDistance, final int[] townHallPos)
    {
        super();
        this.closestName = closestName;
        this.closestDistance = closestDistance;
        this.townHallPos = townHallPos;
    }

    @Override
    public void onExecute(MessageContext ctx, boolean isLogicalServer)
    {
        new WindowTownHallColonyReactivate(townHallPos, closestName, closestDistance).open();
    }

    @Override
    public void toBytes(PacketBuffer buf)
    {
        buf.writeUtf(closestName);
        buf.writeInt(closestDistance);
        buf.writeBlockPos(townHallPos);
    }

    @Override
    public void fromBytes(PacketBuffer buf)
    {
        this.closestName = buf.readUtf(32767);
        this.closestDistance = buf.readInt();
        this.townHallPos = buf.readBlockPos();
    }
}


