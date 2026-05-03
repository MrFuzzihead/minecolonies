package com.ldtteam.blockui.controls;

import com.ldtteam.blockui.Pane;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

/**
 * [1.7.10] Compatibility stub for BlockUI DropDownList (a dropdown selector).
 */
public class DropDownList extends Pane
{
    public DropDownList() {}

    public interface DataProvider
    {
        int getElementCount();
        String getLabel(int index);
    }

    public int getSelectedIndex() { return 0; }
    public void setSelectedIndex(int index) {}
    public void setElements(List<String> elements) {}
    public void setHandler(Consumer<DropDownList> handler) {}
    public void setDataProvider(DataProvider provider) {}
    public void setScrollingListRenderer(Object renderer) {}
    public void setShowScrollbar(boolean show) {}
}


