package com.minecolonies.core.colony.interactionhandling.registry;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.interactionhandling.registry.InteractionResponseHandlerEntry;
import net.minecraftforge.registries.IForgeRegistry;
// [1.7.10] registries removed

public interface IInteractionResponseHandlerRegistry
{
    static IForgeRegistry<InteractionResponseHandlerEntry> getInstance()
    {
        return IMinecoloniesAPI.getInstance().getInteractionResponseHandlerRegistry();
    }
}

