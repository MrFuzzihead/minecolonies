package com.minecolonies.core.network.messages.client.colony;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.colony.Colony;
import io.netty.buffer.Unpooled;
// [1.7.10] Registries removed
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Add or Update a ColonyView on the client.
 */
public class ColonyViewCitizenViewMessage implements IMessage
{
    private int          colonyId;
    private int          citizenId;
    private PacketBuffer citizenBuffer;

    /**
     * The dimension the citizen is in.
     */
    private int /* ResourceKey */ dimension;

    /**
     * Empty constructor used when registering the
     */
    public ColonyViewCitizenViewMessage()
    {
        super();
    }

    /**
     * Updates a {@link com.minecolonies.core.colony.CitizenDataView} of the citizens.
     *
     * @param colony  Colony of the citizen
     * @param citizen Citizen data of the citizen to update view
     */
    public ColonyViewCitizenViewMessage(@NotNull final Colony colony, @NotNull final ICitizenData citizen)
    {
        super();
        this.colonyId = colony.getID();
        this.citizenId = citizen.getId();
        this.citizenBuffer = new PacketBuffer(Unpooled.buffer());
        this.dimension = citizen.getColony().getDimension();
        citizen.serializeViewNetworkData(citizenBuffer);
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        colonyId = buf.readInt();
        citizenId = buf.readInt();
        dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buf.readUtf(32767)));
        this.citizenBuffer = new PacketBuffer(buf.retain());
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        citizenBuffer.resetReaderIndex();
        buf.writeInt(colonyId);
        buf.writeInt(citizenId);
        buf.writeUtf(dimension.location().toString());
        buf.writeBytes(citizenBuffer);
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
        IColonyManager.getInstance().handleColonyViewCitizensMessage(colonyId, citizenId, citizenBuffer, dimension);
        citizenBuffer.release();
    }
}



