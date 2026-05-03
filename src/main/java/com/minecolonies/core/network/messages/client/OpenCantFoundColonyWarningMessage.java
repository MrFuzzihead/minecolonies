package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.client.gui.townhall.WindowTownHallCantCreateColony;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText

/**
 * Message to open the colony founding covenant.
 */
public class OpenCantFoundColonyWarningMessage implements IMessage
{
    /**
     * Colony pos at which we are trying to place.
     */
    private int[] townHallPos;

    /**
     * Warning message to display why colony creation is not possible.
     */
    private String warningMessageTranslationKey;

    /**
     * If we need to set the config setting tooltip.
     */
    private boolean displayConfigTooltip;

    /**
     * Default constructor
     **/
    public OpenCantFoundColonyWarningMessage()
    {
        super();
    }

    public OpenCantFoundColonyWarningMessage(final String warningMessageTranslationKey, final int[] townHallPos, final boolean displayConfigTooltip)
    {
        super();
        this.warningMessageTranslationKey = warningMessageTranslationKey;
        this.townHallPos = townHallPos;
        this.displayConfigTooltip = displayConfigTooltip;
    }

    @Override
    public void onExecute(MessageContext ctx, boolean isLogicalServer)
    {
        new WindowTownHallCantCreateColony(townHallPos, (String) warningMessageTranslationKey, displayConfigTooltip).open();
    }

    @Override
    public void toBytes(PacketBuffer buf)
    {
        buf.writeComponent(warningMessageTranslationKey);
        buf.writeBlockPos(townHallPos);
        buf.writeBoolean(displayConfigTooltip);
    }

    @Override
    public void fromBytes(PacketBuffer buf)
    {
        this.warningMessageTranslationKey = buf.readComponent();
        this.townHallPos = buf.readBlockPos();
        this.displayConfigTooltip = buf.readBoolean();
    }
}



