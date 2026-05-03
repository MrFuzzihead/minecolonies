package com.minecolonies.core.client.gui.modules;

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
import com.ldtteam.blockui.Pane;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Extensible logic for windows, without having to use new base classes.
 * Additionally, this class requires you to implement a layout, which will be automatically constructed and injected into the window.
 */
public interface IWindowWithLayoutModule extends IWindowModule
{
    /**
     * Called after the layout is mounted to the parenting window.
     *
     * @param rootPane the root pane that was made from the layout.
     */
    default void onLayoutMounted(final Pane rootPane) {}

    /**
     * Get the layout used for rendering.
     *
     * @return the id of the layout file.
     */
    @NotNull
    ResourceLocation getLayout();
}

