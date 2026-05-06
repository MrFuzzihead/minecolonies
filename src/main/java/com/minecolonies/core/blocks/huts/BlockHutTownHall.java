package com.minecolonies.core.blocks.huts;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.InteractionResult;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.MineColonies;
import com.minecolonies.core.Network;
import com.minecolonies.core.network.messages.server.GetColonyInfoMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.TranslationConstants.TOWNHALL_BREAKING_DONE_MESSAGE;
import static com.minecolonies.api.util.constant.TranslationConstants.WARNING_DUPLICATE_TOWN_HALL;

/**
 * Hut for the town hall.
 * [1.7.10] Ported: onBlockActivated replaces use(); getPlayerRelativeBlockHardness replaces getDestroyProgress;
 * canPlaceAt uses EntityPlayer; removed BlockState/InteractionResult/ServerLevel/ClientLevel.
 * getRequirements() commented out (ClientLevel/LocalPlayer don't exist in 1.7.10).
 */
public class BlockHutTownHall extends AbstractBlockHut<BlockHutTownHall>
{
    /** Progress in % of breaking the townHall. */
    private int breakProgressOnTownHall = 0;

    /** Ticks at which townhall breaking started. */
    private long lastTownHallBreakingTick = 0;

    /** Detect if the town-hall break was valid. */
    private boolean validTownHallBreak = false;

    /** Interaction timeout for GetColonyInfoMessage. */
    public static long timeout = 0;

    @Override
    @NotNull
    public String getHutName()
    {
        return "blockhuttownhall";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.townHall.get();
    }

    @Override
    public float getPlayerRelativeBlockHardness(
      final EntityPlayer player,
      final World world,
      final int x,
      final int y,
      final int z)
    {
        if (MineColonies.getConfig().getServer().pvp_mode.get() && !world.isRemote)
        {
            final IBuilding building = IColonyManager.getInstance().getBuilding(world, x, y, z);
            if (building != null && building.getColony().isCoordInColony(world, x, y, z)
                  && building.getColony().getPermissions().getRank(player).isHostile())
            {
                final double localProgress = breakProgressOnTownHall;
                final double hardness = getBlockHardness() * 20.0 * 1.5;

                if (localProgress >= hardness / 10.0 * 9.0 && localProgress <= hardness / 10.0 * 9.0 + 1)
                {
                    MessageUtils.format(TOWNHALL_BREAKING_DONE_MESSAGE, player.getDisplayName(), 90).sendTo(building.getColony()).forAllPlayers();
                }
                if (localProgress >= hardness / 4.0 * 3.0 && localProgress <= hardness / 4.0 * 3.0 + 1)
                {
                    MessageUtils.format(TOWNHALL_BREAKING_DONE_MESSAGE, player.getDisplayName(), 75).sendTo(building.getColony()).forAllPlayers();
                }
                else if (localProgress >= hardness / 2.0 && localProgress <= hardness / 2.0 + 1)
                {
                    MessageUtils.format(TOWNHALL_BREAKING_DONE_MESSAGE, player.getDisplayName(), 50).sendTo(building.getColony()).forAllPlayers();
                }
                else if (localProgress >= hardness / 4.0 && localProgress <= hardness / 4.0 + 1)
                {
                    MessageUtils.format(TOWNHALL_BREAKING_DONE_MESSAGE, player.getDisplayName(), 25).sendTo(building.getColony()).forAllPlayers();
                }

                if (localProgress >= hardness - 1)
                {
                    validTownHallBreak = true;
                }

                if (world.getTotalWorldTime() - lastTownHallBreakingTick < 10)
                {
                    breakProgressOnTownHall++;
                }
                else
                {
                    MessageUtils.format(TOWNHALL_BREAKING_DONE_MESSAGE, player.getDisplayName(), 100).sendTo(building.getColony()).forAllPlayers();
                    breakProgressOnTownHall = 0;
                    validTownHallBreak = false;
                }
                lastTownHallBreakingTick = world.getTotalWorldTime();
            }
            else
            {
                validTownHallBreak = true;
            }
        }
        else if (!MineColonies.getConfig().getServer().pvp_mode.get())
        {
            validTownHallBreak = true;
        }

        final float def = super.getPlayerRelativeBlockHardness(player, world, x, y, z);
        return MineColonies.getConfig().getServer().pvp_mode.get() ? def / 12 : def;
    }

    /**
     * Getter for whether the block is eligible for destruction.
     * @return true if the block can be broken.
     */
    public boolean getValidBreak()
    {
        return validTownHallBreak;
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
                building.openGui(player.isSneaking());
            }
            else if (System.currentTimeMillis() > timeout)
            {
                Network.getNetwork().sendToServer(new GetColonyInfoMessage(new int[]{x, y, z}));
                timeout = System.currentTimeMillis() + 1000;
            }
        }
        return true;
    }

    @Override
    public boolean canPlaceAt(final int[] pos, final EntityPlayer player)
    {
        final IColony colony = IColonyManager.getInstance().getIColony(player.worldObj, pos[0], pos[1], pos[2]);

        if (colony == null)
        {
            return true;
        }

        if (colony.getCommonBuildingManager().hasTownHall())
        {
            final IBuilding townHall = colony.getServerBuildingManager().getTownHall();
            if (!colony.getWorld().isRemote && townHall != null)
            {
                MessageUtils.format(WARNING_DUPLICATE_TOWN_HALL,
                    townHall.getPosition()[0] + "," + townHall.getPosition()[1] + "," + townHall.getPosition()[2]).sendTo(player);
            }
            return false;
        }
        return true;
    }
}
