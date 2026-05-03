package com.minecolonies.api.inventory.container;
import net.minecraftforge.items.SlotItemHandler;
import com.minecolonies.api.inventory.ModContainers;
import com.minecolonies.api.util.ItemStackUtils;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
// [1.7.10] Inventory removed
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
// [1.7.10] int not in 1.7.10
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
// [1.7.10] BrewingRecipeRegistry not in 1.7.10 forge
// [1.7.10] items shim in com.minecolonies.api.shim
// [1.7.10] items shim in com.minecolonies.api.shim
// [1.7.10] items shim in com.minecolonies.api.shim
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.minecolonies.api.util.constant.InventoryConstants.*;

/**
 * Crafting container for the recipe teaching of furnace recipes.
 */
public class ContainerCraftingBrewingstand extends Container
{
    /**
     * The furnace inventory.
     */
    private final net.minecraftforge.items.IItemHandler brewingStandInventory;

    /**
     * The EntityPlayer assigned to it.
     */
    private final InventoryPlayer playerInventory;

    /**
     * The colony building.
     */
    public final int[] buildingPos;

    /**
     * The module id.
     */
    private int moduleId;

    /**
     * Deserialize packet buffer to container instance.
     *
     * @param windowId     the id of the window.
     * @param inv          the EntityPlayer inventory.
     * @param packetBuffer network buffer
     * @return new instance
     */
    public static ContainerCraftingBrewingstand fromPacketBuffer(final int windowId, final InventoryPlayer inv, final PacketBuffer packetBuffer)
    {
        final int[] tePos = packetBuffer.readBlockPos();
        final int moduleId = packetBuffer.readInt();
        return new ContainerCraftingBrewingstand(windowId, inv, tePos, moduleId);
    }

    /**
     * Constructs the GUI with the player.
     *
     * @param windowId the window id.
     * @param inv      the EntityPlayer inventory.
     * @param pos      te world pos
     */
    public ContainerCraftingBrewingstand(final int windowId, final InventoryPlayer inv, final int[] pos, final int moduleId)
    {
        super();
        this.moduleId = moduleId;

        this.brewingStandInventory = new IItemHandlerModifiable()
        {
            ItemStack ingredient = null;
            ItemStack potion = null;

            @Override
            public void setStackInSlot(final int slot, @Nonnull final ItemStack stack)
            {
                if (!isItemValid(slot, stack) && !ItemStackUtils.isEmpty(stack))
                {
                    return;
                }

                final ItemStack copy = stack.copy();
                copy.setCount(1);
                if (slot == 3)
                {
                    ingredient = copy;
                }
                else
                {
                    potion = copy;
                }
            }

            @Override
            public int getSlots()
            {
                return 4;
            }

            @Nonnull
            @Override
            public ItemStack getStackInSlot(final int slot)
            {
                if (slot == 3)
                {
                    return ingredient;
                }
                else
                {
                    return potion;
                }
            }

            @Nonnull
            @Override
            public ItemStack insertItem(final int slot, @Nonnull final ItemStack stack, final boolean simulate)
            {
                if (!isItemValid(slot, stack) && !ItemStackUtils.isEmpty(stack))
                {
                    return stack;
                }

                final ItemStack copy = stack.copy();
                copy.setCount(1);
                if (slot == 3)
                {
                    ingredient = copy;
                }
                else
                {
                    potion = copy;
                }
                return stack;
            }

            @Nonnull
            @Override
            public ItemStack extractItem(final int slot, final int amount, final boolean simulate)
            {
                return null;
            }

            @Override
            public int getSlotLimit(final int slot)
            {
                return 1;
            }

            @Override
            public boolean isItemValid(final int slot, @Nonnull final ItemStack stack)
            {
                if (slot == 3)
                {
                    return false; // [1.7.10] BrewingRecipeRegistry not available
                }
                else if (slot >= 0 && slot < 3)
                {
                    return false; // [1.7.10] BrewingRecipeRegistry not available
                }
                else
                {
                    return false;
                }
            }
        };
        this.playerInventory = inv;
        this.buildingPos = pos;

        this.addSlot(new SlotItemHandler(brewingStandInventory, 3, 79, 17));

        this.addSlot(new InputItemHandler(brewingStandInventory, 0, 56, 51));
        this.addSlot(new InputItemHandler(brewingStandInventory, 1, 79, 58));
        this.addSlot(new InputItemHandler(brewingStandInventory, 2, 102, 51));

        // EntityPlayer inventory slots
        // Note: The slot numbers are within the EntityPlayer inventory and may be the same as the field inventory.
        int i;
        for (i = 0; i < INVENTORY_ROWS; i++)
        {
            for (int j = 0; j < INVENTORY_COLUMNS; j++)
            {
                addSlot(new Slot(
                  playerInventory,
                  j + i * INVENTORY_COLUMNS + INVENTORY_COLUMNS,
                  PLAYER_INVENTORY_INITIAL_X_OFFSET + j * PLAYER_INVENTORY_OFFSET_EACH,
                  PLAYER_INVENTORY_INITIAL_Y_OFFSET_CRAFTING + i * PLAYER_INVENTORY_OFFSET_EACH
                ));
            }
        }

        for (i = 0; i < INVENTORY_COLUMNS; i++)
        {
            addSlot(new Slot(
              playerInventory, i,
              PLAYER_INVENTORY_INITIAL_X_OFFSET + i * PLAYER_INVENTORY_OFFSET_EACH,
              PLAYER_INVENTORY_HOTBAR_OFFSET_CRAFTING
            ));
        }
    }

