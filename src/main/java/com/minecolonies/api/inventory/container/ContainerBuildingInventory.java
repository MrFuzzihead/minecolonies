package com.minecolonies.api.inventory.container;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.inventory.ModContainers;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import com.minecolonies.api.util.ItemStackUtils;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraft.item.ItemStack;
// [1.7.10] items shim in com.minecolonies.api.shim
// [1.7.10] items shim in com.minecolonies.api.shim
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.InventoryConstants.*;

/**
 * Container for Mie
 */
public class ContainerBuildingInventory extends Container
{
    /**
     * Lower chest inventory.
     */
    private final net.minecraftforge.items.IItemHandler buildingInventory;

    private final TileEntityColonyBuilding tileEntityColonyBuilding;

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
    public static ContainerBuildingInventory fromPacketBuffer(final int windowId, final InventoryPlayer inv, final PacketBuffer packetBuffer)
    {
        final int colonyId = packetBuffer.readVarInt();
        final int[] tePos = packetBuffer.readBlockPos();
        return new ContainerBuildingInventory(windowId, inv, colonyId, tePos);
    }

    /**
     * Constructor to create an instance of this container.
     *
     * @param windowId the id of the window.
     * @param inv      the EntityPlayer inventory.
     * @param colonyId colony id
     * @param pos      te world pos
     */
    public ContainerBuildingInventory(final int windowId, final InventoryPlayer inv, final int colonyId, final int[] pos)
    {
        super();

        tileEntityColonyBuilding = (TileEntityColonyBuilding) inv.player.worldObj.getTileEntity(pos[0], pos[1], pos[2]);
        this.buildingInventory = tileEntityColonyBuilding.getInventory();
        final int size = buildingInventory.getSlots();
        this.inventorySize = size / INVENTORY_COLUMNS;

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
                      new SlotItemHandler(buildingInventory, index,
                        INVENTORY_BAR_SIZE + k * PLAYER_INVENTORY_OFFSET_EACH,
                        PLAYER_INVENTORY_OFFSET_EACH + j * PLAYER_INVENTORY_OFFSET_EACH)
                      {
                          @Override
                          public void set(final ItemStack stack)
                          {
                              super.putStack(stack);
                              if (!inv.player.worldObj.isRemote && !ItemStackUtils.isEmpty(stack))
                              {
                                  final IColony colony = IColonyManager.getInstance().getColonyByWorld(colonyId, inv.player.worldObj);
                                  final IBuilding building = colony.getServerBuildingManager().getBuilding(pos);
                                  if (building != null)
                                  {
                                      building.overruleNextOpenRequestWithStack(stack);
                                  }
                              }
                          }
                      });
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

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the EntityPlayer inventory and the other inventory(s).
     *
     * @param playerIn EntityPlayer that interacted with this {@code Container}.
     * @param index    Index of the {@link Slot}. This index is relative to the list of slots in this {@code AbstractContainerMenu}, {@link #slots}.
     */
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
            slot.putStack(ItemStackUtils.EMPTY);
        }
        else
        {
            slot.putStack(stackCopy);
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
        tileEntityColonyBuilding.updateItemStorage();
        tileEntityColonyBuilding.updateWarehouseIfAvailable(stack);
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void onContainerClosed(final EntityPlayer playerIn)
    {
        super.removed(playerIn);
    }

    /**
     * Determines whether supplied EntityPlayer can use this container
     */
    @Override
    public boolean canInteractWith(@NotNull final EntityPlayer playerIn)
    {
        return this.tileEntityColonyBuilding.isUseableByPlayer(playerIn); // [1.7.10] isUsableByPlayer→isUseableByPlayer
    }

    /**
     * Get the size of the inventory.
     *
     * @return the size.
     */
    public int getSize()
    {
        return inventorySize;
    }
}



