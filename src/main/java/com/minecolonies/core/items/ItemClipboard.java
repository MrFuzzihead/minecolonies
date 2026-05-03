package com.minecolonies.core.items;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.core.client.gui.WindowClipBoard;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.constant.TranslationConstants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
// [1.7.10] int /* InteractionHand */ removed
// [1.7.10] InteractionResult -> boolean
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Properties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.InteractionResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.World;
// [1.7.10] block.entity removed
import org.jetbrains.annotations.NotNull;
import static com.minecolonies.api.util.constant.Constants.STACKSIZE;
import static com.minecolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_CLIPBOARD_COLONY_SET;

/**
 * Class describing the clipboard item.
 */
public class ItemClipboard extends AbstractItemMinecolonies
{
    /**
     * NBTBase of the colony.
     */
    public static final String TAG_COLONY = "colony";

    /**
     * NBTBase of the "hide unimportant" UI toggle.
     */
    public static final String TAG_HIDEUNIMPORTANT = "hideunimportant";

    /**
     * Sets the name, creative tab, and registers the Clipboard item.
     *
     * @param properties the properties.
     */
    public ItemClipboard(final Properties properties)
    {
        super("clipboard", properties.stacksTo(STACKSIZE));
    }

    @Override
    @NotNull
    public InteractionResult useOn(final UseOnContext ctx)
    {
        final ItemStack clipboard = ctx.getPlayer().getItemInHand(ctx.getHand());

        final NBTTagCompound compound = checkForCompound(clipboard);
        final BlockEntity entity = ctx.getLevel().getBlockEntity(ctx.getClickedPos());

        if (entity instanceof TileEntityColonyBuilding buildingEntity)
        {
            compound.putInt(TAG_COLONY, buildingEntity.getColonyId());
            if (!ctx.getLevel().isClientSide)
            {
                MessageUtils.format(COM_MINECOLONIES_CLIPBOARD_COLONY_SET, buildingEntity.getColony().getName()).sendTo(ctx.getPlayer());
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
        final ItemStack clipboard = playerIn.getItemInHand(hand);

        if (!worldIn.isClientSide) {
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, clipboard);
        }

        openWindow(checkForCompound(clipboard), worldIn, playerIn);

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, clipboard);
    }

    /**
     * Check for the compound and return it. If not available create and return it.
     *
     * @param clipboard the clipboard to check for.
     * @return the compound of the clipboard.
     */
    private static NBTTagCompound checkForCompound(final ItemStack clipboard)
    {
        if (!clipboard.hasTag()) clipboard.setTag(new NBTTagCompound());
        return clipboard.getTag();
    }

    /**
     * Opens the clipboard window if there is a valid colony linked
     * @param compound the item compound
     * @param player the player entity opening the window
     */
    private static void openWindow(NBTTagCompound compound, World world, Player player)
    {
        if (compound.contains(TAG_COLONY))
        {
            final IColonyView colonyView = IColonyManager.getInstance().getColonyView(compound.getInt(TAG_COLONY), world.dimension());
            if (colonyView != null)
            {
                boolean hide = false;

                if (compound.contains(TAG_HIDEUNIMPORTANT))
                {
                    hide = compound.getBoolean(TAG_HIDEUNIMPORTANT);
                }

                new WindowClipBoard(colonyView, hide).open();
            }
        }
        else
        {
            player.displayClientMessage(String.translatable(TranslationConstants.COM_MINECOLONIES_CLIPBOARD_NEED_COLONY), true);
        }
    }
}





