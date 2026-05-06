package com.minecolonies.core.compatibility.journeymap;

// TODO: JourneyMap client API (journeymap.client.api.*) is not present in the 1.7.10 JourneyMap dep.
//       Re-enable this class when a compatible JourneyMap API jar is available.

import java.util.Optional;

/** Disabled JourneyMap wrapper - see TODO above. */
public class Journeymap
{
    private static Journeymap INSTANCE;

    public Journeymap(final Object jmap) {}

    public static Optional<Journeymap> getInstance() { return Optional.ofNullable(INSTANCE); }

    public Optional<JourneymapOptions> getOptions() { return Optional.empty(); }

    public void setOptions(final JourneymapOptions options) {}
}
