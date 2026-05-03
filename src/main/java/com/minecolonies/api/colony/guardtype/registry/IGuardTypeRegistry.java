package com.minecolonies.api.colony.guardtype.registry;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.guardtype.GuardType;
import com.minecolonies.api.registry.SimpleRegistry;
// [1.7.10] IForgeRegistry → SimpleRegistry

public interface IGuardTypeRegistry
{

    static SimpleRegistry<GuardType> getInstance()
    {
        return IMinecoloniesAPI.getInstance().getGuardTypeRegistry();
    }
}
