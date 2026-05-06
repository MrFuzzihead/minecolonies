package com.minecolonies.core.compatibility.journeymap;

// TODO: JourneyMap client API (journeymap.client.api.*) is not present in the 1.7.10 JourneyMap dep.
//       Re-enable this class when a compatible JourneyMap API jar is available.

import com.minecolonies.api.colony.IColonyView;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/** Disabled colony deathpoints - see TODO above. */
public class ColonyDeathpoints
{
    private ColonyDeathpoints() {}

    public static void clear() {}

    public static void unload(@NotNull final Journeymap jmap, @NotNull final int dimension) {}

    public static void updateGraves(@NotNull final Journeymap jmap, @NotNull final IColonyView colony, @NotNull final Set<int[]> graves) {}

    public static void updateChunk(@NotNull final Journeymap jmap, @NotNull final int dimension, @NotNull final Object chunk) {}
}
