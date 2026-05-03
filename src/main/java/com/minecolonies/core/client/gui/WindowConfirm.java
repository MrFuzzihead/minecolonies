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
import com.minecolonies.api.util.constant.Constants;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

import static com.minecolonies.api.util.constant.WindowConstants.*;

/**
 * Confirm/Deny window option. Confirm close, deny return to previous UI.
 */
public class WindowConfirm extends AbstractWindowSkeleton
{
    /**
     * Constructor to initiate the confirm window.
     *
     * @param parent the parent window.
     * @param action the action to run on confirm.
     */
    public WindowConfirm(final AbstractWindowSkeleton parent, final Runnable action, final String title, final String warning)
    {
        super(parent, new ResourceLocation(Constants.MOD_ID, "gui/windowconfirm.xml"));
        registerButton(BUTTON_CONFIRM, this::close);
        registerButton(BUTTON_CANCEL, action);
        findPaneOfTypeByID(TITLE_LABEL, Text.class).setText(String.translatable(title));
        findPaneOfTypeByID(WARNING_LABEL, Text.class).setText(String.translatable(warning));
    }
}


