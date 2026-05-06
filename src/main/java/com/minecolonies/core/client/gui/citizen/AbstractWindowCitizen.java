package com.minecolonies.core.client.gui.citizen;
import net.minecraft.world.entity.player.Player;

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
import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.AbstractWindowSkeleton;
import com.minecolonies.core.colony.buildings.views.AbstractBuildingView;
import com.minecolonies.core.debug.DebugPlayerManager;
import com.minecolonies.core.debug.gui.DebugWindowCitizen;
import com.minecolonies.core.network.messages.server.colony.OpenInventoryMessage;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

/**
 * Object (BOWindow: todo ModularUI2 removed) for the citizen.
 */
public abstract class AbstractWindowCitizen extends AbstractWindowSkeleton
{
    protected final IColonyView colony;

    /**
     * The citizenData.View object.
     */
    protected final ICitizenDataView citizen;

    /**
     * Constructor to initiate the citizen windows.
     *
     * @param citizen citizen to bind the window to.
     * @param ui the xml res loc.
     */
    public AbstractWindowCitizen(final ICitizenDataView citizen, final ResourceLocation ui)
    {
        super(ui);
        this.colony = citizen.getColony();
        this.citizen = citizen;

        registerButton("mainTab", () -> new MainWindowCitizen(citizen).open());
        registerButton("mainIcon", () -> new MainWindowCitizen(citizen).open());
        PaneBuilders.tooltipBuilder().hoverPane(findPaneByID("mainIcon")).build().setText(String.translatable("com.minecolonies.coremod.gui.citizen.main"));

        registerButton("requestTab", () -> new RequestWindowCitizen(citizen).open());
        registerButton("requestIcon", () -> new RequestWindowCitizen(citizen).open());
        PaneBuilders.tooltipBuilder().hoverPane(findPaneByID("requestIcon")).build().setText(String.translatable("com.minecolonies.coremod.gui.citizen.requests"));

        registerButton("inventoryTab", () -> Network.getNetwork().sendToServer(new OpenInventoryMessage(citizen.getColony(), citizen.getName(), citizen.getEntityId())));
        registerButton("inventoryIcon", () -> Network.getNetwork().sendToServer(new OpenInventoryMessage(citizen.getColony(), citizen.getName(), citizen.getEntityId())));
        PaneBuilders.tooltipBuilder().hoverPane(findPaneByID("inventoryIcon")).build().setText(String.translatable("com.minecolonies.coremod.gui.citizen.inventory"));

        registerButton("happinessTab", () -> new HappinessWindowCitizen(citizen).open());
        registerButton("happinessIcon", () -> new HappinessWindowCitizen(citizen).open());
        PaneBuilders.tooltipBuilder().hoverPane(findPaneByID("happinessIcon")).build().setText(String.translatable("com.minecolonies.coremod.gui.citizen.happiness"));

        registerButton("familyTab", () -> new FamilyWindowCitizen(citizen).open());
        registerButton("familyIcon", () -> new FamilyWindowCitizen(citizen).open());
        PaneBuilders.tooltipBuilder().hoverPane(findPaneByID("familyIcon")).build().setText(String.translatable("com.minecolonies.coremod.gui.citizen.family"));

        if (DebugPlayerManager.hasDebugEnabled(mc.player))
        {
            findPaneByID("debugTab").setVisible(true);
            findPaneByID("debugIcon").setVisible(true);
            registerButton("debugTab", () -> new DebugWindowCitizen(citizen).open());
            registerButton("debugIcon", () -> new DebugWindowCitizen(citizen).open());
            PaneBuilders.singleLineTooltip(String.translatable("com.minecolonies.coremod.debug.gui.tabicon"), findPaneByID("debugIcon"));
        }

        final IBuildingView building = citizen.getColony().getClientBuildingManager().getBuilding(citizen.getWorkBuilding());

        if (building instanceof AbstractBuildingView && building.getBuildingType() != ModBuildings.library.get())
        {
            findPaneByID("jobTab").setVisible(true);
            findPaneByID("jobIcon").setVisible(true);

            registerButton("jobTab", () -> new JobWindowCitizen(citizen).open());
            registerButton("jobIcon", () -> new JobWindowCitizen(citizen).open());
            PaneBuilders.tooltipBuilder().hoverPane(findPaneByID("jobIcon")).build().setText(String.translatable("com.minecolonies.coremod.gui.citizen.job"));
        }
        else
        {
            findPaneByID("jobTab").setVisible(false);
            findPaneByID("jobIcon").setVisible(false);
        }
    }
}



