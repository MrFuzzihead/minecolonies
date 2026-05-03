package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.network.IMessage;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

/**
 * Asks the client to stop its music
 */
public class StopMusicMessage implements IMessage
{
    public StopMusicMessage()
    {
        super();
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {

    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {

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
        Minecraft.getInstance().getSoundManager().stop();
        Minecraft.getInstance().getMusicManager().stopPlaying();
    }
}



