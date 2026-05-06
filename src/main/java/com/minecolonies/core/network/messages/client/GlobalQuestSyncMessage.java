package com.minecolonies.core.network.messages.client;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;


import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.datalistener.QuestJsonListener;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The message used to synchronize global quest data from a server to a remote client.
 */
public class GlobalQuestSyncMessage implements IMessage
{

    /**
     * The buffer with the data.
     */
    private PacketBuffer questBuffer;

    /**
     * Empty constructor used when registering the message
     */
    public GlobalQuestSyncMessage()
    {
        super();
    }

    /**
     * Add or Update QuestData on the client.
     *
     * @param buf the bytebuffer.
     */
    public GlobalQuestSyncMessage(final PacketBuffer buf)
    {
        this.questBuffer = new PacketBuffer(buf.copy());
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        questBuffer = new PacketBuffer(buf.retain());
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        questBuffer.resetReaderIndex();
        buf.writeBytes(questBuffer);
    }

    @Nullable
    @Override
    public Boolean getExecutionSide()
    {
        return Boolean.FALSE;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        if (Minecraft.getInstance().World != null)
        {
            QuestJsonListener.readGlobalQuestPackets(questBuffer);
        }
        questBuffer.release();
    }
}



