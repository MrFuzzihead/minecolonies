package com.minecolonies.core.blocks.huts;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.InteractionResult;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.core.client.gui.modules.building.ConnectionModuleWindow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Block of the gate house hut.
 * [1.7.10] Ported: onBlockActivated replaces use(); removed BlockState/InteractionResult/InteractionHand.
 */
public class BlockHutGateHouse extends AbstractBlockHut<BlockHutGateHouse>
{
    public BlockHutGateHouse()
    {
        super();
    }

    @NotNull
    @Override
    public String getHutName()
    {
        return "blockhutgatehouse";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.gateHouse.get();
    }

    @Override
    public boolean canRightClickWithoutPermissions()
    {
        return true;
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
            if (building != null && !building.getColony().getPermissions().hasPermission(player, Action.ACCESS_HUTS))
            {
                new ConnectionModuleWindow(building, true).open();
                return false;
            }

            return super.onBlockActivated(worldIn, x, y, z, player, side, hitX, hitY, hitZ);
        }
        else
        {
            final IColony colony = IColonyManager.getInstance().getIColony(worldIn, x, y, z);
            if (colony != null && !colony.getPermissions().hasPermission(player, Action.ACCESS_HUTS))
            {
                return false;
            }
        }
        return true;
    }
}
