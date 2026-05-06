package com.minecolonies.core.colony.buildings.workerbuildings;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.workerbuildings.IWareHouse;
import com.minecolonies.api.colony.requestsystem.resolver.IRequestResolver;
import com.minecolonies.api.tileentities.*;
import com.minecolonies.api.util.constant.BuildingConstants;
import com.minecolonies.api.util.constant.TypeConstants;
import com.minecolonies.core.blocks.BlockMinecoloniesRack;
import com.minecolonies.core.client.gui.WindowHutMinPlaceholder;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.CourierAssignmentModule;
import com.minecolonies.core.colony.buildings.modules.WarehouseModule;
import com.minecolonies.core.colony.buildings.views.AbstractBuildingView;
import com.minecolonies.core.colony.requestsystem.resolvers.DeliveryRequestResolver;
import com.minecolonies.core.colony.requestsystem.resolvers.PickupRequestResolver;
import com.minecolonies.core.colony.requestsystem.resolvers.WarehouseConcreteRequestResolver;
import com.minecolonies.core.colony.requestsystem.resolvers.WarehouseRequestResolver;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import com.minecolonies.core.tileentities.TileEntityRack;
import com.minecolonies.core.tileentities.TileEntityWareHouse;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.World;
import net.minecraft.block.Block;
// [1.7.10] block.entity removed
import org.jetbrains.annotations.NotNull;

/**
 * Class of the warehouse building.
 */
public class BuildingWareHouse extends AbstractBuilding implements IWareHouse
{
    /**
     * String describing the Warehouse.
     */
    private static final String WAREHOUSE = "warehouse";

    /**
     * Max World of the building.
     */
    private static final int MAX_LEVEL = 5;

    /**
     * Max storage upgrades.
     */
    public static final int MAX_STORAGE_UPGRADE = 3;

    /**
     * Instantiates a new warehouse building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingWareHouse(final IColony c, final int[] l)
    {
        super(c, l);
    }

    @Override
    public void requestRepair(final int[] builder)
    {
        //To ensure that the racks are all set to in the warehouse when repaired.
        for (final int[] pos : containerList)
        {
            if (getColony().getWorld() != null)
            {
                final BlockEntity entity = getColony().getWorld().getBlockEntity(pos);
                if (entity instanceof TileEntityRack)
                {
                    ((AbstractTileEntityRack) entity).setInWarehouse(true);
                }
            }
        }

        super.requestRepair(builder);
    }

    @Override
    public boolean canAccessWareHouse(final ICitizenData citizenData)
    {
        return getFirstModuleOccurance(CourierAssignmentModule.class).hasAssignedCitizen(citizenData);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return WAREHOUSE;
    }

    /**
     * Returns the tile entity that belongs to the colony building.
     *
     * @return {@link TileEntityColonyBuilding} object of the building.
     */
    @Override
    public AbstractTileEntityWareHouse getTileEntity()
    {
        final AbstractTileEntityColonyBuilding entity = super.getTileEntity();
        return !(entity instanceof TileEntityWareHouse) ? null : (AbstractTileEntityWareHouse) entity;
    }

    @Override
    public boolean hasContainerPosition(final int[] inDimensionLocation)
    {
        return containerList.contains(inDimensionLocation) || getLocation().getInDimensionLocation().equals(inDimensionLocation);
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return MAX_LEVEL;
    }

    @Override
    public void registerBlockPosition(@NotNull final Block block, @NotNull final int[] pos, @NotNull final World world)
    {
        if (block instanceof BlockMinecoloniesRack)
        {
            final BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof TileEntityRack)
            {
                ((AbstractTileEntityRack) entity).setInWarehouse(true);
                while (((TileEntityRack) entity).getUpgradeSize() < getFirstModuleOccurance(WarehouseModule.class).getStorageUpgrade())
                {
                    ((TileEntityRack) entity).upgradeRackSize();
                }
            }
        }
        super.registerBlockPosition(block, pos, world);
    }

    @Override
    public ImmutableCollection<IRequestResolver<?>> createResolvers()
    {
        final ImmutableCollection<IRequestResolver<?>> supers = super.createResolvers();
        final ImmutableList.Builder<IRequestResolver<?>> builder = ImmutableList.builder();

        builder.addAll(supers);
        builder.add(new WarehouseRequestResolver(getRequester().getLocation(),
          getColony().getRequestManager().getFactoryController().getNewInstance(TypeConstants.ITOKEN)),
          new WarehouseConcreteRequestResolver(getRequester().getLocation(),
          getColony().getRequestManager().getFactoryController().getNewInstance(TypeConstants.ITOKEN))
          );

        builder.add(new DeliveryRequestResolver(getRequester().getLocation(),
          getColony().getRequestManager().getFactoryController().getNewInstance(TypeConstants.ITOKEN)));
        builder.add(new PickupRequestResolver(getRequester().getLocation(),
          getColony().getRequestManager().getFactoryController().getNewInstance(TypeConstants.ITOKEN)));

        return builder.build();
    }

    /**
     * Upgrade all containers by 9 slots.
     *
     * @param world the world object.
     */
    @Override
    public void upgradeContainers(final World world)
    {
        if (getFirstModuleOccurance(WarehouseModule.class).getStorageUpgrade() < MAX_STORAGE_UPGRADE)
        {
            for (final int[] pos : getContainers())
            {
                final BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof TileEntityRack && !(entity instanceof TileEntityColonyBuilding))
                {
                    ((AbstractTileEntityRack) entity).upgradeRackSize();
                }
            }
            getFirstModuleOccurance(WarehouseModule.class).incrementStorageUpgrade();
        }
        markDirty();
    }

    @Override
    public boolean canBeGathered()
    {
        return false;
    }

    /**
     * Checks if the building can sort its inventory.
     *
     * @return true if the building can sort, false otherwise.
     */
    @Override
    public boolean canSort()
    {
        return getBuildingLevel() >= BuildingConstants.DEFAULT_REQUIRED_SORT_LEVEL;
    }
    
    /**
     * BuildWarehouse View.
     */
    public static class View extends AbstractBuildingView
    {
        /**
         * Instantiate the warehouse view.
         *
         * @param c the colonyview to put it in
         * @param l the positon
         */
        public View(final IColonyView c, final int[] l)
        {
            super(c, l);
        }

        @NotNull
        @Override
        public Object /* BOWindow: todo ModularUI2 */ getWindow()
        {
            return new WindowHutMinPlaceholder<>(this);
        }
    }
}




