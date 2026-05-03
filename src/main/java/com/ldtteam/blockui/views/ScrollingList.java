package com.ldtteam.blockui.views;

import com.ldtteam.blockui.Pane;

import java.util.function.BiConsumer;

/**
 * [1.7.10] Compatibility stub for BlockUI ScrollingList.
 */
public class ScrollingList extends Pane
{
    public ScrollingList() {}

    public interface DataProvider
    {
        int getElementCount();
        void updateElement(int index, Pane rowPane);
    }

    public void setDataProvider(final DataProvider provider) {}

    public int getElementCount()
    {
        return 0;
    }

    public void refreshElementPanes() {}

    public void setDataProvider(final int count, final java.util.function.BiConsumer<Integer, Pane> updater) {}

    /** Lambda-friendly setDataProvider overload */
    public void setDataProvider(final java.util.function.IntSupplier count, final java.util.function.BiConsumer<Integer, Pane> updater) {}

    public int getListElementIndexByPane(final Pane pane) { return 0; }
}

