package com.minecolonies.api.client;

import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

/**
 * Key mappings
 */
public class ModKeyMappings
{
    private static final String CATEGORY = "key.minecolonies.categories.general";

    /**
     * Toggle
     */
    public static final KeyBinding TOGGLE_GOGGLES = new KeyBinding("key.minecolonies.toggle_goggles", Keyboard.KEY_NONE, CATEGORY);

    /**
     * Register key mappings
     */
    public static void register()
    {
        ClientRegistry.registerKeyBinding(TOGGLE_GOGGLES);
    }

    /**
     * Private constructor to hide the implicit one.
     */
    private ModKeyMappings()
    {
        /*
         * Intentionally left empty.
         */
    }
}

