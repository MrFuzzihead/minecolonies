package com.minecolonies.core.network.messages.client.colony;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;


import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.colony.Colony;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] Registries removed
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Add or Update a ColonyView on the client.
 */
public class ColonyViewMessage implements IMessage
{
    /**
     * The colony id.
     */
    private int colonyId;

    /**
     * If this is a new subscription.
     */
    private boolean isNewSubscription;

    /**
     * The buffer with the data.
     */
    private PacketBuffer colonyBuffer;

    /**
     * The dimension of the colony.
     */
    private int /* ResourceKey */ dim;

    /**
     * Empty constructor used when registering the
     */
    public ColonyViewMessage()
    {
        super();
    }

    /**
     * Add or Update a ColonyView on the client.
     *
     * @param colony Colony of the view to update.
     * @param buf    the bytebuffer.
     */
    public ColonyViewMessage(@NotNull final Colony colony, final PacketBuffer buf, boolean newSubscription)
    {
        this.colonyId = colony.getID();
        this.dim = colony.getDimension();
        this.colonyBuffer = new PacketBuffer(buf.copy());
        isNewSubscription = newSubscription;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        final PacketBuffer newBuf = new PacketBuffer(buf.retain());
        colonyId = newBuf.readInt();
        isNewSubscription = newBuf.readBoolean();
        dim = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(newBuf.readUtf(32767)));
        colonyBuffer = newBuf;
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        colonyBuffer.resetReaderIndex();
        buf.writeInt(colonyId);
        buf.writeBoolean(isNewSubscription);
        buf.writeUtf(dim.location().toString());
        buf.writeBytes(colonyBuffer);
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
            IColonyManager.getInstance().handleColonyViewMessage(colonyId, colonyBuffer, Minecraft.getInstance().World, isNewSubscription, dim);
        }
        colonyBuffer.release();
    }
}



