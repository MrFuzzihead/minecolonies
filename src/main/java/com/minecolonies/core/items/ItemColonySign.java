package com.minecolonies.core.items;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity

import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.SoundUtils;
import com.minecolonies.api.util.constant.TranslationConstants;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import com.minecolonies.core.tileentities.TileEntityColonySign;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] InteractionResult -> boolean
import net.minecraft.world.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.InteractionResult;
import net.minecraft.world.item.Properties;
import net.minecraft.world.World;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.minecolonies.api.util.constant.Constants.STACKSIZE;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_COLONY_ID;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_POS;
import static com.minecolonies.api.util.constant.TranslationConstants.*;

/**
 * Class describing the colony sign item.
 */
public class ItemColonySign extends BlockItem
{
    /**
     * NBTBase of the colony.
     */
    public static final String TAG_COLONY = "colony";

    /**
     * Sets the name, creative tab, and registers the colony sign item.
     *
     * @param properties the properties.
     */
    public ItemColonySign(final Properties properties)
    {
        super(ModBlocks.blockColonySign, properties.stacksTo(STACKSIZE));
    }

    @Override
    public InteractionResult useOn(final UseOnContext ctx)
    {
        final ItemStack sign = ctx.getPlayer().getItemInHand(ctx.getHand());
        final NBTTagCompound compound = sign.getOrCreateTag();
        final BlockEntity entity = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
        final BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (ctx.getPlayer().isShiftKeyDown())
        {
            if (state.getBlock() == ModBlocks.blockHutGateHouse && entity instanceof TileEntityColonyBuilding buildingEntity)
            {
                if (!ctx.getLevel().isClientSide)
                {
                    if (buildingEntity.getColony() == null)
                    {
                        MessageUtils.format(COM_MINECOLONIES_SIGN_NULL_COLONY).sendTo(ctx.getPlayer());
                        return InteractionResult.SUCCESS;
                    }

                    if (buildingEntity.getBuilding() != null && buildingEntity.getBuilding().getBuildingLevel() <= 0)
                    {
                        MessageUtils.format(COM_MINECOLONIES_SIGN_BAD_GATEHOUSE).sendTo(ctx.getPlayer());
                        return InteractionResult.SUCCESS;
                    }

                    // Attempt Connect two colonies.
                    if (compound.contains(TAG_COLONY) && compound.getInt(TAG_COLONY) != buildingEntity.getColonyId())
                    {
                        final IColony sourceColony = IColonyManager.getInstance().getColonyByDimension(compound.getInt(TAG_COLONY), ctx.getLevel().dimension());
                        if (sourceColony == null)
                        {
                            MessageUtils.format(COM_MINECOLONIES_SIGN_NULL_COLONY).sendTo(ctx.getPlayer());
                            return InteractionResult.SUCCESS;
                        }

                        if (!sourceColony.getPermissions().hasPermission(ctx.getPlayer(), Action.MANAGE_HUTS))
                        {
                            MessageUtils.format(COM_MINECOLONIES_SIGN_COLONY_NO_PERM, buildingEntity.getColony().getName()).sendTo(ctx.getPlayer());
                            return InteractionResult.SUCCESS;
                        }

                        sourceColony.getConnectionManager().attemptEstablishConnection(ctx.getClickedPos(), buildingEntity.getColony());
                        return InteractionResult.SUCCESS;
                    }

                    if (buildingEntity.getColony().getPermissions().hasPermission(ctx.getPlayer(), Action.MANAGE_HUTS))
                    {
                        compound.putInt(TAG_COLONY, buildingEntity.getColonyId());
                        BlockPosUtil.write(compound, TAG_POS, ctx.getClickedPos());
                        MessageUtils.format(COM_MINECOLONIES_SIGN_COLONY_SET, buildingEntity.getColony().getName()).sendTo(ctx.getPlayer());
                    }
                    else
                    {
                        MessageUtils.format(COM_MINECOLONIES_SIGN_COLONY_NO_PERM, buildingEntity.getColony().getName()).sendTo(ctx.getPlayer());
                    }
                }
                return InteractionResult.SUCCESS;
            }
            else if (entity instanceof TileEntityColonySign signEntity)
            {
                if (!ctx.getLevel().isClientSide)
                {
                    final IColony colony = IColonyManager.getInstance().getColonyByDimension(signEntity.getColonyId(), ctx.getLevel().dimension());
                    if (colony == null)
                    {
                        MessageUtils.format(COM_MINECOLONIES_SIGN_NULL_COLONY).sendTo(ctx.getPlayer());
                        return InteractionResult.SUCCESS;
                    }

                    // Attempt connect two colonies.
                    if (compound.contains(TAG_COLONY) && compound.getInt(TAG_COLONY) != signEntity.getColonyId())
                    {
                        final IColony sourceColony = IColonyManager.getInstance().getColonyByDimension(compound.getInt(TAG_COLONY), ctx.getLevel().dimension());
                        if (sourceColony == null)
                        {
                            MessageUtils.format(COM_MINECOLONIES_SIGN_NULL_COLONY).sendTo(ctx.getPlayer());
                            return InteractionResult.SUCCESS;
                        }

                        if (!sourceColony.getPermissions().hasPermission(ctx.getPlayer(), Action.MANAGE_HUTS))
                        {
                            MessageUtils.format(COM_MINECOLONIES_SIGN_COLONY_NO_PERM, sourceColony.getName()).sendTo(ctx.getPlayer());
                            return InteractionResult.SUCCESS;
                        }

                        sourceColony.getConnectionManager().attemptEstablishConnection(ctx.getClickedPos(), colony);
                        return InteractionResult.SUCCESS;
                    }

                    if (colony.getPermissions().hasPermission(ctx.getPlayer(), Action.MANAGE_HUTS))
                    {
                        compound.putInt(TAG_COLONY, signEntity.getColonyId());
                        BlockPosUtil.write(compound, TAG_POS, ctx.getClickedPos());
                        MessageUtils.format(COM_MINECOLONIES_SIGN_COLONY_SET, colony.getName()).sendTo(ctx.getPlayer());
                    }
                    else
                    {
                        MessageUtils.format(COM_MINECOLONIES_SIGN_COLONY_NO_PERM, colony.getName()).sendTo(ctx.getPlayer());
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(ctx);
    }

    // [1.7.10 TODO] canPlace(BlockPlaceContext, BlockState) does not exist in 1.7.10 — ItemBlock.canPlace is different
    // This method is stubbed out; sign placement validation is done in onItemUse instead.
    /*
    @Override
    protected boolean canPlace(final BlockPlaceContext ctx, final BlockState state)
    {
        if (!super.canPlace(ctx, state))
        {
            return false;
        }
        if (!ctx.getItemInHand().getOrCreateTag().contains(TAG_COLONY))
        {
            if (ctx.getLevel().isClientSide)
            {
                MessageUtils.format(COM_MINECOLONIES_NEED_COLONY).sendTo(ctx.getPlayer());
            }
            return false;
        }

        if (!ctx.getLevel().isClientSide)
        {
            final int colonyId = ctx.getItemInHand().getTag().getInt(TAG_COLONY);
            final IColony colony = IColonyManager.getInstance().getColonyByDimension(colonyId, ctx.getLevel().dimension());
            if (colony == null)
            {
                MessageUtils.format(COM_MINECOLONIES_NEED_COLONY).sendTo(ctx.getPlayer());
                return false;
            }
            if (colony.getConnectionManager().addNewConnectionNode(ctx.getClickedPos()))
            {
                SoundUtils.playSuccessSound(ctx.getPlayer(), ctx.getClickedPos());
                return true;
            }
            else
            {
                SoundUtils.playErrorSound(ctx.getPlayer(), ctx.getClickedPos());
                return false;
            }
        }

        return true;
    }
    */
    @Override
    public void appendHoverText(@NotNull final ItemStack stack, @Nullable final World worldIn, @NotNull final List<String> tooltip, @NotNull final TooltipFlag flagIn)
    {
        if (stack.getOrCreateTag().contains(TAG_COLONY))
        {
            final String colonyHint = String.translatable(TranslationConstants.COM_MINECOLONIES_CORE_COLONY_SIGN_TOOLTIP_COLONY, stack.getOrCreateTag().getInt(TAG_COLONY));
            colonyHint.setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_BLUE));
            tooltip.add(colonyHint);
        }

        final String guiHint = String.translatable(TranslationConstants.COM_MINECOLONIES_CORE_COLONY_SIGN_TOOLTIP);
        guiHint.setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN));
        tooltip.add(guiHint);

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}





