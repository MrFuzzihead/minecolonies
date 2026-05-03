package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.client.gui.townhall.WindowTownHallDeleteAbandonColony;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Message to open the colony founding covenant.
 */
public class OpenDeleteAbandonColonyMessage implements IMessage
{
    /**
     * Colony pos at which we are trying to place.
     */
    private int[] currentTownHallPos;

    /**
     * Colony pos we are deleting or abandoning.
     */
    private int[] oldColonyPos;

    /**
     * Old colony name.
     */
    private String oldColonyName;

    /**
     * Old colony id.
     */
    private int oldColonyId;

    /**
     * Default constructor
     **/
    public OpenDeleteAbandonColonyMessage()
    {
        super();
    }

    public OpenDeleteAbandonColonyMessage(final int[] currentTownHallPos, final String oldColonyName, final int[] oldColonyPos, final int oldColonyId)
    {
        super();
        this.currentTownHallPos = currentTownHallPos;
        this.oldColonyName = oldColonyName;
        this.oldColonyPos = oldColonyPos;
        this.oldColonyId = oldColonyId;
    }

    @Override
    public void onExecute(MessageContext ctx, boolean isLogicalServer)
    {
        new WindowTownHallDeleteAbandonColony(currentTownHallPos, oldColonyName, oldColonyPos).open();
    }

    @Override
    public void toBytes(PacketBuffer buf)
    {
        buf.writeBlockPos(currentTownHallPos);
        buf.writeUtf(oldColonyName);
        buf.writeBlockPos(oldColonyPos);
        buf.writeInt(oldColonyId);
    }

    @Override
    public void fromBytes(PacketBuffer buf)
    {
        this.currentTownHallPos = buf.readBlockPos();
        this.oldColonyName = buf.readUtf(32767);
        this.oldColonyPos = buf.readBlockPos();
        this.oldColonyId = buf.readInt();
    }
}


