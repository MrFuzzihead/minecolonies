package com.minecolonies.core.blocks.huts;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity

import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.tileentities.MinecoloniesTileEntities;
import com.minecolonies.core.tileentities.TileEntityWareHouse;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Hut for the warehouse.
 * [1.7.10] Ported: createTileEntity returns TileEntityWareHouse; removed BlockEntity/BlockState params.
 */
public class BlockHutWareHouse extends AbstractBlockHut<BlockHutWareHouse>
{
    public BlockHutWareHouse()
    {
        super();
    }

    @NotNull
    @Override
    public String getHutName()
    {
        return "blockhutwarehouse";
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(final World world, final int metadata)
    {
        final TileEntityWareHouse building = (TileEntityWareHouse) MinecoloniesTileEntities.WAREHOUSE.get().create();
        building.registryName = this.getBuildingEntry().getRegistryName();
        return building;
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.wareHouse.get();
    }
}