    /**
     * Special input item handler for the brewing stand.
     */
    private static class InputItemHandler extends SlotItemHandler
    {
        /**
         * Default constructor.
         * @param itemHandler the inventory.
         * @param index the index.
         * @param xPosition x positon.
         * @param yPosition y position.
         */
        public InputItemHandler(final net.minecraftforge.items.IItemHandler itemHandler, final int index, final int xPosition, final int yPosition)
        {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public int getMaxStackSize()
        {
            return 1;
        }

        @NotNull
        @Override
        public ItemStack remove(final int par1)
        {
            return null;
        }

        @Override
        public boolean isItemValid(final @NotNull ItemStack par1ItemStack)
        {
            return true;
        }

        @Override
        public boolean canTakeStack(final EntityPlayer par1PlayerEntity)
        {
            return false;
        }
    }

    @Override
    public void clicked(final int slotId, final int clickedButton, final int mode, final EntityPlayer playerIn)
    {
        if (slotId >= 0 && slotId < brewingStandInventory.getSlots())
        {
            if (mode == 0
                  || mode == 1
                  || mode == 2
                  || mode == 3)
            {
                final Slot slot = this.slots.get(slotId);
                handleSlotClick(slot, this.getCarried());
            }
        }
        else
        {
            super.clicked(slotId, clickedButton, mode, playerInventory.player);
        }
    }

    /**
     * Sets the input item (intended mainly for crafting teaching).
     *
     * @param stack The input stack.
     */
    public void setInput(final ItemStack stack)
    {
        handleSlotClick(getSlot(0), stack);
    }

    /**
     * Sets the container (input potion, intended mostly for crafting teaching).
     *
     * @param stack The container stack.
     */
    public void setContainer(final ItemStack stack)
    {
        handleSlotClick(getSlot(1), stack);
        handleSlotClick(getSlot(2), stack);
        handleSlotClick(getSlot(3), stack);
    }

    /**
     * Handle a slot click.
     *
     * @param slot  the clicked slot.
     * @param stack the used stack.
     * @return the result.
     */
    private ItemStack handleSlotClick(final Slot slot, final ItemStack stack)
    {
        if (stack.getCount() > 0)
        {
            final ItemStack copy = stack.copy();
            copy.setCount(1);
            slot.set(copy);
        }
        else if (slot.getStack().getCount() > 0)
        {
            slot.set(null);
        }

        return slot.getStack().copy();
    }

    @Override
    public boolean canInteractWith(@NotNull final EntityPlayer playerIn)
    {
        return true;
    }

    @NotNull
    @Override
    public ItemStack transferStackInSlot(final EntityPlayer playerIn, final int index)
    {
        final Slot slot = this.slots.get(index);
        if (slot != null && slot.getHasStack())
        {
            final ItemStack stack = slot.getStack();
            if (index < 3)
            {
                setContainer(null);
                return null;
            }
            if (index == 3)
            {
                setInput(null);
                return null;
            }

            if (false) { // [1.7.10] BrewingRecipeRegistry not available
                return null;
            }
            else if (false) { // [1.7.10] BrewingRecipeRegistry not available
                return null;
            }
        }

        return null;
    }

    /**
     * Getter for the player.
     *
     * @return the player.
     */
    public EntityPlayer getPlayer()
    {
        return playerInventory.player;
    }

    /**
     * Getter for the world obj.
     *
     * @return the world obj.
     */
    public World getWorldObj()
    {
        return playerInventory.player.World();
    }

    /**
     * Get the position of the container.
     *
     * @return the position.
     */
    public int[] getPos()
    {
        return buildingPos;
    }

    /**
     * Get the module if of the container.
     * @return the module id.
     */
    public int getModuleId()
    {
        return this.moduleId;
    }
}



