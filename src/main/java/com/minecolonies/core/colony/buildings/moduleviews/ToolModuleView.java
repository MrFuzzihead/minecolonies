package com.minecolonies.core.colony.buildings.moduleviews;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;


// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.modules.building.ToolModuleWindow;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * Client side version of the abstract class for all buildings which allows to select tools.
 */
public class ToolModuleView extends AbstractBuildingModuleView
{
    /**
     * The worker specific tool.
     */
    private final Item tool;

    /**
     * The tool of the worker.
     * @param tool the item.
     */
    public ToolModuleView(final Item tool)
    {
        super();
        this.tool = tool;
    }

    @Override
    public String getDesc()
    {
        return String.translatable("com.minecolonies.coremod.gui.workerhuts.tools");
    }

    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Object /* BOWindow: todo ModularUI2 */ getWindow()
    {
        return new ToolModuleWindow(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/scepter.png");
    }

    /**
     * Get the correct tool.
     * @return the tool to give.
     */
    public Item getTool()
    {
        return tool;
    }
}



