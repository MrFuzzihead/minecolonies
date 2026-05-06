package com.minecolonies.core.colony.buildings.moduleviews;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;


// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.modules.building.WorkOrderModuleWindow;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * Client side version of the abstract class for building that handle workorders.
 */
public class WorkOrderListModuleView extends AbstractBuildingModuleView
{
    /**
     * The tool of the worker.
     */
    public WorkOrderListModuleView()
    {
        super();
    }

    @Override
    public String getDesc()
    {
        return String.translatable("com.minecolonies.coremod.gui.townhall.workorders");
    }

    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Object /* BOWindow: todo ModularUI2 */ getWindow()
    {
        return new WorkOrderModuleWindow(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/info.png");
    }
}



