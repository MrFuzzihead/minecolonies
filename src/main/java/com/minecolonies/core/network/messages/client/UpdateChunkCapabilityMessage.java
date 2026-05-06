package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.colony.IColonyTagCapability;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.api.util.ChunkCapData;
import com.minecolonies.core.colony.ColonyChunkDataHandler;
import com.minecolonies.core.util.ChunkClientDataHelper;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Update the ChunkCapability with a colony.
 */
public class UpdateChunkCapabilityMessage implements IMessage
{
    private ChunkCapData chunkCapData;

    public UpdateChunkCapabilityMessage() { super(); }

    public UpdateChunkCapabilityMessage(@NotNull final ChunkCapData chunkCapData)
    {
        this.chunkCapData = chunkCapData;
    }

    public UpdateChunkCapabilityMessage(@NotNull final IColonyTagCapability tagCapability, final int x, final int z)
    {
        this.chunkCapData = new ChunkCapData(x, z, tagCapability.getOwningColony(), tagCapability.getStaticClaimColonies(), tagCapability.getAllClaimingBuildings());
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf) { chunkCapData = ChunkCapData.fromBytes(buf); }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf) { chunkCapData.toBytes(buf); }

    @Nullable
    @Override
    public Boolean getExecutionSide() { return Boolean.FALSE; }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        final net.minecraft.world.World world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;

        if (!WorldUtil.isChunkLoaded(world, new ChunkPos(chunkCapData.x, chunkCapData.z)))
        {
            ChunkClientDataHelper.addCapData(chunkCapData);
            return;
        }

        final Chunk chunk = world.getChunkFromChunkCoords(chunkCapData.x, chunkCapData.z);
        final IColonyTagCapability cap = ColonyChunkDataHandler.getColonyTagCapability(chunk);

        if (cap != null && cap.getOwningColony() != chunkCapData.getOwningColony())
        {
            ChunkClientDataHelper.applyCap(chunkCapData, chunk);
        }
    }
}
