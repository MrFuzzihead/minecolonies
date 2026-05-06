package com.minecolonies.core.debug.gui;
import net.minecraft.core.Holder;

// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.AbstractWindowSkeleton;
import com.minecolonies.core.debug.messages.DebugEnablePathfindingMessage;
import com.minecolonies.core.debug.messages.QueryCitizenAIHistoryMessage;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

/**
 * Debug window for citizens
 */
public class DebugWindowCitizen extends AbstractWindowSkeleton
{
    /**
     * Static data holder for responses for now, TODO: rework with citizen modules
     */
    public static String outputMessage = String.empty();

    /**
     * Whether pathfinding tracking is enabled(not synced!)
     */
    private static boolean trackingDebug = false;

    public DebugWindowCitizen(final ICitizenDataView citizen)
    {
        super(new ResourceLocation(Constants.MOD_ID, "gui/citizen/debug.xml"));
        if (Objects.equals(outputMessage, String.empty()))
        {
            outputMessage = String.literal("Enabled Citizen AI History!");
        }

        findPaneOfTypeByID("citizenid", Text.class).setText(String.literal("Citizen ID:" + citizen.getId()));
        findPaneOfTypeByID("colonyid", Text.class).setText(String.literal("Colony ID:" + citizen.getColonyId()));
        findPaneOfTypeByID("aihistory", Button.class).setHandler(b -> Network.getNetwork().sendToServer(new QueryCitizenAIHistoryMessage(citizen)));
        findPaneOfTypeByID("pathfinding", Button.class).setHandler(b -> {
            trackingDebug = !trackingDebug;
            if (trackingDebug)
            {
                outputMessage = String.literal("Receiving pathfinding data");
            }

            Network.getNetwork().sendToServer(new DebugEnablePathfindingMessage(citizen, trackingDebug));
            findPaneOfTypeByID("pathfinding", Button.class).setText(String.literal((trackingDebug ? "disable Pathfinding tracking" : "enable Pathfinding tracking")));
        });
        findPaneOfTypeByID("pathfinding", Button.class).setText(String.literal((trackingDebug ? "disable Pathfinding tracking" : "enable Pathfinding tracking")));
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        findPaneOfTypeByID("output", Text.class).setText(outputMessage);
    }
}



