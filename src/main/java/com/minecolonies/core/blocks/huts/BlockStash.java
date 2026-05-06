package com.minecolonies.core.blocks.huts;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.InteractionResult;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.blocks.AbstractColonyBlock;
import com.minecolonies.api.blocks.interfaces.IRSComponentBlock;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.tileentities.MinecoloniesTileEntities;
import com.minecolonies.core.Network;
import com.minecolonies.core.network.messages.server.colony.OpenInventoryMessage;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Hut for the Stash.
 * [1.7.10] Ported: createTileEntity replaces newBlockEntity; setBlockBoundsBasedOnState replaces VoxelShape;
 * onBlockActivated replaces use(); removed BlockState/BlockEntity/InteractionResult.
 */
public class BlockStash extends AbstractColonyBlock<BlockStash> implements IRSComponentBlock
{
    @NotNull
    @Override
    public String getHutName()
    {
        return "blockstash";
    }

    @Override
    public @Nullable TileEntity createTileEntity(final World world, final int metadata)
    {
        final TileEntityColonyBuilding building = (TileEntityColonyBuilding) MinecoloniesTileEntities.STASH.get().create();
        building.registryName = this.getBuildingEntry().getRegistryName();
        return building;
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.stash.get();
    }

    @Override
    public float getPlayerRelativeBlockHardness(final EntityPlayer player, final World world, final int x, final int y, final int z)
    {
        return 1f / 30f;
    }

    @Override
    public void setBlockBoundsBasedOnState(final IBlockAccess access, final int x, final int y, final int z)
    {
        final int meta = access.getBlockMetadata(x, y, z);
        // metadata 0=SOUTH, 1=WEST, 2=NORTH, 3=EAST
        switch (meta & 0x3)
        {
            case 2: // NORTH
                setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f);
                break;
            case 0: // SOUTH
                setBlockBounds(0.0f, 0.0f, 0.5f, 1.0f, 1.0f, 1.0f);
                break;
            case 3: // EAST
                setBlockBounds(0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                break;
            case 1: // WEST
            default:
                setBlockBounds(0.0f, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f);
                break;
        }
    }

    @NotNull
    @Override
    public boolean onBlockActivated(
      final World worldIn,
      final int x,
      final int y,
      final int z,
      final EntityPlayer player,
      final int side,
      final float hitX,
      final float hitY,
      final float hitZ)
    {
        if (worldIn.isRemote)
        {
            @Nullable final IBuildingView building = IColonyManager.getInstance().getBuildingView(worldIn, x, y, z);

            if (building != null
                  && building.getColony() != null
                  && building.getColony().getPermissions().hasPermission(player, Action.ACCESS_HUTS))
            {
                Network.getNetwork().sendToServer(new OpenInventoryMessage(building));
            }
        }
        return true;
    }
}
