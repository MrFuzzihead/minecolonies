package com.minecolonies.core.event.capabilityproviders;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import net.minecraft.world.level.chunk.LevelChunk;

// [1.7.10 BACKPORT] This class replaced by ColonyChunkDataHandler (ChunkAPI IChunkDataHandler).
//
// In 1.21, per-chunk colony data was attached via Forge's ICapabilitySerializable:
//   event.registerCapability(IColonyTagCapability.class);
//   // + @SubscribeEvent AttachCapabilitiesEvent<LevelChunk>
//
// In 1.7.10, this is replaced by ChunkAPI:
//   ChunkDataManager.registerDataHandler(new ColonyChunkDataHandler())  — called in preInit.
//
// This class is kept as a stub so that any remaining import references compile.

/**
 * Stub — replaced by {@link com.minecolonies.core.colony.ColonyChunkDataHandler}.
 * Not instantiated in 1.7.10.
 */
public final class MinecoloniesChunkCapabilityProvider
{
    private MinecoloniesChunkCapabilityProvider() { /* stub */ }
}
