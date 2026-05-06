package com.minecolonies.core.research;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;


import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.research.IGlobalResearchTree;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The message used to synchronize global research trees from a server to a remote client.
 */
public class GlobalResearchTreeMessage implements IMessage
{

    /**
     * The buffer with the data.
     */
    private PacketBuffer treeBuffer;

    /**
     * Empty constructor used when registering the message
     */
    public GlobalResearchTreeMessage()
    {
        super();
    }

    /**
     * Add or Update a GlobalResearchTree on the client.
     *
     * @param buf               the bytebuffer.
     */
    public GlobalResearchTreeMessage(final PacketBuffer buf)
    {
        this.treeBuffer = new PacketBuffer(buf.copy());
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        treeBuffer = new PacketBuffer(buf.retain());
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeBytes(treeBuffer);
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
            IGlobalResearchTree.getInstance().handleGlobalResearchTreeMessage(treeBuffer);
        }
        treeBuffer.release();
    }
}



