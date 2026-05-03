package com.minecolonies.core.colony.buildings.moduleviews;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.modules.building.ConnectionModuleWindow;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * Client side version of colony connection module. Primarily a wrapper for the UI.
 */
public class ColonyConnectionModuleView extends AbstractBuildingModuleView
{
    /**
     * Constructor.
     */
    public ColonyConnectionModuleView()
    {
        super();
    }

    @Override
    public String getDesc()
    {
        return String.translatable("com.minecolonies.core.gui.connections");
    }

    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Object /* BOWindow: todo ModularUI2 */ getWindow()
    {
        return new ConnectionModuleWindow(buildingView, false);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/connection.png");
    }
}



