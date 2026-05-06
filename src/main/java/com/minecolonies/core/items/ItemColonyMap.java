package com.minecolonies.core.items;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.constant.TranslationConstants;
import com.minecolonies.core.client.gui.map.WindowColonyMap;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
// [1.7.10] int /* InteractionHand */ removed
// [1.7.10] InteractionResult -> boolean
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Properties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.InteractionResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.World;
// [1.7.10] block.entity removed
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.Constants.STACKSIZE;
import static com.minecolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_MAP_COLONY_SET;

/**
 * Class describing the map item.
 */
public class ItemColonyMap extends AbstractItemMinecolonies
{
    /**
     * NBTBase of the colony.
     */
    public static final String TAG_COLONY = "colony";

    /**
     * Sets the name, creative tab, and registers the map item.
     *
     * @param properties the properties.
     */
    public ItemColonyMap(final Properties properties)
    {
        super("colonymap", properties.stacksTo(STACKSIZE));
    }

    @Override
    @NotNull
    public InteractionResult useOn(final UseOnContext ctx)
    {
        final ItemStack map = ctx.getPlayer().getItemInHand(ctx.getHand());

        final NBTTagCompound compound = checkForCompound(map);
        final BlockEntity entity = ctx.getLevel().getBlockEntity(ctx.getClickedPos());

        if (entity instanceof TileEntityColonyBuilding buildingEntity)
        {
            compound.setInteger(TAG_COLONY, buildingEntity.getColonyId());
            if (!ctx.getLevel().isClientSide)
            {
                MessageUtils.format(COM_MINECOLONIES_MAP_COLONY_SET, buildingEntity.getColony().getName()).sendTo(ctx.getPlayer());
            }
        }
        else if (ctx.getLevel().isClientSide)
        {
            openWindow(compound, ctx.getLevel(), ctx.getPlayer());
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Handles mid air use.
     *
     * @param worldIn  the world
     * @param playerIn the player
     * @param hand     the hand
     * @return the result
     */
    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(
            final World worldIn,
            final Player playerIn,
            final int /* InteractionHand */ hand)
    {
        final ItemStack map = playerIn.getItemInHand(hand);

        if (!worldIn.isClientSide) {
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, map);
        }

        openWindow(checkForCompound(map), worldIn, playerIn);

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, map);
    }

    /**
     * Check for the compound and return it. If not available create and return it.
     *
     * @param map the map to check for.
     * @return the compound of the map.
     */
    private static NBTTagCompound checkForCompound(final ItemStack map)
    {
        if (!map.hasTag()) map.setTag(new NBTTagCompound());
        return map.getTag();
    }

    /**
     * Opens the map window if there is a valid colony linked
     * @param compound the item compound
     * @param player the player entity opening the window
     */
    private static void openWindow(NBTTagCompound compound, World world, Player player)
    {
        if (compound.hasKey(TAG_COLONY))
        {
            final IColonyView colonyView = IColonyManager.getInstance().getColonyView(compound.getInt(TAG_COLONY), world.dimension());
            if (colonyView != null && colonyView.getClientBuildingManager().getTownHall() != null)
            {
                new WindowColonyMap(false, colonyView.getClientBuildingManager().getTownHall()).open();
            }
        }
        else
        {
            player.displayClientMessage(String.translatable(TranslationConstants.COM_MINECOLONIES_MAP_NEED_COLONY), true);
        }
    }
}





