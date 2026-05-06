package com.minecolonies.core.items;
import net.minecraft.world.item.InteractionResult;
import net.minecraft.world.item.Properties;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.IGuardBuilding;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.core.colony.buildings.AbstractBuildingGuards;
import com.minecolonies.core.colony.buildings.modules.settings.GuardTaskSetting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.NBTTagCompound;
// [1.7.10] InteractionResult -> boolean
// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_ID;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_POS;
import static com.minecolonies.api.util.constant.translation.ToolTranslationConstants.*;

/**
 * Guard Scepter Item class. Used to give tasks to guards.
 */
public class ItemScepterGuard extends AbstractItemMinecolonies
{
    /**
     * The compound NBTBase for the last pos the tool has been clicked.
     */
    private static final String TAG_LAST_POS = "lastPos";

    /**
     * GuardScepter constructor. Sets max stack to 1, like other tools.
     *
     * @param properties the properties.
     */
    public ItemScepterGuard(final Properties properties)
    {
        super("scepterguard", properties.stacksTo(1).durability(2));
    }

    @NotNull
    @Override
    public InteractionResult useOn(final UseOnContext ctx)
    {
        // if server world, do nothing
        if (ctx.getLevel().isClientSide)
        {
            return InteractionResult.FAIL;
        }

        final ItemStack scepter = ctx.getPlayer().getItemInHand(ctx.getHand());
        if (!scepter.hasTag())
        {
            scepter.setTag(new NBTTagCompound());
        }
        final NBTTagCompound compound = scepter.getTag();

        if (compound.hasKey(TAG_LAST_POS))
        {
            final int[] lastPos = BlockPosUtil.read(compound, TAG_LAST_POS);
            if (lastPos.equals(ctx.getClickedPos()))
            {
                ctx.getPlayer().getInventory().removeItemNoUpdate(ctx.getPlayer().getInventory().selected);
                MessageUtils.format(TOOL_GUARD_SCEPTER_ADD_PATROL_TARGETS_FINISHED).sendTo(ctx.getPlayer());
                return InteractionResult.FAIL;
            }
        }
        return handleItemUsage(ctx.getLevel(), ctx.getClickedPos(), compound, ctx.getPlayer(), scepter);
    }

    /**
     * Handles the usage of the item.
     *
     * @param worldIn  the world it is used in.
     * @param pos      the position.
     * @param compound the compound.
     * @param playerIn the player using it.
     * @param stack the used item.
     * @return if it has been successful.
     */
    @NotNull
    private static InteractionResult handleItemUsage(final World worldIn, final int[] pos, final NBTTagCompound compound, final Player playerIn, final ItemStack stack)
    {
        if (!compound.hasKey(TAG_ID))
        {
            return InteractionResult.FAIL;
        }
        final IColony colony = IColonyManager.getInstance().getColonyByWorld(compound.getInt(TAG_ID), worldIn);
        if (colony == null)
        {
            return InteractionResult.FAIL;
        }

        final int[] guardTower = BlockPosUtil.read(compound, TAG_POS);
        final IBuilding hut = colony.getServerBuildingManager().getBuilding(guardTower);
        if (!(hut instanceof AbstractBuildingGuards))
        {
            return InteractionResult.FAIL;
        }
        final IGuardBuilding tower = (IGuardBuilding) hut;

        if (BlockPosUtil.getDistance2D(pos, guardTower) > tower.getPatrolDistance())
        {
            MessageUtils.format(TOOL_GUARD_SCEPTER_TOWER_TOO_FAR).sendTo(playerIn);
            return InteractionResult.FAIL;
        }

        if (hut.getSetting(AbstractBuildingGuards.GUARD_TASK).getValue().equals(GuardTaskSetting.GUARD))
        {
            MessageUtils.format(TOOL_GUARD_SCEPTER_ADD_GUARD_TARGET, pos.toShortString()).sendTo(playerIn);
            tower.setGuardPos(pos);
            playerIn.getInventory().removeItem(stack);
        }
        else
        {
            if (!compound.hasKey(TAG_LAST_POS))
            {
                tower.resetPatrolTargets();
            }
            tower.addPatrolTarget(pos);
            MessageUtils.format(TOOL_GUARD_SCEPTER_ADD_PATROL_TARGET, pos.toShortString()).sendTo(playerIn);
        }
        BlockPosUtil.write(compound, TAG_LAST_POS, pos);

        return InteractionResult.SUCCESS;
    }
}




