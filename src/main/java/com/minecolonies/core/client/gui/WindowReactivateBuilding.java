package com.minecolonies.core.client.gui;

// [1.7.10] blockui replaced by ModularUI2
import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.PaneBuilders;
import com.ldtteam.blockui.MouseEventCallback;
import com.ldtteam.blockui.controls.BOGuiGraphics;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ButtonHandler;
import com.ldtteam.blockui.controls.ButtonImage;
import com.ldtteam.blockui.controls.Color;
import com.ldtteam.blockui.controls.DropDownList;
import com.ldtteam.blockui.controls.Image;
import com.ldtteam.blockui.controls.ItemIcon;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.controls.TextField;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.Box;
import com.ldtteam.blockui.views.ScrollingList;
import com.ldtteam.blockui.views.SwitchView;
import com.ldtteam.blockui.views.View;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.buildings.registry.IBuildingRegistry;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.Network;
import com.minecolonies.core.network.messages.server.ReactivateBuildingMessage;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.WindowConstants.*;

/**
 * Window to reactivate a building.
 */
public class WindowReactivateBuilding extends AbstractWindowSkeleton
{
    /*
     * Building the worker is trying to place.
     */
    @NotNull
    private final int[] pos;

    /**
     * Creates a new instance of this window.
     * @param pos the position of the building.
     */
    public WindowReactivateBuilding(@NotNull final int[] pos)
    {
        super(new ResourceLocation(Constants.MOD_ID, "gui/windowreactivatebuilding.xml"));
        this.pos = pos;
        registerButton(BUTTON_REACTIVATE, this::reactivateClicked);
        registerButton(BUTTON_CANCEL, this::cancelClicked);


        if (Minecraft.getInstance().World.getBlockEntity(pos) instanceof TileEntityColonyBuilding tileEntityColonyBuilding)
        {
            final BuildingEntry buildingEntry = IBuildingRegistry.getInstance().getValue(tileEntityColonyBuilding.registryName);
            if (buildingEntry == ModBuildings.home.get() || buildingEntry == ModBuildings.tavern.get())
            {
                findPaneOfTypeByID("text", Text.class).setText(String.translatable("com.minecolonies.core.gui.reactivate.message.living", String.translatable(buildingEntry.getTranslationKey())));
            }
            else if (buildingEntry != null)
            {
                findPaneOfTypeByID("text", Text.class).setText(String.translatable("com.minecolonies.core.gui.reactivate.message.working", String.translatable(buildingEntry.getTranslationKey())));
            }
        }
    }

    /**
     * Reactivate the building.
     */
    private void reactivateClicked()
    {
        Network.getNetwork().sendToServer(new ReactivateBuildingMessage(pos));
        close();
    }


    /**
     * Cancel reactivation.
     */
    private void cancelClicked()
    {
        close();
    }
}



