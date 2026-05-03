package com.minecolonies.api.colony.buildings.modules;

import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.util.constant.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Default interface for all client side building modules.
 * Ported to 1.7.10: FriendlyByteBuf→PacketBuffer; Component→String; BlockUI→TODO stub; @OnlyIn(Dist.CLIENT)→@SideOnly(Side.CLIENT).
 */
public interface IBuildingModuleView
{
    /**
     * Deserialize the data on the client side.
     * @param buf the buffer to read it from.
     */
    void deserialize(@NotNull final PacketBuffer buf);

    /**
     * Set the building view of this module view.
     * @param buildingView the building view to set.
     * @return this module itself.
     */
    IBuildingModuleView setBuildingView(final IBuildingView buildingView);

    /**
     * Whether this module appears as a GUI page.
     * @return true to show the GUI page.
     */
    default boolean isPageVisible() { return true; }

    /**
     * Get the matching window for the module.
     * @return the window (ModularUI2 window stub).
     * TODO: port to ModularUI2 window
     */
    @SideOnly(Side.CLIENT)
    Object getWindow();

    /**
     * Get the icon string for the module view.
     * @return the icon identifier.
     */
    @Deprecated
    default String getIcon()
    {
        return "custom";
    }

    /**
     * Get the resource location of the icon for the module view.
     * @return the resource location.
     */
    default ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/" + getIcon() + ".png");
    }

    /**
     * Get the lang string for the title.
     * @return the lang string.
     */
    @Nullable
    String getDesc();

    IBuildingModuleView setColonyView(IColonyView colonyView);

    /**
     * Get the colony view the module belongs to.
     * @return the colony view.
     */
    IColonyView getColony();

    /**
     * Get the building view to this.
     * @return the building view.
     */
    IBuildingView getBuildingView();

    /**
     * Set the producer of this module
     * @param moduleSet
     * @return
     */
    <M extends IBuildingModule, V extends IBuildingModuleView> IBuildingModuleView setProducer(BuildingEntry.ModuleProducer<M, V> moduleSet);

    /**
     * Get the producer of this module
     * @return
     */
    <M extends IBuildingModule, V extends IBuildingModuleView> BuildingEntry.ModuleProducer<M, V> getProducer();
}
