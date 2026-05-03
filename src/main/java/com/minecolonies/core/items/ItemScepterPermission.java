package com.minecolonies.core.items;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.items.IBlockOverlayItem;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.core.Network;
import com.minecolonies.core.network.messages.server.colony.ChangeFreeToInteractBlockMessage;
import net.minecraft.util.EnumChatFormatting;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] int /* InteractionHand */ removed
// [1.7.10] InteractionResult -> boolean
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.World;
import net.minecraft.block.Block;
// [1.7.10] BlockState -> int metadata
// [1.7.10] world.phys removed
// [1.7.10] world.phys removed
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.minecolonies.api.util.constant.translation.ToolTranslationConstants.*;

/**
 * Permission scepter. used to add free to interact blocks or positions to the colonies permission list
 */
public class ItemScepterPermission extends AbstractItemMinecolonies implements IBlockOverlayItem
{
    /**
     * The NBT NBTBase of the mode
     */
    private static final String TAG_ITEM_MODE = "scepterMode";

    /**
     * the scepters block mode NBTBase value
     */
    private static final String TAG_VALUE_MODE_BLOCK = "modeBlock";

    /**
     * the scepters location mode NBTBase value
     */
    private static final String TAG_VALUE_MODE_LOCATION = "modeLocation";

    private static final int GREEN_OVERLAY = 0xFF00FF00;
    private static final int BLOCK_OVERLAY_RANGE_XZ = 32;
    private static final int BLOCK_OVERLAY_RANGE_Y = 6;

    /**
     * constructor.
     * <p>
     * - set the name - set max damage value - set creative tab - set max stack size
     *
     * @param properties the properties.
     */
    public ItemScepterPermission(final Item.Properties properties)
    {
        super("scepterpermission", properties.stacksTo(1).durability(2));
    }

    @NotNull
    private static InteractionResult handleAddBlockType(
      final Player playerIn,
      final World worldIn,
      final int[] pos,
      final IColonyView iColonyView)
    {
        final BlockState blockState = iColonyView.getWorld().getBlockState(pos);
        final Block block = blockState.getBlock();

        final ChangeFreeToInteractBlockMessage.MessageType type = Screen.hasControlDown()
                ? ChangeFreeToInteractBlockMessage.MessageType.REMOVE_BLOCK
                : ChangeFreeToInteractBlockMessage.MessageType.ADD_BLOCK;
        final ChangeFreeToInteractBlockMessage message = new ChangeFreeToInteractBlockMessage(
          iColonyView,
          block,
          type);
        Network.getNetwork().sendToServer(message);

        return InteractionResult.SUCCESS;
    }

    @NotNull
    private static InteractionResult handleAddLocation(
      final Player playerIn,
      final World worldIn,
      final int[] pos,
      final IColonyView iColonyView)
    {
        final ChangeFreeToInteractBlockMessage.MessageType type = Screen.hasControlDown()
                ? ChangeFreeToInteractBlockMessage.MessageType.REMOVE_BLOCK
                : ChangeFreeToInteractBlockMessage.MessageType.ADD_BLOCK;
        final ChangeFreeToInteractBlockMessage message = new ChangeFreeToInteractBlockMessage(iColonyView, pos, type);
        Network.getNetwork().sendToServer(message);

        return InteractionResult.SUCCESS;
    }

