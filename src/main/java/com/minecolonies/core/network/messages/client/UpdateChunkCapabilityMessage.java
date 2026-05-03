package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.colony.IColonyTagCapability;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.api.util.ChunkCapData;
import com.minecolonies.core.util.ChunkClientDataHelper;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.world.World.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.colony.IColony.CLOSE_COLONY_CAP;

/**
 * Update the ChunkCapability with a colony.
 */
public class UpdateChunkCapabilityMessage implements IMessage
{
    /**
     * The chunk cap data
     */
    private ChunkCapData chunkCapData;

    /**
     * Empty constructor used when registering the
     */
    public UpdateChunkCapabilityMessage()
    {
        super();
    }

    /**
     * Create a message to update the chunk cap on the client side.
     *
     * @param chunkCapData the data.
     */
    public UpdateChunkCapabilityMessage(@NotNull final ChunkCapData chunkCapData)
    {
        this.chunkCapData = chunkCapData;
    }

    public UpdateChunkCapabilityMessage(@NotNull final IColonyTagCapability tagCapability, final int x, final int z)
    {
        this.chunkCapData = new ChunkCapData(x, z, tagCapability.getOwningColony(), tagCapability.getStaticClaimColonies(), tagCapability.getAllClaimingBuildings());
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        chunkCapData = ChunkCapData.fromBytes(buf);
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        chunkCapData.toBytes(buf);
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
        final ClientLevel world = Minecraft.getInstance().World;

        if (!WorldUtil.isChunkLoaded(world, new ChunkPos(chunkCapData.x, chunkCapData.z)))
        {
            ChunkClientDataHelper.addCapData(chunkCapData);
            return;
        }

        final LevelChunk chunk = world.getChunk(chunkCapData.x, chunkCapData.z);
        final IColonyTagCapability cap = chunk.getCapability(CLOSE_COLONY_CAP, null).orElseGet(null);

        if (cap != null && cap.getOwningColony() != chunkCapData.getOwningColony())
        {
            ChunkClientDataHelper.applyCap(chunkCapData, chunk);
        }
    }
}


