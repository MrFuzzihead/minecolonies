package com.minecolonies.core.colony.buildings.moduleviews;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.colony.managers.interfaces.IStatisticsManager;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.modules.building.WindowStatsModule;
import com.minecolonies.core.colony.managers.StatisticsManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Building statistic module.
 */
public class BuildingStatisticsModuleView extends AbstractBuildingModuleView
{
    /**
     * List of all beds.
     */
    private IStatisticsManager statisticsManager = new StatisticsManager();

    @Override
    public void deserialize(final @NotNull PacketBuffer buf)
    {
        statisticsManager.deserialize(buf);
    }

    @Override
    public Object /* BOWindow: todo ModularUI2 */ getWindow()
    {
        return new WindowStatsModule(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/stats.png");
    }

    @Override
    public String getDesc()
    {
        return String.translatable("com.minecolonies.core.gui.modules.stats");
    }

    /**
     * Get the statistic manager of the building.
     * @return the manager.
     */
    public IStatisticsManager getBuildingStatisticsManager()
    {
        return statisticsManager;
    }
}



