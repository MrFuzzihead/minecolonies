package com.minecolonies.core.network.messages.client.colony;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.research.IResearchManager;
import io.netty.buffer.Unpooled;
// [1.7.10] Registries removed
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Message to synch research manager to colony.
 */
public class ColonyViewResearchManagerViewMessage implements IMessage
{
    private int             colonyId;
    private PacketBuffer researchManagerData;

    /**
     * Dimension of the colony.
     */
    private int /* ResourceKey */ dimension;

    /**
     * Empty constructor used when registering the
     */
    public ColonyViewResearchManagerViewMessage()
    {
        super();
    }

    /**
     * Creates a message to send the research manager to the client.
     * @param colony the colony.
     * @param researchManager the research manager.
     */
    public ColonyViewResearchManagerViewMessage(final IColony colony, @NotNull final IResearchManager researchManager)
    {
        super();
        this.colonyId = colony.getID();
        this.dimension = colony.getDimension();

        this.researchManagerData = new PacketBuffer(Unpooled.buffer());

        final NBTTagCompound researchCompound = new NBTTagCompound();
        researchManager.writeToNBT(researchCompound);
        this.researchManagerData.writeNbt(researchCompound);
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        colonyId = buf.readInt();
        dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buf.readUtf(32767)));
        researchManagerData = new PacketBuffer(Unpooled.buffer(buf.readableBytes()));
        buf.readBytes(researchManagerData, buf.readableBytes());
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        researchManagerData.resetReaderIndex();
        buf.writeInt(colonyId);
        buf.writeUtf(dimension.location().toString());
        buf.writeBytes(researchManagerData);
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
        final IColonyView colonyView = IColonyManager.getInstance().getColonyView(colonyId, dimension);
        if (colonyView != null)
        {
            colonyView.handleColonyViewResearchManagerUpdate(researchManagerData.readNbt());
        }
    }
}




