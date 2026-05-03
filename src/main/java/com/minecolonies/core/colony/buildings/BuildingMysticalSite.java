package com.minecolonies.core.colony.buildings;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.IMysticalSite;
import com.minecolonies.core.client.gui.WindowHutMinPlaceholder;
import com.minecolonies.core.colony.buildings.views.AbstractBuildingView;
// [1.7.10] int[] -> int x,y,z
import org.jetbrains.annotations.NotNull;

public class BuildingMysticalSite extends AbstractBuilding implements IMysticalSite
{
    private static final String MYSTICAL_SITE = "mysticalsite";

    /**
     * Maximum building World
     */
    private static final int MAX_BUILDING_LEVEL = 5;

    /**
     * The constructor of the building.
     *
     * @param c the colony
     * @param l the position
     */
    public BuildingMysticalSite(@NotNull final IColony c, final int[] l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return MYSTICAL_SITE;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return MAX_BUILDING_LEVEL;
    }

    /**
     * The client side representation of the building.
     */
    public static class View extends AbstractBuildingView
    {
        /**
         * Instantiates the view of the building.
         *
         * @param c the colonyView.
         * @param l the location of the block.
         */
        public View(final IColonyView c, final int[] l)
        {
            super(c, l);
        }

        @NotNull
        @Override
        public Object /* BOWindow: todo ModularUI2 */ getWindow()
        {
            return new WindowHutMinPlaceholder<>(this);
        }
    }
}



