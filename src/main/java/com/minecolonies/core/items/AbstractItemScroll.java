package com.minecolonies.core.items;
import net.minecraft.world.item.InteractionResult;
import net.minecraft.world.item.Properties;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.MessageUtils;
// [1.7.10] Registries removed
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.UseAnim;
import net.minecraft.nbt.NBTTagCompound;
// [1.7.10] block.entity removed
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_COLONY_ID;
import static com.minecolonies.api.util.constant.TranslationConstants.*;

// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
// [1.7.10] int /* InteractionHand */ removed
// [1.7.10] InteractionResult -> boolean
import net.minecraft.world.InteractionResultHolder;

/**
 * Scroll items base class, does colony registering/checks.
 */
public abstract class AbstractItemScroll extends AbstractItemMinecolonies
{
    public static final String TAG_COLONY_DIM = "colony_dim";
    public static final String TAG_BUILDING_POS = "building_pos";
    public static final String TAG_CACHED_COLONY_NAME = "cached_colony_name";

    public static final int    FAIL_RESPONSES_TOTAL = 10;

    /**
     * Sets the name, creative tab, and registers the item.
     *
     * @param name       The name of this item
     * @param properties the properties.
     */
    public AbstractItemScroll(final String name, final Properties properties)
    {
        super(name, properties);
    }

    @Override
    public int getUseDuration(ItemStack itemStack)
    {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack)
    {
        return UseAnim.BOW;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, World world, EntityLivingBase entityLiving)
    {
        if (!(entityLiving instanceof EntityPlayerMP) || world.isClientSide)
        {
            return itemStack;
        }

        final EntityPlayerMP player = (EntityPlayerMP) entityLiving;

        if (!needsColony())
        {
            return onItemUseSuccess(itemStack, world, player);
        }

        final IColony colony = getColony(itemStack);
        if (colony == null)
        {
            player.displayClientMessage(String.translatable(MESSAGE_SCROLL_NEED_COLONY), true);
            return itemStack;
        }

        if (!colony.getPermissions().hasPermission(player, Action.RIGHTCLICK_BLOCK))
        {
            MessageUtils.format(MESSAGE_SCROLL_NO_PERMISSION).sendTo(player);
            return itemStack;
        }

        return onItemUseSuccess(itemStack, world, player);
    }

    /**
     * Called when the item gets used
     *
     * @param itemStack stack thats used
     * @param world     world its used in
     * @param player    player its used by
     * @return stack
     */
    protected abstract ItemStack onItemUseSuccess(final ItemStack itemStack, final World world, final EntityPlayerMP player);

    @Override
    public InteractionResultHolder<ItemStack> use(World world, Player player, int /* InteractionHand */ hand)
    {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);

        // Sneak rightclick
        return new InteractionResultHolder<>(InteractionResult.FAIL, itemStack);
    }

    @Override
    @NotNull
    public InteractionResult useOn(UseOnContext ctx)
    {
        // Right click on block
        if (ctx.getLevel().isClientSide || !ctx.getPlayer().isShiftKeyDown() || !needsColony())
        {
            return InteractionResult.PASS;
        }

        final BlockEntity te = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
        final ItemStack scroll = ctx.getPlayer().getItemInHand(ctx.getHand());
        final NBTTagCompound compound = checkForCompound(scroll);
        if (te instanceof TileEntityColonyBuilding buildingTe)
        {
            compound.putInt(TAG_COLONY_ID, buildingTe.getColonyId());
            compound.putString(TAG_COLONY_DIM, buildingTe.getColony().getWorld().dimension().location().toString());
            BlockPosUtil.write(compound, TAG_BUILDING_POS, ctx.getClickedPos());
            MessageUtils.format(MESSAGE_SCROLL_REGISTERED, buildingTe.getColony().getName()).sendTo(ctx.getPlayer());
            compound.putString(TAG_CACHED_COLONY_NAME, buildingTe.getColony().getName());
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Whether this items need to register to a colony
     *
     * @return true if so
     */
    protected abstract boolean needsColony();

    private static NBTTagCompound checkForCompound(final ItemStack item)
    {
        if (!item.hasTag())
        {
            item.setTag(new NBTTagCompound());
        }
        return item.getTag();
    }

    /**
     * Get the colony from the stack
     *
     * @param stack to use
     * @return colony
     */
    protected IColony getColony(final ItemStack stack)
    {
        if (!stack.hasTag() || !stack.getTag().contains(TAG_COLONY_ID) || !stack.getTag().contains(TAG_COLONY_DIM))
        {
            return null;
        }

        return IColonyManager.getInstance().getColonyByDimension(stack.getTag().getInt(TAG_COLONY_ID), ResourceKey.create(Registries.DIMENSION, new ResourceLocation(stack.getTag().getString(TAG_COLONY_DIM))));
    }

    /**
     * Get the colony view from the stack
     *
     * @param stack to use
     * @return colony
     */
    protected IColony getColonyView(final ItemStack stack)
    {
        if (!stack.hasTag() || !stack.getTag().contains(TAG_COLONY_ID) || !stack.getTag().contains(TAG_COLONY_DIM))
        {
            return null;
        }

        return IColonyManager.getInstance().getColonyView(stack.getTag().getInt(TAG_COLONY_ID), ResourceKey.create(Registries.DIMENSION, new ResourceLocation(stack.getTag().getString(TAG_COLONY_DIM))));
    }
}





