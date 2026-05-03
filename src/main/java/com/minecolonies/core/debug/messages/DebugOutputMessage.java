package com.minecolonies.core.debug.messages;

import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.debug.gui.DebugWindowCitizen;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Message for sending debug text to the client. Used for citizen debug window for now
 */
public class DebugOutputMessage implements IMessage
{
    /**
     * The debug information to be displayed in the output
     */
    private String debugInfo;

    /**
     * Whether to clear the output first
     */
    private boolean clear = false;

    public DebugOutputMessage()
    {
        super();
    }

    public DebugOutputMessage(final String message, final boolean clear)
    {
        this.debugInfo = message;
        this.clear = clear;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        debugInfo = buf.readComponent();
        clear = buf.readBoolean();
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeComponent(debugInfo);
        buf.writeBoolean(clear);
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
        if (clear)
        {
            DebugWindowCitizen.outputMessage = String.literal("").append(debugInfo);
        }
        else
        {
            DebugWindowCitizen.outputMessage.append(String.literal("\n")).append(debugInfo);
        }
    }
}


