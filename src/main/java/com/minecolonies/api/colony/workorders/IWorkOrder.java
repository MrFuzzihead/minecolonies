package com.minecolonies.api.colony.workorders;

import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.entity.ai.workers.util.BuildingProgressStage;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.util.AxisAlignedBB;
// [1.7.10] world.phys removed
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IWorkOrder
{
    /**
     * Get the ID of the work order.
     *
     * @return ID of the work order
     */
    int getID();

    /**
     * Set the ID of the work order.
     *
     * @param id the new ID for the work order
     */
    void setID(int id);

    /**
     * Getter for the priority.
     *
     * @return the priority of the work order.
     */
    int getPriority();

    /**
     * Setter for the priority.
     *
     * @param priority the new priority.
     */
    void setPriority(int priority);

    /**
     * Get the structure this work order should be using, if any.
     *
     * @return the schematic path.
     */
    String getStructurePath();

    /**
     * Get the structure this work order should be using, if any.
     *
     * @return the pack name.
     */
    String getStructurePack();

    /**
     * Loads the blueprint if necessary
     * @param world world to use
     * @param afterLoad consumes the loaded blueprint or null
     */
    void loadBlueprint(final World world, final Consumer<Blueprint> afterLoad);

    /**
     * Get the current World of the structure of the work order.
     *
     * @return the current World.
     */
    int getCurrentLevel();

    /**
     * Get the target World of the structure of the work order.
     *
     * @return the target World.
     */
    int getTargetLevel();

    /**
     * The name of the work order.
     *
     * @return the work order name.
     */
    String getTranslationKey();

    /**
     * The type of the work order.
     *
     * @return the work order type.
     */
    WorkOrderType getWorkOrderType();

    /**
     * Get the current location of the building
     *
     * @return the location
     */
    int[] getLocation();

    /**
     * Get the current rotation of the building
     *
     * @return the location
     */
    int getRotation();

    /**
     * Whether the current building is mirrored
     *
     * @return the location
     */
    boolean isMirrored();

    /**
     * Is the Work Order claimed?
     *
     * @return true if the Work Order has been claimed
     */
    boolean isClaimed();

    /**
     * Get the position of the Citizen that the Work Order is claimed by.
     *
     * @return ID of citizen the Work Order has been claimed by, new int[]{0,0,0}
     */
    @NotNull
    int[] getClaimedBy();

    /**
     * Set the Work order as claimed by a given building.
     *
     * @param builder the building position.
     */
    void setClaimedBy(int[] builder);

    /**
     * Get the name of the work order, provides the custom name or the work order name when no custom name is given
     *
     * @return the display name for the work order
     */
    String getDisplayName();

    /**
     * Get the file name of the structure.
     * Calculates the file name from the path.
     * @return the name without the appendix.
     */
    default String getFileName()
    {
        final String[] split = getStructurePath().contains("\\") ? getStructurePath().split("\\\\") : getStructurePath().split("/");
        return split[split.length - 1].replace(".blueprint", "");
    }

    /**
     * Store a blueprint reference
     *
     * @param blueprint
     */
    void setBlueprint(Blueprint blueprint, final World world);

    /**
     * Get the stored blueprint
     *
     * @return
     */
    @Nullable
    public Blueprint getBlueprint();

    /**
     * Clears the stored blueprint
     */
    public void clearBlueprint();

    /**
     * Get the blueprints Boundingbox
     */
    @Nullable
    public AxisAlignedBB getBoundingBox();

    /**
     * Get the related colony or view
     *
     * @return
     */
    public IColony getColony();

    /**
     * Set the related colony or view
     *
     * @return
     */
    public void setColony(IColony colony);

    /**
     * The buildings stage
     *
     * @return stage index
     */
    BuildingProgressStage getStage();
}





