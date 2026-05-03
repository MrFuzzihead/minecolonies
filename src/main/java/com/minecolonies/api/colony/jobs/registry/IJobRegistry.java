package com.minecolonies.api.colony.jobs.registry;
import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.registry.SimpleRegistry;
// [1.7.10] IForgeRegistry ? SimpleRegistry
public interface IJobRegistry
{
    static SimpleRegistry<JobEntry> getInstance()
    {
        return IMinecoloniesAPI.getInstance().getJobRegistry();
    }
}
