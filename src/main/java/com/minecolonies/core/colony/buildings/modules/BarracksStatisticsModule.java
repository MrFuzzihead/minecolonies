package com.minecolonies.core.colony.buildings.modules;

import java.util.List;

import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.managers.interfaces.IStatisticsManager;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingBarracks;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import static com.minecolonies.core.colony.buildings.modules.BuildingModules.STATS_MODULE;

/**
 * Building statistic module.
 */
public class BarracksStatisticsModule extends BuildingStatisticsModule
{
    /**
     * Serializes the composite stats of all towers associated with this barracks
     * 
     * @param buf      the buffer to write to
     * @param fullSync whether to serialize the full stats or only the dirty ones
     */
    @Override
    public void serializeToView(final PacketBuffer buf, final boolean fullSync)
    {
        this.getBuildingStatisticsManager().clear();
        List<int[]> towers = ((BuildingBarracks) building).getTowers();

        for (final int[] towerPos : towers)
        {
            IBuilding tower = building.getColony().getServerBuildingManager().getBuilding(towerPos);
            if (tower != null)
            {
                BuildingStatisticsModule towerStats = tower.getModule(STATS_MODULE);
                IStatisticsManager.aggregateStats(this.getBuildingStatisticsManager(), towerStats.getBuildingStatisticsManager());
            }
        }

        this.getBuildingStatisticsManager().serialize(buf, fullSync);
    }
}


