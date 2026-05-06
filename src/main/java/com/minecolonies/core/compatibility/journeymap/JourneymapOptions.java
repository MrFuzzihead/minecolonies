package com.minecolonies.core.compatibility.journeymap;

// TODO: JourneyMap client API (journeymap.client.api.*) is not present in the 1.7.10 JourneyMap dep.
//       Re-enable this class when a compatible JourneyMap API jar is available.

/** Disabled JourneyMap options - see TODO above. */
public class JourneymapOptions
{
    public enum BorderStyle { HIDDEN, OUTLINE, FILLED }
    public enum RaiderColor
    {
        NONE, HOSTILE, RED, ORANGE, YELLOW;
        public Object getColor() { return null; }
    }

    public static BorderStyle getBorderFullscreenStyle(final java.util.Optional<JourneymapOptions> opts) { return BorderStyle.HIDDEN; }
    public static BorderStyle getBorderMinimapStyle(final java.util.Optional<JourneymapOptions> opts) { return BorderStyle.HIDDEN; }
    public static boolean getDeathpoints(final java.util.Optional<JourneymapOptions> opts) { return false; }
    public static boolean getShowColonyName(final java.util.Optional<JourneymapOptions> opts) { return false; }
    public static boolean getShowColonistNameMinimap(final java.util.Optional<JourneymapOptions> opts) { return false; }
    public static boolean getShowColonistNameFullscreen(final java.util.Optional<JourneymapOptions> opts) { return false; }
    public static boolean getShowColonistTooltip(final java.util.Optional<JourneymapOptions> opts) { return false; }
    public static boolean getShowColonistTeamColour(final java.util.Optional<JourneymapOptions> opts) { return false; }
    public static boolean getShowGuards(final java.util.Optional<JourneymapOptions> opts) { return false; }
    public static boolean getShowCitizens(final java.util.Optional<JourneymapOptions> opts) { return false; }
    public static boolean getShowVisitors(final java.util.Optional<JourneymapOptions> opts) { return false; }
    public static RaiderColor getRaiderColor(final java.util.Optional<JourneymapOptions> opts) { return RaiderColor.NONE; }
}
