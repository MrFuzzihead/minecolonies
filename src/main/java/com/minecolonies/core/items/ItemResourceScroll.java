package com.minecolonies.core.items;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.InteractionResult;
import net.minecraft.world.item.Properties;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.workorders.IWorkOrderView;
import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.constant.TranslationConstants;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.WindowResourceList;
import com.minecolonies.core.colony.buildings.moduleviews.BuildingResourcesModuleView;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingBuilder;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingWareHouse;
import com.minecolonies.core.network.messages.server.ResourceScrollSaveWarehouseSnapshotMessage;
import com.minecolonies.core.tileentities.TileEntityRack;
import com.minecolonies.core.tileentities.TileEntityWareHouse;
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
// [1.7.10] block.entity removed
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.Constants.STACKSIZE;
import static com.minecolonies.api.util.constant.NbtTagConstants.*;
import static com.minecolonies.api.util.constant.TranslationConstants.*;

/**
 * Class describing the resource scroll item.
 */
public class ItemResourceScroll extends AbstractItemMinecolonies
{
    /**
     * Sets the name, creative tab, and registers the resource scroll item.
     *
     * @param properties the properties.
     */
    public ItemResourceScroll(final Properties properties)
    {
        super("resourcescroll", properties.stacksTo(STACKSIZE));
    }

    /**
     * Opens the scroll window if there is a valid builder linked
     *
     * @param compound the item compound
     * @param player   the player entity opening the window
     */
    private static void openWindow(final NBTTagCompound compound, final Player player)
    {
        final int colonyId = compound.getInt(TAG_COLONY_ID);
        final int[] builderPos = compound.hasKey(TAG_BUILDER) ? BlockPosUtil.read(compound, TAG_BUILDER) : null;

        final IColonyView colonyView = IColonyManager.getInstance().getColonyView(colonyId, Minecraft.getInstance().World.dimension());
        if (colonyView != null)
        {
            final IBuildingView buildingView = colonyView.getClientBuildingManager().getBuilding(builderPos);
            if (buildingView instanceof BuildingBuilder.View builderBuildingView)
            {
                final String currentHash = getWorkOrderHash(buildingView);
                final String storedHash = compound.hasKey(TAG_WAREHOUSE_SNAPSHOT_WO_HASH) ? compound.getString(TAG_WAREHOUSE_SNAPSHOT_WO_HASH) : null;
                final boolean snapshotNeedsUpdate = !Objects.equals(currentHash, storedHash);

                Map<String, Integer> warehouseSnapshot = new HashMap<>();
                if (snapshotNeedsUpdate)
                {
                    // If the hashes no longer match one another, the NBT data is out of sync, inform the server to wipe the NBT.
                    Network.getNetwork().sendToServer(new ResourceScrollSaveWarehouseSnapshotMessage(builderPos));
                }
                else
                {
                    // If the hashes are still up-to-date, load the old snapshot data from the NBT, if any exists.
                    if (compound.hasKey(TAG_WAREHOUSE_SNAPSHOT))
                    {
                        final NBTTagCompound warehouseSnapshotCompound = compound.getCompoundTag(TAG_WAREHOUSE_SNAPSHOT);
                        warehouseSnapshot = warehouseSnapshotCompound.getAllKeys().stream()
                                              .collect(Collectors.toMap(k -> k, warehouseSnapshotCompound::getInt));
                    }
                }

                new WindowResourceList(builderBuildingView, warehouseSnapshot).open();
            }
            else
            {
                MessageUtils.format(String.translatable(TranslationConstants.COM_MINECOLONIES_SCROLL_NO_COLONY)).sendTo(player);
            }
        }
        else
        {
            MessageUtils.format(String.translatable(TranslationConstants.COM_MINECOLONIES_SCROLL_NO_COLONY)).sendTo(player);
        }
    }

    /**
     * Creates a work order hash from a work order view.
     *
     * @param buildingView the building view instance.
     * @return the work order hash.
     */
    @NotNull
    private static String getWorkOrderHash(final IBuildingView buildingView)
    {
        final Optional<IWorkOrderView> currentWorkOrder = buildingView.getColony()
                                                            .getWorkOrders()
                                                            .stream()
                                                            .filter(o -> o.getClaimedBy().equals(buildingView.getID()))
                                                            .max(Comparator.comparingInt(IWorkOrderView::getPriority));
        if (currentWorkOrder.isEmpty())
        {
            return "";
        }

        long location = currentWorkOrder.get().getLocation().asLong();
        return location + "__" + currentWorkOrder.get().getStructurePack();
    }

    /**
     * Updates the warehouse snapshot.
     *
     * @param warehousePos the position of the warehouse.
     * @param compound     the compound data.
     * @param player       the player entity who clicked the warehouse.
     */
    private static void updateWarehouseSnapshot(final int[] warehousePos, final NBTTagCompound compound, final Player player)
    {
        if (!compound.hasKey(TAG_COLONY_ID) || !compound.hasKey(TAG_BUILDER))
        {
            MessageUtils.format(COM_MINECOLONIES_SCROLL_NO_COLONY).sendTo(player);
            return;
        }

        final IColonyView colonyView = IColonyManager.getInstance().getColonyView(compound.getInt(TAG_COLONY_ID), Minecraft.getInstance().World.dimension());
        if (colonyView != null)
        {
            final int[] builderPos = BlockPosUtil.read(compound, TAG_BUILDER);
            final IBuildingView buildingView = colonyView.getClientBuildingManager().getBuilding(builderPos);
            if (buildingView instanceof BuildingBuilder.View)
            {
                final String currentHash = getWorkOrderHash(buildingView);
                final WarehouseSnapshot warehouseSnapshotData = gatherWarehouseSnapshot(buildingView, warehousePos, currentHash, player);

                if (warehouseSnapshotData != null)
                {
                    Network.getNetwork().sendToServer(new ResourceScrollSaveWarehouseSnapshotMessage(builderPos, warehouseSnapshotData.snapshot, warehouseSnapshotData.hash));
                }
                else
                {
                    Network.getNetwork().sendToServer(new ResourceScrollSaveWarehouseSnapshotMessage(builderPos));
                }
            }
        }
    }

