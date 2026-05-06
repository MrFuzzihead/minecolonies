package com.minecolonies.api.colony.buildings;
import net.minecraft.block.state.BlockState;

import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] EnumFacing -> net.minecraft.util.EnumFacing
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.block.Block;
// [1.7.10] BlockState -> int metadata
// [1.7.10] capabilities removed
// [1.7.10] capabilities removed
// [1.7.10] LazyOptional removed
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public interface IBuildingContainer extends ISchematicProvider
{
    void deserializeNBT(NBTTagCompound compound);

    NBTTagCompound serializeNBT();

    /**
     * Get the pick up priority of the building.
     *
     * @return the priority, an integer.
     */
    int getPickUpPriority();

    /**
     * Increase or decrease the current pickup priority.
     *
     * @param value the new prio to add to.
     */
    void alterPickUpPriority(int value);

    /**
     * Add a new container to the building.
     *
     * @param pos position to add.
     */
    void addContainerPosition(@NotNull int[] pos);

    /**
     * Remove a container from the building.
     *
     * @param pos position to remove.
     */
    void removeContainerPosition(int[] pos);

    /**
     * Get all containers which belong to the building (including hutblock).
     *
     * @return a copy of the list to avoid currentModification exception.
     */
    List<int[]> getContainers();

    /**
     * Register a blockState and position. We suppress this warning since this parameter will be used in child classes which override this method.
     *
     * @param blockState to be registered
     * @param pos        of the blockState
     * @param world      world to register it at.
     */
    void registerBlockPosition(@NotNull BlockState blockState, @NotNull int[] pos, @NotNull World world);

    /**
     * Register a block and position. We suppress this warning since this parameter will be used in child classes which override this method.
     *
     * @param block to be registered
     * @param pos   of the block
     * @param world world to register it at.
     */
    @SuppressWarnings("squid:S1172")
    void registerBlockPosition(@NotNull Block block, @NotNull int[] pos, @NotNull World world);

    /**
     * Returns the tile entity that belongs to the colony building.
     *
     * @return {@link AbstractTileEntityColonyBuilding} object of the building.
     */
    AbstractTileEntityColonyBuilding getTileEntity();

    /**
     * Sets the tile entity for the building.
     *
     * @param te The tileentity
     */
    void setTileEntity(AbstractTileEntityColonyBuilding te);

    // [1.7.10] getCapability not supported - no Forge capability system in 1.7.10
}





