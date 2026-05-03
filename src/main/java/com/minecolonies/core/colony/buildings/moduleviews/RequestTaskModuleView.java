package com.minecolonies.core.colony.buildings.moduleviews;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.modules.building.WindowHutRequestTaskModule;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Request task module to display tasks in the UI.
 */
public abstract class RequestTaskModuleView extends AbstractBuildingModuleView
{
    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Object /* BOWindow: todo ModularUI2 */ getWindow()
    {
        return new WindowHutRequestTaskModule(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/info.png");
    }

    @Override
    public String getDesc()
    {
        return String.translatable("com.minecolonies.coremod.gui.workerhuts.crafter.tasks");
    }

    /**
     * Get the specific task list.
     * @return the task list.
     */
    public abstract List<IToken<?>> getTasks();
}



