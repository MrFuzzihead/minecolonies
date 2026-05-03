package com.minecolonies.api.configuration;

import net.minecraftforge.common.config.Configuration;

/**
 * Mod client configuration. Loaded clientside, not synced.
 */
public class ClientConfiguration extends AbstractConfiguration
{
    public final BooleanValue citizenVoices;
    public final BooleanValue neighborbuildingrendering;
    public final IntValue     neighborbuildingrange;
    public final IntValue     buildgogglerange;
    public final BooleanValue colonyteamborders;
    public final BooleanValue holidayFeatures;
    public final BooleanValue showdyetooltips;

    /**
     * Builds client configuration.
     *
     * @param config config
     */
    protected ClientConfiguration(final Configuration config)
    {
        createCategory(config, "gameplay");
        citizenVoices            = defineBoolean(config, "enablecitizenvoices", true);
        neighborbuildingrendering = defineBoolean(config, "neighborbuildingrendering", true);
        neighborbuildingrange    = defineInteger(config, "neighborbuildingrange", 4, -2, 16);
        buildgogglerange         = defineInteger(config, "buildgogglerange", 50, 1, 250);
        colonyteamborders        = defineBoolean(config, "colonyteamborders", true);
        holidayFeatures          = defineBoolean(config, "holidayfeatures", true);
        showdyetooltips          = defineBoolean(config, "showdyetooltips", true);

        swapToCategory(config, "pathfinding");

        finishCategory(config);
    }
}
