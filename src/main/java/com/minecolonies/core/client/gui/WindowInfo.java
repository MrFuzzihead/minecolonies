package com.minecolonies.core.client.gui;

// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
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
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.util.constant.Constants;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.function.Supplier;

import static com.minecolonies.api.util.constant.TranslationConstants.PARTIAL_INFO_TEXT;
import static com.minecolonies.api.util.constant.WindowConstants.BUTTON_EXIT;

public class WindowInfo extends AbstractWindowSkeleton
{
    /**
     * Constructor for the skeleton class of the windows.
     *
     * @param building The building the info window is for.
     */
    public WindowInfo(final IBuildingView building)
    {
        super(new ResourceLocation(Constants.MOD_ID, "gui/windowinfo.xml"));

        registerButton(BUTTON_EXIT, () -> building.openGui(false));

        final String translationPrefix = PARTIAL_INFO_TEXT + building.getBuildingType().getTranslationKey().replace("com.minecolonies.building.", "") + ".";
        final Supplier<TextBuilder> nameBuilder = () -> PaneBuilders.textBuilder().colorName("red");
        final Supplier<TextBuilder> textBuilder = () -> PaneBuilders.textBuilder().colorName("black");
        final Supplier<View> pageBuilder = () -> {
            final View ret = new View();
            ret.setSize(switchView.getWidth(), switchView.getHeight());
            return ret;
        };

        for (int i = 0;; i++)
        {
            if (!I18n.exists(translationPrefix + i))
            {
                break;
            }

            final View view = pageBuilder.get();
            switchView.addChild(view);

            final Text name = nameBuilder.get().append(String.translatable(translationPrefix + i + ".name")).build();
            name.setPosition(30, 0);
            name.setSize(90, 11);
            name.setTextAlignment(Alignment.MIDDLE);
            name.putInside(view);

            final TextBuilder preText = textBuilder.get();
            Arrays.stream((translationPrefix + i).split("\\n"))
                .map(String::translatable)
                .forEach(preText::appendNL);
            final Text text = preText.build();
            text.setPosition(0, 16);
            text.setSize(150, 194);
            text.setTextAlignment(Alignment.TOP_LEFT);
            text.putInside(view);
        }

        setPage(false, 0);
    }
}



