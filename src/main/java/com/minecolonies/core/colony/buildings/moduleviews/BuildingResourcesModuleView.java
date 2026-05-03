package com.minecolonies.core.colony.buildings.moduleviews;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.modules.building.WindowBuilderResModule;
import com.minecolonies.core.colony.buildings.utils.BuildingBuilderResource;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BuildingResourcesModuleView extends AbstractBuildingModuleView
{
    /**
     * The resources they have to keep.
     */
    private final HashMap<String, BuildingBuilderResource> resources = new HashMap<>();

    /**
     * The building they are working on.
     */
    private int workOrderId;

    /**
     * Building progress.
     */
    private double progress;

    /**
     * Information on the phases.
     */
    private int finishedStages = 0;
    private int totalStages    = 1;

    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {
        final int size = buf.readInt();
        resources.clear();

        for (int i = 0; i < size; i++)
        {
            final ItemStack itemStack = buf.readItem();
            final int amountAvailable = buf.readInt();
            final int amountNeeded = buf.readInt();
            final BuildingBuilderResource resource = new BuildingBuilderResource(itemStack, amountNeeded, amountAvailable);
            final int hashCode = itemStack.hasTag() ? itemStack.getTag().hashCode() : 0;
            final String key = itemStack.getDescriptionId() + "-" + hashCode;
            resources.put(key, resource);
        }

        workOrderId = buf.readInt();
        progress = buf.readDouble();
        totalStages = buf.readInt();
        finishedStages = buf.readInt();
    }

    /**
     * Get the work order id.
     *
     * @return a string describing it.
     */
    public int getWorkOrderId()
    {
        return workOrderId;
    }

    /**
     * Getter for the needed resources.
     *
     * @return a copy of the HashMap(String, Object).
     */

    public Map<String, BuildingBuilderResource> getResources()
    {
        return Collections.unmodifiableMap(resources);
    }

    /**
     * Get the building progress (relative to items used)
     *
     * @return the progress.
     */
    public int getProgress()
    {
        int localProgress = Math.max(100 - (int) (progress * 100), 0);
        if (finishedStages == 0)
        {
            if (totalStages == finishedStages)
            {
                return 0;
            }
        }
        return localProgress;
    }

    /**
     * Get the current stage status.
     * @return the stage.
     */
    public int getCurrentStage()
    {
        return finishedStages;
    }

    /**
     * Get the total number of stages,
     * @return all stages.
     */
    public int getTotalStages()
    {
        return totalStages;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Object /* BOWindow: todo ModularUI2 */ getWindow()
    {
        return new WindowBuilderResModule(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/inventory.png");
    }

    @Override
    public String getDesc()
    {
        return String.translatable("com.minecolonies.coremod.gui.workerhuts.resourcelist");
    }
}



