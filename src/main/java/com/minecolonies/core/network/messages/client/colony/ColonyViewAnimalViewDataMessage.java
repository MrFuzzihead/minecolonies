package com.minecolonies.core.network.messages.client.colony;
import net.minecraft.world.entity.animal.Animal;

import com.minecolonies.api.colony.IAnimalData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.IVisitorData;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.util.Log;
import io.netty.buffer.Unpooled;
// [1.7.10] Registries removed
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Sends visitor data to the client
 */
public class ColonyViewAnimalViewDataMessage implements IMessage
{
    /**
     * The colony id
     */
    private int colonyId;

    /**
     * The dimension the citizen is in.
     */
    private int /* ResourceKey */ dimension;

    /**
     * Visiting entity data
     */
    private Set<IAnimalData> animals;

    /**
     * Visitor buf to read on client side.
     */
    private PacketBuffer animalBuf;

    /**
     * If a general refresh is necessary,
     */
    private boolean refresh;

    /**
     * Empty constructor used when registering the
     */
    public ColonyViewAnimalViewDataMessage()
    {
        super();
    }

    /**
     * Updates a {@link com.minecolonies.core.colony.CitizenDataView} of the citizens.
     *
     * @param colony Colony of the citizen
     */
    public ColonyViewAnimalViewDataMessage(@NotNull final IColony colony, @NotNull final Set<IAnimalData> animals, final boolean refresh)
    {
        super();
        this.colonyId = colony.getID();
        this.dimension = colony.getDimension();
        this.animals = animals;
        this.refresh = refresh;

        animalBuf = new PacketBuffer(Unpooled.buffer());
        for (final IAnimalData data : animals)
        {
            animalBuf.writeInt(data.getId());
            data.serializeViewNetworkData(animalBuf);
        }
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        colonyId = buf.readInt();
        dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buf.readUtf(32767)));
        refresh = buf.readBoolean();
        this.animalBuf = new PacketBuffer(buf.retain());
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        animalBuf.resetReaderIndex();
        buf.writeInt(colonyId);
        buf.writeUtf(dimension.location().toString());
        buf.writeBoolean(refresh);
        buf.writeInt(animals.size());
        buf.writeBytes(animalBuf);
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
        final IColonyView colony = IColonyManager.getInstance().getColonyView(colonyId, dimension);

        if (colony == null)
        {
            Log.getLogger().warn("Received animal data for nonexisting colony:" + colonyId + " dim:" + dimension);
        }
        else
        {
            colony.handleColonyViewAnimalMessage(animalBuf, refresh);
        }
        animalBuf.release();
    }
}



