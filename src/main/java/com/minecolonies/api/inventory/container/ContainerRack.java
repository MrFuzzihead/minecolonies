package com.minecolonies.api.inventory.container;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.blocks.AbstractBlockMinecoloniesRack;
import com.minecolonies.api.blocks.types.RackType;
import com.minecolonies.api.inventory.ModContainers;
import com.minecolonies.api.tileentities.AbstractTileEntityRack;
import com.minecolonies.api.util.ItemStackUtils;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.EntityPlayerMP;
// [1.7.10] Inventory removed
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
// [1.7.10] int not in 1.7.10
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
// [1.7.10] items shim in com.minecolonies.api.shim
// [1.7.10] items shim in com.minecolonies.api.shim
// [1.7.10] items shim in com.minecolonies.api.shim
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.InventoryConstants.*;

/**
 * The container class for the rack.
 */
public class ContainerRack extends Container
{
    /**
     * The inventory.
     */
    private final net.minecraftforge.items.IItemHandler inventory;

    /**
     * The tileEntity.
     */
    public final AbstractTileEntityRack rack;

    /**
     * The tileEntity.
     */
    public final AbstractTileEntityRack neighborRack;

    /**
     * Amount of rows.
     */
    private final int inventorySize;

    /**
     * Deserialize packet buffer to container instance.
     *
     * @param windowId     the id of the window.
     * @param inv          the EntityPlayer inventory.
     * @param packetBuffer network buffer
     * @return new instance
     */
    public static ContainerRack fromPacketBuffer(final int windowId, final InventoryPlayer inv, final PacketBuffer packetBuffer)
    {
        final int[] tePos = packetBuffer.readBlockPos();
        final int[] neighborPos = packetBuffer.readBlockPos();
        return new ContainerRack(windowId, inv, tePos, neighborPos);
    }

    /**
     * The container constructor.
     *
     * @param windowId the window id.
     * @param inv      the inventory.
     * @param rack     te world pos.
     * @param neighbor neighbor te world pos
     */
    public ContainerRack(final int windowId, final InventoryPlayer inv, final int[] rack, final int[] neighbor)
    {
        super();

        final AbstractTileEntityRack abstractTileEntityRack = (AbstractTileEntityRack) inv.player.World().getBlockEntity(rack);
        // TODO: bug, what if neighbor is actually bp.ZERO? (unlikely to happen)
        final AbstractTileEntityRack neighborRack = neighbor.equals(new int[]{0,0,0}) ? null : (AbstractTileEntityRack) inv.player.World().getBlockEntity(neighbor);

        if (neighborRack != null)
        {
            if (abstractTileEntityRack.getBlockState().getValue(AbstractBlockMinecoloniesRack.VARIANT) != RackType.NO_RENDER)
            {
                this.inventory = new CombinedInvWrapper(abstractTileEntityRack.getInventory(), neighborRack.getInventory());
            }
            else
            {
                this.inventory = new CombinedInvWrapper(neighborRack.getInventory(), abstractTileEntityRack.getInventory());
            }
        }
        else
        {
            this.inventory = abstractTileEntityRack.getInventory();
        }

        this.rack = abstractTileEntityRack;
        this.neighborRack = neighborRack;
        this.inventorySize = this.inventory.getSlots() / INVENTORY_COLUMNS;
        final int size = this.inventory.getSlots();

        final int columns = inventorySize <= INVENTORY_BAR_SIZE ? INVENTORY_COLUMNS : ((size / INVENTORY_BAR_SIZE) + 1);
        final int extraOffset = inventorySize <= INVENTORY_BAR_SIZE ? 0 : 2;
        int index = 0;

        for (int j = 0; j < Math.min(this.inventorySize, INVENTORY_BAR_SIZE); ++j)
        {
            for (int k = 0; k < columns; ++k)
            {
                if (index < size)
                {
                    this.addSlot(
                      new SlotItemHandler(inventory, index,
                        INVENTORY_BAR_SIZE + k * PLAYER_INVENTORY_OFFSET_EACH,
                        PLAYER_INVENTORY_OFFSET_EACH + j * PLAYER_INVENTORY_OFFSET_EACH));
                    index++;
                }
            }
        }

        // EntityPlayer inventory slots
        // Note: The slot numbers are within the EntityPlayer inventory and may be the same as the field inventory.
        int i;
        for (i = 0; i < INVENTORY_ROWS; i++)
        {
            for (int j = 0; j < INVENTORY_COLUMNS; j++)
            {
                addSlot(new Slot(
                  inv,
                  j + i * INVENTORY_COLUMNS + INVENTORY_COLUMNS,
                  PLAYER_INVENTORY_INITIAL_X_OFFSET + j * PLAYER_INVENTORY_OFFSET_EACH,
                  PLAYER_INVENTORY_INITIAL_Y_OFFSET + extraOffset + PLAYER_INVENTORY_OFFSET_EACH * Math.min(this.inventorySize, INVENTORY_BAR_SIZE)
                    + i * PLAYER_INVENTORY_OFFSET_EACH
                ));
            }
        }

        for (i = 0; i < INVENTORY_COLUMNS; i++)
        {
            addSlot(new Slot(
              inv, i,
              PLAYER_INVENTORY_INITIAL_X_OFFSET + i * PLAYER_INVENTORY_OFFSET_EACH,
              PLAYER_INVENTORY_HOTBAR_OFFSET + extraOffset + PLAYER_INVENTORY_OFFSET_EACH * Math.min(this.inventorySize,
                INVENTORY_BAR_SIZE)
            ));
        }
    }

