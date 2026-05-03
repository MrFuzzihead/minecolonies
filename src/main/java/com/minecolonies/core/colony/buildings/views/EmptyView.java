package com.minecolonies.core.colony.buildings.views;

import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
// [1.7.10] int[] -> int x,y,z

/**
 * An Empty implementation of {@link IBuildingView}. Currently used for Stash.
 */
public class EmptyView extends AbstractBuildingView
{
    /**
     * Instantiates the view of the building.
     *
     * @param c the colonyView.
     * @param l the location of the block.
     */
    public EmptyView(final IColonyView c, final int[] l)
    {
        super(c, l);
    }
}


