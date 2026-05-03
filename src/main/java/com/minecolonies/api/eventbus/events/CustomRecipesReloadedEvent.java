package com.minecolonies.api.eventbus.events;

// [1.7.10] eventbus removed
import cpw.mods.fml.common.eventhandler.Event;

/**
 * This event is fired on the client side whenever the CustomRecipeManager has been
 * populated. This occurs once on world load/connect and again whenever data-packs are reloaded.
 */
public class CustomRecipesReloadedEvent extends Event
{
    public CustomRecipesReloadedEvent()
    {
    }
}

