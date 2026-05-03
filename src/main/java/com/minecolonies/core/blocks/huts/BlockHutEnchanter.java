package com.minecolonies.core.blocks.huts;

import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.core.tileentities.TileEntityEnchanter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Hut for the enchanter.
 * [1.7.10] Ported: createTileEntity returns TileEntityEnchanter; removed BlockEntity/BlockState params.
 */
public class BlockHutEnchanter extends AbstractBlockHut<BlockHutEnchanter>
{
    @NotNull
    @Override
    public String getHutName()
    {
        return "blockhutenchanter";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.enchanter.get();
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(final World world, final int metadata)
    {
        final TileEntityEnchanter building = new TileEntityEnchanter();
        building.registryName = this.getBuildingEntry().getRegistryName();
        return building;
    }
}
