package com.minecolonies.core.client.gui;

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
import com.minecolonies.api.colony.buildings.modules.IBuildingModuleView;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.core.colony.buildings.views.AbstractBuildingView;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static com.minecolonies.api.util.constant.WindowConstants.DESC_LABEL;

/**
 * Generic module window class. This creates the navigational menu.
 */
public abstract class AbstractModuleWindow<T extends IBuildingModuleView> extends AbstractBuildingWindow<IBuildingView>
{
    /**
     * Module view.
     */
    protected final T moduleView;

    /**
     * Constructor for the window.
     *
     * @param moduleView {@link AbstractBuildingView}.
     * @param resource   window resource location.
     */
    public AbstractModuleWindow(final T moduleView, final ResourceLocation resource)
    {
        this(null, moduleView, resource);
    }

    /**
     * Constructor for the window.
     *
     * @param parent     the parent window.
     * @param moduleView {@link AbstractBuildingView}.
     * @param resource   window resource location.
     */
    public AbstractModuleWindow(final Object /* BOWindow: todo ModularUI2 */ parent, final T moduleView, final ResourceLocation resource)
    {
        super(parent, moduleView.getBuildingView(), resource);
        this.moduleView = moduleView;

        setHeader(Optional.ofNullable(moduleView.getDesc()).map(String::copy).orElse(null));
    }

    /**
     * Update the header
     *
     * @param header the header text.
     */
    protected void setHeader(@Nullable final String header)
    {
        final Text labelPane = window.findPaneOfTypeByID(DESC_LABEL, Text.class);
        if (labelPane != null && header != null)
        {
            labelPane.setText(header);
        }
    }
}


