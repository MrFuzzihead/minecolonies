package com.minecolonies.core.colony.workorders.view;

import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.buildings.workerbuildings.ITownHallView;
import com.minecolonies.api.util.constant.TranslationConstants;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingBuilder;
import net.minecraft.util.IChatComponent;

/**
 * The client side representation for a work order that the builder can take to build plantation fields.
 */
public class WorkOrderPlantationFieldView extends AbstractWorkOrderView
{
    @Override
    public String getDisplayName()
    {
        return getOrderTypePrefix(String.translatable(getTranslationKey()));
    }

    private String getOrderTypePrefix(String nameComponent)
    {
        return switch (this.getWorkOrderType())
        {
            case BUILD -> String.translatable(TranslationConstants.BUILDER_ACTION_BUILDING, nameComponent);
            case REPAIR -> String.translatable(TranslationConstants.BUILDER_ACTION_REPAIRING, nameComponent);
            case REMOVE -> String.translatable(TranslationConstants.BUILDER_ACTION_REMOVING, nameComponent);
            default -> nameComponent;
        };
    }

    @Override
    public boolean shouldShowIn(IBuildingView view)
    {
        return view instanceof ITownHallView || view instanceof BuildingBuilder.View;
    }
}


