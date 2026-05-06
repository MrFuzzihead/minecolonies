package com.minecolonies.core.items;
import net.minecraft.world.item.InteractionResult;
import net.minecraft.world.item.Properties;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.constant.TranslationConstants;
import com.minecolonies.core.client.gui.questlog.WindowQuestLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
// [1.7.10] int /* InteractionHand */ removed
// [1.7.10] InteractionResult -> boolean
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.World;
// [1.7.10] block.entity removed
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.Constants.STACKSIZE;
import static com.minecolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_QUEST_LOG_COLONY_SET;

/**
 * Class describing the quest log item.
 */
public class ItemQuestLog extends AbstractItemMinecolonies
{
    /**
     * NBTBase of the colony.
     */
    public static final String TAG_COLONY = "colony";

    /**
     * Sets the name, creative tab, and registers the quest log item.
     *
     * @param properties the properties.
     */
    public ItemQuestLog(final Properties properties)
    {
        super("questlog", properties.stacksTo(STACKSIZE));
    }

    @Override
    @NotNull
    public InteractionResult useOn(final UseOnContext ctx)
    {
        final ItemStack questLog = ctx.getPlayer().getItemInHand(ctx.getHand());

        final NBTTagCompound compound = checkForCompound(questLog);
        final BlockEntity entity = ctx.getLevel().getBlockEntity(ctx.getClickedPos());

        if (entity instanceof TileEntityColonyBuilding buildingEntity)
        {
            compound.putInt(TAG_COLONY, buildingEntity.getColonyId());
            if (!ctx.getLevel().isClientSide)
            {
                MessageUtils.format(COM_MINECOLONIES_QUEST_LOG_COLONY_SET, buildingEntity.getColony().getName()).sendTo(ctx.getPlayer());
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
        final ItemStack questLog = playerIn.getItemInHand(hand);

        if (!worldIn.isClientSide)
        {
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, questLog);
        }

        openWindow(checkForCompound(questLog), worldIn, playerIn);

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, questLog);
    }

    /**
     * Check for the compound and return it. If not available create and return it.
     *
     * @param questLog the quest log item to check for.
     * @return the compound of the quest log.
     */
    private static NBTTagCompound checkForCompound(final ItemStack questLog)
    {
        if (!questLog.hasTag())
        {
            questLog.setTag(new NBTTagCompound());
        }
        return questLog.getTag();
    }

    /**
     * Opens the quest log window if there is a valid colony linked
     *
     * @param compound the item compound
     * @param player   the player entity opening the window
     */
    private static void openWindow(NBTTagCompound compound, World world, Player player)
    {
        if (compound.contains(TAG_COLONY))
        {
            final IColonyView colonyView = IColonyManager.getInstance().getColonyView(compound.getInt(TAG_COLONY), world.dimension());
            if (colonyView != null)
            {
                new WindowQuestLog(colonyView).open();
            }
        }
        else
        {
            player.displayClientMessage(String.translatable(TranslationConstants.COM_MINECOLONIES_QUEST_LOG_NEED_COLONY), true);
        }
    }
}






