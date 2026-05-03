package com.minecolonies.core.colony.buildings.moduleviews;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.modules.building.EnchanterStationModuleWindow;
import com.minecolonies.core.network.messages.server.colony.building.enchanter.EnchanterWorkerSetMessage;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnchanterStationsModuleView extends AbstractBuildingModuleView
{
    /**
     * List of buildings the enchanter gathers experience from.
     */
    private List<int[]> buildingToGatherFrom = new ArrayList<>();

    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {
        final int size = buf.readInt();
        buildingToGatherFrom.clear();
        for (int i = 0; i < size; i++)
        {
            buildingToGatherFrom.add(buf.readBlockPos());
        }
    }

    /**
     * Getter for the list.
     *
     * @return the list.
     */
    public List<int[]> getBuildingsToGatherFrom()
    {
        return buildingToGatherFrom;
    }

    /**
     * Add a new worker to gather xp from.
     *
     * @param int[] the pos of the building.
     */
    public void addWorker(final int[] blockPos)
    {
        buildingToGatherFrom.add(blockPos);
        Network.getNetwork().sendToServer(new EnchanterWorkerSetMessage(buildingView, blockPos, true));
    }

    /**
     * Remove a worker to stop gathering from.
     *
     * @param int[] the pos of that worker.
     */
    public void removeWorker(final int[] blockPos)
    {
        buildingToGatherFrom.remove(blockPos);
        Network.getNetwork().sendToServer(new EnchanterWorkerSetMessage(buildingView, blockPos, false));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Object /* BOWindow: todo ModularUI2 */ getWindow()
    {
        return new EnchanterStationModuleWindow(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/entity.png");
    }
    
    @Override
    public String getDesc()
    {
        return String.translatable("com.minecolonies.gui.workerhuts.enchanter.workers");
    }
}





