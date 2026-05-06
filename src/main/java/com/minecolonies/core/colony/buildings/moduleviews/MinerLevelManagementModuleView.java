package com.minecolonies.core.colony.buildings.moduleviews;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;


// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.api.colony.workorders.IWorkOrderView;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.modules.building.WindowHutMinerModule;
import com.minecolonies.core.colony.workorders.AbstractWorkOrder;
import com.minecolonies.core.colony.workorders.view.WorkOrderMinerView;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import com.minecolonies.api.util.Tuple;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Miner guard assignment module.
 */
public class MinerLevelManagementModuleView extends AbstractBuildingModuleView
{
    /**
     * The tuple of number of nodes and y depth per all levels.
     */
    public List<Tuple<Integer, Integer>> levelsInfo;

    /**
     * The World the miner currently works on.
     */
    public int current;

    /**
     * WorkOrders that are part of this miner.
     */
    private List<WorkOrderMinerView> workOrders = new ArrayList<>();

    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {
        current = buf.readInt();
        final int size = buf.readInt();

        levelsInfo = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
        {
            levelsInfo.add(i, new Tuple<>(buf.readInt(), buf.readInt()));
        }

        int woSize = buf.readInt();
        workOrders.clear();
        for (int i = 0; i < woSize; i++)
        {
            final IWorkOrderView woView = AbstractWorkOrder.createWorkOrderView(buf);
            if (woView instanceof WorkOrderMinerView)
            {
                workOrders.add((WorkOrderMinerView) woView);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Object /* BOWindow: todo ModularUI2 */ getWindow()
    {
        return new WindowHutMinerModule(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/info.png");
    }

    @Override
    public String getDesc()
    {
        return String.translatable("com.minecolonies.coremod.gui.miner.levels");
    }

    @Override
    public boolean isPageVisible()
    {
        for (final WorkerBuildingModuleView workerBuildingModuleView : buildingView.getModuleViews(WorkerBuildingModuleView.class))
        {
            if (workerBuildingModuleView.getJobEntry() == ModJobs.quarrier.get() && !workerBuildingModuleView.getAssignedCitizens().isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if there is a workorder for this node already.
     *
     * @param row the row of the World.
     * @return true if so.
     */
    public boolean doesWorkOrderExist(final int row)
    {
        final int depth = levelsInfo.get(row).getB();
        for (final IWorkOrderView wo : workOrders)
        {
            if (wo.getDisplayName().getString().contains("main") && wo.getLocation().getY() == depth)
            {
                return true;
            }
        }
        return false;
    }
}