    @Override
    public void clicked(int slotId, int dragType, @NotNull int clickTypeIn, EntityPlayer player)
    {
        if (player.World().isRemote || slotId >= inventory.getSlots() || slotId < 0)
        {
            super.clicked(slotId, dragType, clickTypeIn, player);
            return;
        }
        final ItemStack currentStack = inventory.getStackInSlot(slotId).copy();
        super.clicked(slotId, dragType, clickTypeIn, player);
        final ItemStack afterStack = inventory.getStackInSlot(slotId).copy();

        if (!ItemStack.matches(currentStack, afterStack))
        {
            this.updateRacks(afterStack);
        }
    }

    @NotNull
    @Override
    public ItemStack transferStackInSlot(final EntityPlayer playerIn, final int index)
    {
        final Slot slot = this.slots.get(index);

        if (slot == null || !slot.getHasStack())
        {
            return ItemStackUtils.EMPTY;
        }

        final ItemStack stackCopy = slot.getStack().copy();

        final int maxIndex = this.inventorySize * INVENTORY_COLUMNS;

        if (index < maxIndex)
        {
            if (!this.moveItemStackTo(stackCopy, maxIndex, this.slots.size(), true))
            {
                return ItemStackUtils.EMPTY;
            }
        }
        else if (!this.moveItemStackTo(stackCopy, 0, maxIndex, false))
        {
            return ItemStackUtils.EMPTY;
        }

        if (ItemStackUtils.getSize(stackCopy) == 0)
        {
            slot.set(ItemStackUtils.EMPTY);
        }
        else
        {
            slot.set(stackCopy);
            slot.setChanged();
        }

        if (playerIn instanceof EntityPlayerMP)
        {
            this.updateRacks(stackCopy);
        }

        return stackCopy;
    }

    @Override
    protected boolean moveItemStackTo(final ItemStack stack, final int startIndex, final int endIndex, final boolean reverseDirection)
    {
        final ItemStack before = stack.copy();
        final boolean merge =  super.moveItemStackTo(stack, startIndex, endIndex, reverseDirection);
        if (merge)
        {
            this.updateRacks(before);
        }
        return merge;
    }

    /**
     * Update the racks (combined inv and warehouse).
     * @param stack the stack to set.
     */
    private void updateRacks(final ItemStack stack)
    {
        rack.updateItemStorage();
        rack.updateWarehouseIfAvailable(stack);
        if (neighborRack != null)
        {
            neighborRack.updateItemStorage();
            neighborRack.updateWarehouseIfAvailable(stack);
        }
    }

    @Override
    public boolean canInteractWith(final EntityPlayer playerIn)
    {
        return true;
    }
}




