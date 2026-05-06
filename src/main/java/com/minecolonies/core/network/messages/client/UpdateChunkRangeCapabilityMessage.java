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
import net.minecraft.world.World;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Update the ChunkCapability with a colony.
 */
public class UpdateChunkRangeCapabilityMessage implements IMessage
{
    private final List<ChunkCapData> caps = new ArrayList<>();

    public UpdateChunkRangeCapabilityMessage() { super(); }

    public UpdateChunkRangeCapabilityMessage(@NotNull final World world, final int xC, final int zC, final int range, boolean checkLoaded)
    {
        for (int x = -range; x <= range; x++)
        {
            for (int z = -range; z <= range; z++)
            {
                final int chunkX = xC + x;
                final int chunkZ = zC + z;
                if (!checkLoaded || WorldUtil.isEntityChunkLoaded(world, chunkX, chunkZ))
                {
                    final Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
                    final IColonyTagCapability cap = ColonyChunkDataHandler.getColonyTagCapability(chunk);
                    if (cap != null)
                    {
                        caps.add(new ChunkCapData(chunkX, chunkZ, cap.getOwningColony(), cap.getStaticClaimColonies(), cap.getAllClaimingBuildings()));
                    }
                }
            }
        }
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        final int size = buf.readInt();
        for (int i = 0; i < size; i++) caps.add(ChunkCapData.fromBytes(buf));
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(caps.size());
        for (final ChunkCapData c : caps) c.toBytes(buf);
    }

    @Nullable
    @Override
    public Boolean getExecutionSide() { return Boolean.FALSE; }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        final World world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        for (final ChunkCapData data : caps)
        {
            if (!WorldUtil.isChunkLoaded(world, new ChunkPos(data.x, data.z)))
            {
                ChunkClientDataHelper.addCapData(data);
                continue;
            }
            final Chunk chunk = world.getChunkFromChunkCoords(data.x, data.z);
            ChunkClientDataHelper.applyCap(data, chunk);
        }
    }
}
