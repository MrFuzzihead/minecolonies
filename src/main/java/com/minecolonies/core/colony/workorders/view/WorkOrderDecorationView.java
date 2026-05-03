package com.minecolonies.core.colony.workorders.view;

import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.buildings.workerbuildings.ITownHallView;
import com.minecolonies.api.util.constant.TranslationConstants;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingBuilder;
import net.minecraft.util.IChatComponent;

/**
 * The client side representation for a work order that the builder can take to build decorations.
 */
public class WorkOrderDecorationView extends AbstractWorkOrderView
{
    @Override
    public String getDisplayName()
    {
        return getOrderTypePrefix(String.translatable(getTranslationKey()));
    }

    private String getOrderTypePrefix(String nameComponent)
    {
        switch (this.getWorkOrderType())
        {
            case BUILD:
                return String.translatable(TranslationConstants.BUILDER_ACTION_BUILDING, nameComponent);
            case UPGRADE:
                return String.translatable(TranslationConstants.BUILDER_ACTION_UPGRADING, nameComponent, getCurrentLevel(), getTargetLevel());
            case REPAIR:
                return String.translatable(TranslationConstants.BUILDER_ACTION_REPAIRING, nameComponent);
            case REMOVE:
                return String.translatable(TranslationConstants.BUILDER_ACTION_REMOVING, nameComponent);
            default:
                return nameComponent;
        }
    }

    @Override
    public boolean shouldShowIn(IBuildingView view)
    {
        return view instanceof ITownHallView || view instanceof BuildingBuilder.View;
    }
}


