package com.minecolonies.core.colony.buildings;
import net.minecraft.util.Direction;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.ldtteam.structurize.storage.StructurePacks;
import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.IBuildingContainer;
import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import com.minecolonies.core.tileentities.TileEntityRack;
import com.minecolonies.core.blocks.BlockMinecoloniesRack;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] EnumFacing -> net.minecraft.util.EnumFacing
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
// [1.7.10] NbtUtils removed
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.MathHelper;
import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
// [1.7.10] capabilities removed
// [1.7.10] capabilities removed
// [1.7.10] Object /* LazyOptional */ removed
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

import static com.minecolonies.api.colony.requestsystem.requestable.deliveryman.AbstractDeliverymanRequestable.getMaxBuildingPriority;
import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Class containing the container action of the buildings.
 */
public abstract class AbstractBuildingContainer extends AbstractSchematicProvider implements IBuildingContainer
{
    /**
     * A list which contains the position of all containers which belong to the worker building.
     */
    protected final Set<int[]> containerList = new HashSet<>();

    /**
     * List of items the worker should keep. With the quantity and if he should keep it in the inventory as well.
     */
    protected final Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> keepX = new HashMap<>();

    /**
     * The tileEntity of the building.
     */
    protected AbstractTileEntityColonyBuilding tileEntity;

    /**
     * Priority of the building in the pickUpList. This is the unscaled value (mainly for a more intuitive GUI).
     */
    private int unscaledPickUpPriority = 5;

    /**
     * The constructor for the building container.
     *
     * @param pos    the position of it.
     * @param colony the colony.
     */
    public AbstractBuildingContainer(final int[] pos, final IColony colony)
    {
        super(pos, colony);
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);

        final NBTTagList containerTagList = compound.getTagList(TAG_CONTAINERS, 10); // 10 = TAG_Compound
        for (int i = 0; i < containerTagList.tagCount(); ++i)
        {
            final NBTTagCompound containerCompound = containerTagList.getCompoundTagAt(i);
            containerList.add(com.minecolonies.api.util.BlockPosUtil.read(containerCompound, "pos"));
        }
        if (compound.hasKey(TAG_PRIO))
        {
            this.unscaledPickUpPriority = compound.getInteger(TAG_PRIO);
        }
        if (compound.hasKey(TAG_PRIO_STATE))
        {
            // This was the old int representation of Pickup:Never
            if (compound.getInteger(TAG_PRIO_STATE) == 0)
            {
                this.unscaledPickUpPriority = 0;
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = super.serializeNBT();

        @NotNull final NBTTagList containerTagList = new NBTTagList();
        for (@NotNull final int[] pos : containerList)
        {
            final NBTTagCompound posCompound = new NBTTagCompound();
            com.minecolonies.api.util.BlockPosUtil.write(posCompound, "pos", pos);
            containerTagList.appendTag(posCompound);
        }
        compound.setTag(TAG_CONTAINERS, containerTagList);
        compound.setInteger(TAG_PRIO, this.unscaledPickUpPriority);

        return compound;
    }

    @Override
    public int getPickUpPriority()
    {
        return this.unscaledPickUpPriority;
    }

    @Override
    public void alterPickUpPriority(final int value)
    {
        this.unscaledPickUpPriority = net.minecraft.util.MathHelper.clamp_int(this.unscaledPickUpPriority + value, 0, getMaxBuildingPriority(false));
    }

    @Override
    public void addContainerPosition(@NotNull final int[] pos)
    {
        containerList.add(pos);
    }

    @Override
    public void removeContainerPosition(final int[] pos)
    {
        containerList.remove(pos);
    }

    @Override
    public List<int[]> getContainers()
    {
        final List<int[]> list = new ArrayList<>(containerList);;
        list.add(this.getPosition());
        return list;
    }

    @Override
    // [1.7.10] BlockState is our custom wrapper class with .block field
    public void registerBlockPosition(@NotNull final net.minecraft.block.state.BlockState blockState, @NotNull final int[] pos, @NotNull final World world)
    {
        registerBlockPosition(blockState.block, pos, world);
    }

    @Override
    @SuppressWarnings("squid:S1172")
    public void registerBlockPosition(@NotNull final Block block, @NotNull final int[] pos, @NotNull final World world)
    {
        if (block instanceof AbstractBlockHut)
        {
            final net.minecraft.tileentity.TileEntity entity = world.getTileEntity(pos[0], pos[1], pos[2]);
            if (entity instanceof TileEntityColonyBuilding buildingEntity)
            {
                // [1.7.10] StructurePacks not available; skip setStructurePack
                buildingEntity.setMirror(isMirrored());
                final IBuilding building = colony.getServerBuildingManager().getBuilding(pos);
                if (building != null)
                {
                    building.setStructurePack(getStructurePack());
                    building.setParent(getID());
                }
            }
        }
        else if (block instanceof BlockMinecoloniesRack)
        {
            addContainerPosition(pos);
            final net.minecraft.tileentity.TileEntity entity = world.getTileEntity(pos[0], pos[1], pos[2]);
            if (entity instanceof TileEntityRack rackEntity)
            {
                rackEntity.setBuildingPos(getID()[0], getID()[1], getID()[2]);
            }
        }
    }

    /**
     * Gets the list of tags, and finds the first location registered there.
     * @param tagName the name of the NBTBase to query
     * @return the int[], or null if not found
     */
    @Nullable
    protected int[] getFirstLocationFromTag(@NotNull final String tagName)
    {
        final List<int[]> locations = getLocationsFromTag(tagName);
        return locations.isEmpty() ? null : locations.get(0);
    }

    @Override
    public List<int[]> getLocationsFromTag(@NotNull final String tagName)
    {
        // [1.7.10] tag position map uses long[] keys; return empty list as stub
        return Collections.emptyList();
    }

    @Override
    public void setTileEntity(final AbstractTileEntityColonyBuilding te)
    {
        tileEntity = te;
        if (te != null && te.isOutdated())
        {
            safeUpdateTEDataFromSchematic();
        }
    }

    //------------------------- !Start! Capabilities handling for minecolonies buildings -------------------------//
    // [1.7.10] getCapability not supported - no Forge capability system in 1.7.10
    //------------------------- !End! Capabilities handling for minecolonies buildings -------------------------//
}






