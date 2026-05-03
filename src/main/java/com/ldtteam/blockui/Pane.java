package com.ldtteam.blockui;

import net.minecraft.util.ResourceLocation;

/**
 * [1.7.10] Compatibility stub for BlockUI Pane base class.
 * The real Pane is in the BlockUI mod (1.16+).
 */
public class Pane
{
    public Pane() {}

    public String getID()
    {
        return "";
    }

    public void setPosition(final int x, final int y) {}

    public void setSize(final int w, final int h) {}

    public int getWidth() { return 0; }
    public int getHeight() { return 0; }

    public void off() {}

    public void on() {}

    public void disable() {}
    public void enable() {}

    public <T extends Pane> T findPaneOfTypeByID(final String id, final Class<T> type)
    {
        return null;
    }

    public Pane findPaneByID(final String id)
    {
        return null;
    }

    public Pane getParent()
    {
        return null;
    }
}


