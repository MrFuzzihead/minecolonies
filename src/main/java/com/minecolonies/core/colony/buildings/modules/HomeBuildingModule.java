package com.minecolonies.core.colony.buildings.modules;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModule;
import com.minecolonies.api.colony.buildings.modules.IDefinesCoreBuildingStatsModule;
import com.minecolonies.api.colony.buildings.modules.stat.IStat;
import com.minecolonies.core.client.gui.huts.WindowHutLiving;
import com.minecolonies.core.colony.buildings.views.LivingBuildingView;
// [1.7.10] int[] -> int x,y,z
import org.jetbrains.annotations.NotNull;

/**
 * The class of the citizen hut.
 */
public class HomeBuildingModule extends AbstractBuildingModule implements IDefinesCoreBuildingStatsModule
{
    @Override
    public IStat<Integer> getMaxInhabitants()
    {
        return (prev) -> Math.max(prev, building.getBuildingLevel());
    }

    /**
     * ClientSide representation of the building.
     */
    public static class View extends LivingBuildingView
    {
        /**
         * Creates an instance of the citizen hut window.
         *
         * @param c the colonyView.
         * @param l the position the hut is at.
         */
        public View(final IColonyView c, final int[] l)
        {
            super(c, l);
        }

        @NotNull
        @Override
        public Object /* BOWindow: todo ModularUI2 */ getWindow()
        {
            return new WindowHutLiving(this);
        }
    }
}