    /**
     * Load the map of warehouse items from the given warehouse
     *
     * @param buildingView      the builder building view instance.
     * @param warehouseBlockPos the position of the warehouse.
     * @param hash              the current work order hash.
     * @param player            the player who triggered the resource scroll.
     * @return a map containing the snapshot, or null in case a fault appears.
     */
    @Nullable
    private static ItemResourceScroll.WarehouseSnapshot gatherWarehouseSnapshot(
      final IBuildingView buildingView,
      final int[] warehouseBlockPos,
      final String hash,
      final Player player)
    {
        final IBuildingView warehouse = buildingView.getColony().getClientBuildingManager().getBuilding(warehouseBlockPos);

        if (warehouse == null)
        {
            MessageUtils.format(COM_MINECOLONIES_SCROLL_WRONG_COLONY).sendTo(player);
            return null;
        }

        if (hash.isBlank())
        {
            return null;
        }

        final BuildingResourcesModuleView resourcesModule = buildingView.getModuleViewByType(BuildingResourcesModuleView.class);

        final Map<String, Integer> items = new HashMap<>();
        for (final int[] container : warehouse.getContainers())
        {
            final BlockEntity blockEntity = warehouse.getColony().getWorld().getBlockEntity(container);
            if (blockEntity instanceof TileEntityRack rack)
            {
                rack.getAllContent().forEach((item, amount) -> {
                    final int hashCode = item.getItemStack().hasTag() ? item.getItemStack().getTag().hashCode() : 0;
                    final String key = item.getItemStack().getDescriptionId() + "-" + hashCode;
                    if (!resourcesModule.getResources().containsKey(key))
                    {
                        return;
                    }

                    int oldAmount = items.getOrDefault(key, 0);
                    items.put(key, oldAmount + amount);
                });
            }
        }

        return new WarehouseSnapshot(items, hash);
    }

    /**
     * Used when clicking on block in world.
     *
     * @param ctx the context of use.
     * @return the result
     */
    @Override
    @NotNull
    public InteractionResult useOn(UseOnContext ctx)
    {
        final ItemStack scroll = ctx.getPlayer().getItemInHand(ctx.getHand());

        final NBTTagCompound compound = scroll.getOrCreateTag();
        final BlockEntity entity = ctx.getLevel().getBlockEntity(ctx.getClickedPos());

        if (ctx.getLevel().isClientSide)
        {
            if (entity instanceof AbstractTileEntityColonyBuilding buildingEntity)
            {
                if (buildingEntity instanceof TileEntityWareHouse)
                {
                    updateWarehouseSnapshot(buildingEntity.getTilePos(), compound, ctx.getPlayer());
                }
            }
            else
            {
                openWindow(compound, ctx.getPlayer());
            }
        }
        else if (entity instanceof AbstractTileEntityColonyBuilding buildingEntity)
        {
            if (buildingEntity.getBuilding() instanceof BuildingBuilder)
            {
                compound.setInteger(TAG_COLONY_ID, buildingEntity.getColonyId());
                BlockPosUtil.write(compound, TAG_BUILDER, buildingEntity.getPosition());

                MessageUtils.format(COM_MINECOLONIES_SCROLL_BUILDING_SET, buildingEntity.getColony().getName()).sendTo(ctx.getPlayer());
            }
            else if (buildingEntity.getBuilding() instanceof BuildingWareHouse)
            {
                MessageUtils.format(COM_MINECOLONIES_SCROLL_SNAPSHOT).sendTo(ctx.getPlayer());
            }
            else if (buildingEntity.getBuilding() != null)
            {
                final String buildingTypeComponent = MessageUtils.format(buildingEntity.getBuilding().getBuildingType().getTranslationKey()).create();
                MessageUtils.format(COM_MINECOLONIES_SCROLL_WRONG_BUILDING, buildingTypeComponent, buildingEntity.getColony().getName()).sendTo(ctx.getPlayer());
            }
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

        if (!worldIn.isClientSide)
        {
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, clipboard);
        }

        openWindow(clipboard.getOrCreateTag(), playerIn);

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, clipboard);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<String> tooltip, TooltipFlag flagIn)
    {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        if (worldIn == null)
        {
            return;
        }

        final NBTTagCompound compound = stack.getOrCreateTag();
        final int colonyId = compound.getInt(TAG_COLONY_ID);
        final int[] builderPos = BlockPosUtil.read(compound, TAG_BUILDER);

        final IColonyView colonyView = IColonyManager.getInstance().getColonyView(colonyId, worldIn.dimension());
        if (colonyView != null)
        {
            final IBuildingView buildingView = colonyView.getClientBuildingManager().getBuilding(builderPos);
            if (buildingView instanceof BuildingBuilder.View builderBuildingView)
            {
                String name = builderBuildingView.getWorkerName();
                tooltip.add(name != null && !name.trim().isEmpty()
                              ? String.literal(ChatFormatting.DARK_PURPLE + name)
                              : String.translatable(COM_MINECOLONIES_SCROLL_BUILDING_NO_WORKER));
            }
        }
    }

    /**
     * Container class for warehouse snapshot data.
     *
     * @param snapshot the snapshot data.
     * @param hash     the work order hash for comparison between work orders.
     */
    private record WarehouseSnapshot(Map<String, Integer> snapshot, String hash)
    {
    }
}





