package com.minecolonies.core.colony.workorders.view;

import com.minecolonies.api.colony.buildings.views.IBuildingView;
import net.minecraft.util.IChatComponent;

/**
 * The client side representation for a work order that the builder can take to build mineshafts.
 */
public class WorkOrderMinerView extends AbstractWorkOrderView
{
    @Override
    public String getDisplayName()
    {
        return String.translatable(getTranslationKey());
    }

    @Override
    public boolean shouldShowIn(IBuildingView view)
    {
        return false;
    }
}


