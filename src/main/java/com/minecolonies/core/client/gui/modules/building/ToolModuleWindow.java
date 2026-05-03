package com.minecolonies.core.client.gui.modules.building;

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
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.AbstractModuleWindow;
import com.minecolonies.core.colony.buildings.moduleviews.ToolModuleView;
import com.minecolonies.core.network.messages.server.colony.building.GiveToolMessage;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
// [1.7.10] registries removed

public class ToolModuleWindow extends AbstractModuleWindow<ToolModuleView>
{
    /**
     * ID of the button to give tool
     */
    private static final String BUTTON_GIVE_TOOL = "giveTool";

    /**
     * Constructor for the minimum stock window view.
     *
     * @param moduleView the module view.
     */
    public ToolModuleWindow(final ToolModuleView moduleView)
    {
        super(moduleView,  new ResourceLocation(Constants.MOD_ID, "gui/layouthuts/layouttool.xml"));

        findPaneOfTypeByID("desc", Text.class).setText(String.translatable("com.minecolonies.coremod.gui.tooldesc." + ForgeRegistries.ITEMS.getKey(moduleView.getTool()).getPath()));
        registerButton(BUTTON_GIVE_TOOL, this::givePlayerScepter);
    }

    /**
     * Send message to player to add scepter to his inventory.
     */
    private void givePlayerScepter()
    {
        Network.getNetwork().sendToServer(new GiveToolMessage(buildingView, moduleView.getTool()));
    }
}


