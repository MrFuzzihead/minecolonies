package com.minecolonies.api.configuration;

import net.minecraftforge.common.config.Configuration;

public class CommonConfiguration extends AbstractConfiguration
{
    public final BooleanValue generateSupplyLoot;
    public final BooleanValue rsEnableDebugLogging;

    /**
     * Builds client configuration.
     *
     * @param config config builder
     */
    protected CommonConfiguration(final Configuration config)
    {
        createCategory(config, "gameplay");
        generateSupplyLoot = defineBoolean(config, "generatesupplyloot", true);
        finishCategory(config);

        createCategory(config, "requestsystem");
        rsEnableDebugLogging = defineBoolean(config, "enabledebuglogging", false);
        finishCategory(config);
    }
}