    /**
     * Used when clicking on block in world.
     *
     * @return the result
     */
    @Override
    @NotNull
    public InteractionResult useOn(final UseOnContext ctx)
    {
        if (!ctx.getLevel().isClientSide)
        {
            return InteractionResult.SUCCESS;
        }
        final ItemStack scepter = ctx.getPlayer().getItemInHand(ctx.getHand());
        if (!scepter.hasTag())
        {
            scepter.setTag(new NBTTagCompound());
        }

        final IColonyView iColonyView = IColonyManager.getInstance().getClosestColonyView(ctx.getLevel(), ctx.getClickedPos());
        if (iColonyView == null)
        {
            return InteractionResult.FAIL;
        }
        final NBTTagCompound compound = scepter.getTag();
        return handleItemAction(compound, ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), iColonyView);
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
        final ItemStack scepter = playerIn.getItemInHand(hand);
        if (worldIn.isClientSide)
        {
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, scepter);
        }
        if (!scepter.hasTag())
        {
            scepter.setTag(new NBTTagCompound());
        }
        final NBTTagCompound compound = scepter.getTag();

        toggleItemMode(playerIn, compound);

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, scepter);
    }

    private static void toggleItemMode(final Player playerIn, final NBTTagCompound compound)
    {
        final String itemMode = compound.getString(TAG_ITEM_MODE);

        switch (itemMode)
        {
            case TAG_VALUE_MODE_BLOCK:
                compound.putString(TAG_ITEM_MODE, TAG_VALUE_MODE_LOCATION);
                MessageUtils.format(TOOL_PERMISSION_SCEPTER_SET_MODE, MessageUtils.format(TOOL_PERMISSION_SCEPTER_MODE_LOCATION).create()).sendTo(playerIn);
                break;
            case TAG_VALUE_MODE_LOCATION:
            default:
                compound.putString(TAG_ITEM_MODE, TAG_VALUE_MODE_BLOCK);
                MessageUtils.format(TOOL_PERMISSION_SCEPTER_SET_MODE, MessageUtils.format(TOOL_PERMISSION_SCEPTER_MODE_BLOCK).create()).sendTo(playerIn);
                break;
        }
    }

    @NotNull
    @Override
    public List<OverlayBox> getOverlayBoxes(@NotNull final World world, @NotNull final Player player, @NotNull final ItemStack stack)
    {
        final List<OverlayBox> boxes = new ArrayList<>();
        final IColonyView colony = IColonyManager.getInstance().getClosestColonyView(world, player.blockPosition());
        if (colony == null || !colony.getPermissions().hasPermission(player, Action.EDIT_PERMISSIONS))
        {
            return boxes;
        }

        final String itemMode = stack.getOrCreateTag().getString(TAG_ITEM_MODE);
        switch (itemMode)
        {
            case TAG_VALUE_MODE_BLOCK:
                final Set<Block> freeBlocks = new HashSet<>(colony.getFreeBlocks());
                for (final int[] pos : new java.util.ArrayList<int[]>()) // TODO: [1.7.10] BlockPos.withinManhattan stub
                {
                    if (world.isLoaded(pos) && freeBlocks.contains(world.getBlockState(pos).getBlock()))
                    {
                        boxes.add(new OverlayBox(AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(pos)), GREEN_OVERLAY, 0.02f, true));
                    }
                }
                break;
            case TAG_VALUE_MODE_LOCATION:
            default:
                for (final int[] pos : colony.getFreePositions())
                {
                    boxes.add(new OverlayBox(AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(pos)), GREEN_OVERLAY, 0.02f, true));
                }
                break;
        }

        return boxes;
    }

    @Override
    public void appendHoverText(@NotNull final ItemStack stack, @Nullable final World World,
                                @NotNull final List<String> tooltip, @NotNull final TooltipFlag flags)
    {
        final String itemMode = stack.getOrCreateTag().getString(TAG_ITEM_MODE);
        final String mode;
        switch (itemMode)
        {
            case TAG_VALUE_MODE_BLOCK:
                mode = String.translatable(TOOL_PERMISSION_SCEPTER_MODE_BLOCK);
                break;
            case TAG_VALUE_MODE_LOCATION:
            default:
                mode = String.translatable(TOOL_PERMISSION_SCEPTER_MODE_LOCATION);
                break;
        }
        tooltip.add(String.translatable(TOOL_PERMISSION_SCEPTER_MODE, mode.withStyle(ChatFormatting.YELLOW)));

        super.appendHoverText(stack, World, tooltip, flags);
    }

    @NotNull
    private static InteractionResult handleItemAction(
      final NBTTagCompound compound,
      final Player playerIn,
      final World worldIn,
      final int[] pos,
      final IColonyView iColonyView)
    {
        final String tagItemMode = compound.getString(TAG_ITEM_MODE);

        switch (tagItemMode)
        {
            case TAG_VALUE_MODE_BLOCK:
                return handleAddBlockType(playerIn, worldIn, pos, iColonyView);
            case TAG_VALUE_MODE_LOCATION:
                return handleAddLocation(playerIn, worldIn, pos, iColonyView);
            default:
                toggleItemMode(playerIn, compound);
                return handleItemAction(compound, playerIn, worldIn, pos, iColonyView);
        }
    }
}





