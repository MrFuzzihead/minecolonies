package com.minecolonies.core.compatibility.journeymap;

// TODO: JourneyMap client API (journeymap.client.api.*) is not present in the 1.7.10 JourneyMap dep.
//       Re-enable this class when a compatible JourneyMap API jar is available.

import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;

/** Disabled colony border mapping - see TODO above. */
public class ColonyBorderMapping
{
    private ColonyBorderMapping() {}

    public static String getCurrentColony() { return ""; }

    public static void load(@NotNull final Journeymap jmap, @NotNull final int dimension) {}

    public static void unload(@NotNull final Journeymap jmap, @NotNull final int dimension) {}

    public static void updateChunk(@NotNull final Journeymap jmap, @NotNull final int dimension, @NotNull final LevelChunk chunk) {}

    public static void updatePending(@NotNull final Journeymap jmap, @NotNull final int dimension) {}
}
