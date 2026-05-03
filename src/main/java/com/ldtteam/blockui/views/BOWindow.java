package com.ldtteam.blockui.views;

import com.ldtteam.blockui.Pane;
import net.minecraft.util.ResourceLocation;

/**
 * [1.7.10] Compatibility stub for BlockUI BOWindow base class.
 * The real BOWindow extends Screen/GuiScreen in BlockUI (1.16+).
 */
public abstract class BOWindow extends Pane
{
    /** Resource location of the XML layout file. */
    public ResourceLocation xmlResourceLocation;

    protected BOWindow(final ResourceLocation resource)
    {
        this.xmlResourceLocation = resource;
    }

    public void onOpened() {}

    public void onUpdate() {}

    public void onClosed() {}

    public void close() {}

    public void open() {}
}

