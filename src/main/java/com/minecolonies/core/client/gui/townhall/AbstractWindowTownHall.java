package com.minecolonies.core.client.gui.townhall;

// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
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
import com.minecolonies.api.colony.buildings.workerbuildings.ITownHallView;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.AbstractBuildingMainWindow;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingTownHall;
import net.minecraft.util.ResourceLocation;

import static com.minecolonies.api.util.constant.WindowConstants.*;

/**
 * Object (BOWindow: todo ModularUI2 removed) for the town hall.
 */
public abstract class AbstractWindowTownHall extends AbstractBuildingMainWindow<ITownHallView>
{
    /**
     * Color constants for builder list.
     */
    public static final int RED       = Color.getByName("red", 0);
    public static final int DARKGREEN = Color.getByName("darkgreen", 0);
    public static final int ORANGE    = Color.getByName("orange", 0);
    public static final int YELLOW = Color.getByName("yellow", 0);

    /**
     * Constructor for the town hall window.
     *
     * @param townHall {@link BuildingTownHall.View}.
     */
    public AbstractWindowTownHall(final BuildingTownHall.View townHall, final String page)
    {
        super(townHall, new ResourceLocation(Constants.MOD_ID, "gui/townhall/").withSuffix(page));

        registerButton(BUTTON_ACTIONS, () -> new WindowMainPage(townHall).open());
        registerButton(BUTTON_INFOPAGE, () -> new WindowInfoPage(townHall).open());
        registerButton(BUTTON_PERMISSIONS, () -> new WindowPermissionsPage(townHall).open());
        registerButton(BUTTON_CITIZENS, () -> new WindowCitizenPage(townHall).open());
        registerButton(BUTTON_STATS, () -> new WindowStatsPage(townHall).open());
        registerButton(BUTTON_SETTINGS, () -> new WindowSettings(townHall).open());
        registerButton(BUTTON_ALLIANCE, () -> new WindowAlliancePage(townHall).open());

        findPaneOfTypeByID(getWindowId() + "0", Image.class).hide();
        findPaneOfTypeByID(getWindowId(), ButtonImage.class).hide();

        findPaneOfTypeByID(getWindowId() + "1", ButtonImage.class).show();
    }

    /**
     * Get the id that identifies the window.
     * @return the string id.
     */
    protected abstract String getWindowId();

    @Override
    protected boolean shouldRenderDefaultSidebar()
    {
        return false;
    }
}



