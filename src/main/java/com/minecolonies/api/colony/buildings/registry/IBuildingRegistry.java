package com.minecolonies.api.colony.buildings.registry;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.registry.SimpleRegistry;
// [1.7.10] IForgeRegistry → SimpleRegistry

public interface IBuildingRegistry
{

    static SimpleRegistry<BuildingEntry> getInstance()
    {
        return IMinecoloniesAPI.getInstance().getBuildingRegistry();
    }
}

