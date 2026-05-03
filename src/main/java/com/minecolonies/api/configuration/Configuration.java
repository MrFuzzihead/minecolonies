package com.minecolonies.api.configuration;

// [1.7.10 BACKPORT] Forge 1.7.10 config system
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

/**
 * Mod root configuration.
 *
 * <p><b>1.7.10 Backport:</b> In 1.21 the three configuration sections (client, server,
 * common) were registered with FML via {@code ModLoadingContext.registerConfig(Type, spec)}.
 * In 1.7.10, Forge uses flat {@link Configuration} file objects per-section backed by
 * {@code .cfg} files. We create three separate files so that client-only and server-only
 * settings are still separated:
 * <ul>
 *   <li>{@code minecolonies-common.cfg} — common (both sides, synced)</li>
 *   <li>{@code minecolonies-server.cfg} — server-side settings</li>
 *   <li>{@code minecolonies-client.cfg} — client-side settings</li>
 * </ul>
 * All config files are saved to the standard Forge config directory
 * ({@code config/} next to the mods folder).</p>
 */
public class Configuration
{
    /** Loaded clientside, not synced. */
    private final ClientConfiguration clientConfig;

    /** Loaded serverside, synced on connection. */
    private final ServerConfiguration serverConfig;

    /** Common configuration, synced on connection. */
    private final CommonConfiguration commonConfiguration;

    /**
     * Builds configuration tree and loads values from disk.
     *
     * <p>Must be called during {@code FMLPreInitializationEvent}.</p>
     *
     * @param event the FML pre-init event used to locate the config directory.
     */
    public Configuration(final FMLPreInitializationEvent event)
    {
        final File configDir = event.getModConfigurationDirectory();

        // [1.7.10 BACKPORT] Three separate config files — one per section.
        // In 1.21 these were registered with ModLoadingContext.registerConfig().
        final net.minecraftforge.common.config.Configuration serverCfg = new net.minecraftforge.common.config.Configuration(new File(configDir, "minecolonies-server.cfg"));
        final net.minecraftforge.common.config.Configuration clientCfg = new net.minecraftforge.common.config.Configuration(new File(configDir, "minecolonies-client.cfg"));
        final net.minecraftforge.common.config.Configuration commonCfg = new net.minecraftforge.common.config.Configuration(new File(configDir, "minecolonies-common.cfg"));

        serverCfg.load();
        clientCfg.load();
        commonCfg.load();

        serverConfig       = new ServerConfiguration(serverCfg);
        clientConfig       = new ClientConfiguration(clientCfg);
        commonConfiguration = new CommonConfiguration(commonCfg);

        // Persist any new keys that were added since the last run.
        if (serverCfg.hasChanged()) serverCfg.save();
        if (clientCfg.hasChanged()) clientCfg.save();
        if (commonCfg.hasChanged()) commonCfg.save();
    }

    public ClientConfiguration getClient()
    {
        return clientConfig;
    }

    public ServerConfiguration getServer()
    {
        return serverConfig;
    }

    public CommonConfiguration getCommon()
    {
        return commonConfiguration;
    }
}
