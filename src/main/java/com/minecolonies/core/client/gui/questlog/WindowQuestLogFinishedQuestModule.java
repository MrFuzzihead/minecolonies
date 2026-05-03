package com.minecolonies.core.client.gui.questlog;

// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.PaneBuilders;
import com.ldtteam.blockui.PaneParams;
import com.ldtteam.blockui.MouseEventCallback;
import com.ldtteam.blockui.controls.BOGuiGraphics;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ButtonHandler;
import com.ldtteam.blockui.controls.ButtonImage;
import com.ldtteam.blockui.controls.Image;
import com.ldtteam.blockui.controls.ItemIcon;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.Box;
import com.ldtteam.blockui.views.ScrollingList;
import com.ldtteam.blockui.views.SwitchView;
import com.ldtteam.blockui.views.View;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.quests.FinishedQuest;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.List;

import static com.minecolonies.api.util.constant.translation.GuiTranslationConstants.*;
import static com.minecolonies.core.client.gui.questlog.Constants.LABEL_COMPLETED_COUNT;
import static com.minecolonies.core.client.gui.questlog.Constants.LABEL_QUEST_NAME;

/**
 * Window quest log renderer for finished quests.
 */
public class WindowQuestLogFinishedQuestModule implements WindowQuestLogQuestModule<FinishedQuest>
{
    @Override
    public List<FinishedQuest> getQuestItems(final IColonyView colonyView)
    {
        return colonyView.getQuestManager().getFinishedQuests();
    }

    @Override
    public void renderQuestItem(final FinishedQuest quest, final IColonyView colonyView, final Pane row)
    {
        setText(row, LABEL_QUEST_NAME, String.translatable(QUEST_LOG_NAME_PREFIX).append(quest.template().getName()));

        if (quest.template().getMaxOccurrence() > 1)
        {
            setText(row,
              LABEL_COMPLETED_COUNT,
              String.translatable(QUEST_LOG_COMPLETED_MULTIPLE_TEXT, quest.finishedCount(), quest.template().getMaxOccurrence()).withStyle(ChatFormatting.GOLD));
        }
        else
        {
            setText(row, LABEL_COMPLETED_COUNT, String.translatable(QUEST_LOG_COMPLETED_ONCE_TEXT).withStyle(ChatFormatting.GOLD));
        }
    }

    /**
     * Quick setter for assigning text to a field, as well as providing a tooltip if the text is too long.
     *
     * @param container the container element for the text element.
     * @param id        the id of the text element.
     * @param String the text String to write as text on the element.
     */
    private void setText(final Pane container, final String id, final String String)
    {
        final Text label = container.findPaneOfTypeByID(id, Text.class);
        label.setText(String);

        if (label.getRenderedTextWidth() > label.getWidth())
        {
            PaneBuilders.tooltipBuilder()
              .append(String)
              .hoverPane(label)
              .build();
        }
    }
}




