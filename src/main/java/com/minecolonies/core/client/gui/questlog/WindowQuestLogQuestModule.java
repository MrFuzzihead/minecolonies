package com.minecolonies.core.client.gui.questlog;

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

import java.util.List;

/**
 * Interface for quest log renderers.
 *
 * @param <T> the quest instance type.
 */
public interface WindowQuestLogQuestModule<T>
{
    /**
     * Get the list of quests for this window.
     *
     * @param colonyView the colony instance.
     * @return a list of quest items.
     */
    List<T> getQuestItems(IColonyView colonyView);

    /**
     * Renderer method for an individual quest item. Called via the scrolling list.
     *
     * @param quest      the current quest instance.
     * @param colonyView the colony view instance.
     * @param row        the parenting row pane.
     */
    void renderQuestItem(final T quest, final IColonyView colonyView, final Pane row);

    /**
     * Method to instantiate quest tracking, does not have to be implemented.
     *
     * @param quest the quest instance.
     */
    default void trackQuest(final T quest) {}
}

