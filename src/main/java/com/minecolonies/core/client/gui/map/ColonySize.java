package com.minecolonies.core.client.gui.map;

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
import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.views.View;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.network.messages.client.colony.ColonyListMessage;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

/**
 * Represents a colony by size, returns the used image corresponding for each size.
 */
public enum ColonySize
{
    SMALL(new ResourceLocation(Constants.MOD_ID, "gui/map/colonysmall.xml"), 25),
    MEDIUM(new ResourceLocation(Constants.MOD_ID, "gui/map/colonymedium.xml"), 75),
    LARGE(new ResourceLocation(Constants.MOD_ID, "gui/map/colonylarge.xml"), 5000);

    private final ResourceLocation imagePath;

    private final int maxCitizens;

    ColonySize(final ResourceLocation imagePath, final int maxCitizens)
    {
        this.imagePath = imagePath;
        this.maxCitizens = maxCitizens;
    }

    public static View createViewForInfo(final ColonyListMessage.ColonyInfo colony)
    {
        final View colonyPane = new View();

        final ColonySize size = getSizeByCount(colony.getCitizencount());

        Loader.createFromXMLFile(size.imagePath, colonyPane);

        final Pane background = colonyPane.findPaneByID("background");
        colonyPane.setSize(background.getWidth(), background.getHeight());

        final Text colonyName = colonyPane.findPaneOfTypeByID("textcontent", Text.class);
        colonyName.setText(String.literal(colony.getName()));

        return colonyPane;
    }

    public static ColonySize getSizeByCount(final int count)
    {
        for (ColonySize size : values())
        {
            if (count < size.maxCitizens)
            {
                return size;
            }
        }
        return SMALL;
    }
}


