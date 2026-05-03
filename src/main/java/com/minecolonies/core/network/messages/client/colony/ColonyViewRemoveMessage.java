package com.minecolonies.core.network.messages.client.colony;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.network.IMessage;
// [1.7.10] Registries removed
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Message for removing a view on the client, used for cleaning up after deletion
 */
public class ColonyViewRemoveMessage implements IMessage
{
    private int id;
    private int /* ResourceKey */ dimension;

    public ColonyViewRemoveMessage()
    {
        super();
    }

    public ColonyViewRemoveMessage(final int id, final int /* ResourceKey */ dimension)
    {
        this.id = id;
        this.dimension = dimension;
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeInt(id);
        buf.writeUtf(dimension.location().toString());
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        id = buf.readInt();
        dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buf.readUtf(32767)));
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
        IColonyManager.getInstance().removeColonyView(id, dimension);
    }
}



