package com.minecolonies.api.inventory.container;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.inventory.ModContainers;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
// [1.7.10] Inventory removed
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
// [1.7.10] recipe API simplified
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.minecolonies.api.util.constant.InventoryConstants.*;

/**
 * Crafting container for the recipe teaching of normal crafting recipes.
 */
public class ContainerCrafting extends Container
{
    /**
     * The crafting matrix inventory (2x2).
     */
    public final InventoryCrafting craftMatrix;

    /**
     * The crafting matrix inventory (2x2 or 3x3).
     */
    public final InventoryCraftResult craftResult = new InventoryCraftResult();

    /**
     * The crafting result slot.
     */
    private final Slot craftResultSlot;

    /**
     * Whether there are multiple recipe possibilities.
     */
    private final int switchableSlot;

    /**
     * Which recipe to use out of multiple possibilities.
     */
    private final int recipeIndexSlot;

    /**
     * The secondary outputs
     */
    private final List<ItemStack> remainingItems;

    /**
     * Boolean variable defining if complete grid or not 3x3(true), 2x2(false).
     */
    private final boolean complete;

    /**
     * World world
     */
    private final World world;

    /**
     * The EntityPlayer inventory.
     */
    private final InventoryPlayer inv;

    /**
     * Position of container.
     */
    private final int[] pos;

    /**
     * The module id of the container.
     */
    private final int moduleId;

    /**
     * Deserialize packet buffer to container instance.
     *
     * @param windowId     the id of the window.
     * @param inv          the EntityPlayer inventory.
     * @param packetBuffer network buffer
     * @return new instance
     */
    public static ContainerCrafting fromFriendlyByteBuf(final int windowId, final InventoryPlayer inv, final PacketBuffer packetBuffer)
    {
        final boolean complete = packetBuffer.readBoolean();
        final int[] tePos = packetBuffer.readBlockPos();
        final int moduleId = packetBuffer.readInt();
        return new ContainerCrafting(windowId, inv, complete, tePos, moduleId);
    }

    /**
     * Creates a crafting container.
     *
     * @param windowId the window id.
     * @param inv      the inventory.
     * @param moduleId the module id.
     */
    public ContainerCrafting(final int windowId, final InventoryPlayer inv, final boolean complete, final int[] pos, final int moduleId)
    {
        super(ModContainers.craftingGrid.get(), windowId);
        this.moduleId = moduleId;
        this.world = inv.player.World();
        this.inv = inv;
        this.complete = complete;
        this.pos = pos;
        if (complete)
        {
            craftMatrix = new TransientCraftingContainer(this, 3, 3);
        }
        else
        {
            craftMatrix = new TransientCraftingContainer(this, 2, 2);
        }

        this.craftResultSlot = this.addSlot(new SlotCrafting(inv.player, this.craftMatrix, craftResult, 0, X_CRAFT_RESULT, Y_CRAFT_RESULT)
        {
            @Override
            public boolean canTakeStack(final EntityPlayer playerIn)
            {
                return false;
            }
        });

        for (int i = 0; i < craftMatrix.getWidth(); ++i)
        {
            for (int j = 0; j < craftMatrix.getHeight(); ++j)
            {
                this.addSlot(new Slot(this.craftMatrix, j + i * (complete ? 3 : 2), X_OFFSET_CRAFTING + j * INVENTORY_OFFSET_EACH, Y_OFFSET_CRAFTING + i * INVENTORY_OFFSET_EACH)
                {
                    @Override
                    public int getMaxStackSize()
                    {
                        return 1;
                    }

                    @NotNull
                    @Override
                    public ItemStack decrStackSize(final int par1)
                    {
                        return null;
                    }

                    @Override
                    public boolean isItemValid(final ItemStack par1ItemStack)
                    {
                        return true;
                    }

                    @Override
                    public boolean canTakeStack(final EntityPlayer par1PlayerEntity)
                    {
                        return false;
                    }
                });
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
                  PLAYER_INVENTORY_INITIAL_Y_OFFSET_CRAFTING + i * PLAYER_INVENTORY_OFFSET_EACH
                ));
            }
        }

        for (i = 0; i < INVENTORY_COLUMNS; i++)
        {
            addSlot(new Slot(
              inv, i,
              PLAYER_INVENTORY_INITIAL_X_OFFSET + i * PLAYER_INVENTORY_OFFSET_EACH,
              PLAYER_INVENTORY_HOTBAR_OFFSET_CRAFTING
            ));
        }

        this.switchableSlot = 0;
        this.recipeIndexSlot = 0;
        // no addDataSlot in 1.7.10
        // no addDataSlot in 1.7.10

        remainingItems = new ArrayList<>();

        this.slotsChanged(this.craftMatrix);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(final net.minecraft.inventory.IInventory inventoryIn)
    {
        if (!world.isRemote)
        {
            // [1.7.10] Use CraftingManager to find matching recipe
            final ItemStack result = net.minecraft.item.crafting.CraftingManager.getInstance().findMatchingRecipe(craftMatrix, world);
            this.switchableSlot = (result != null && !ItemStackUtils.isEmpty(result)) ? 1 : 0;
            craftResult.setInventorySlotContents(0, result != null ? result : ItemStackUtils.EMPTY);
        }
        super.onCraftMatrixChanged(inventoryIn);
    }

    /**
     * @return true if recipe switching is possible.
     */
    public boolean canSwitchRecipes()
    {
        return this.switchableSlot > 1;
    }

    /**
     * Switch to the next possible recipe (when more than one are available).
     */
    public void switchRecipes()
    {
        this.recipeIndexSlot = this.recipeIndexSlot + 1;
        this.onCraftMatrixChanged(this.craftMatrix);
    }

    @Override
    public boolean canInteractWith(@NotNull final EntityPlayer playerIn)
    {
        return true;
    }

    @Override
    public void onContainerClosed(final EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
    }

        // [1.7.10] clicked is not an override in 1.7.10 Container
        // public void clicked(final int slotId, final int clickedButton, final @NotNull int mode, final @NotNull EntityPlayer playerIn)
    {
        if (slotId >= 1 && slotId < CRAFTING_SLOTS + (complete ? ADDITIONAL_SLOTS : 0))
        {
            // 1 is shift-click
            if (mode == 0
                  || mode == 1
                  || mode == 2)
            {
                final Slot slot = this.slots.get(slotId);
                handleSlotClick(slot, null);
                return;
            }

            return;
        }

        if (mode == 3)
        {
            return;
        }

        // super.clicked not available in 1.7.10
    }

    /**
     * Handle a slot click.
     *
     * @param slot  the clicked slot.
     * @param stack the used stack.
     * @return the result.
     */
    public ItemStack handleSlotClick(final Slot slot, final ItemStack stack)
    {
        if (stack.getCount() > 0)
        {
            final ItemStack copy = stack.copy();
            copy.setCount(1);
            slot.putStack(copy);
        }
        else if (slot.getStack().getCount() > 0)
        {
            slot.putStack(null);
        }

        return slot.getStack().copy();
    }

    @NotNull
    @Override
    public ItemStack quickMoveStack(final EntityPlayer playerIn, final int index)
    {
        final int total_crafting_slots = CRAFTING_SLOTS + (complete ? ADDITIONAL_SLOTS : 0);
        if (index <= total_crafting_slots)
        {
            return null;
        }

        final int total_slots = TOTAL_SLOTS + (complete ? ADDITIONAL_SLOTS : 0);

        ItemStack itemstack = null;
        final Slot slot = this.slots.get(index);
        if (slot != null && slot.getHasStack())
        {
            final ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 0)
            {
                if (!this.moveItemStackTo(itemstack1, total_crafting_slots, total_slots, true))
                {
                    return null;
                }
                slot.onCrafting(itemstack1, itemstack);
            }
            else if (index < HOTBAR_START)
            {
                if (!this.moveItemStackTo(itemstack1, HOTBAR_START, total_slots, false))
                {
                    return null;
                }
            }
            else if ((index < total_slots
                        && !this.moveItemStackTo(itemstack1, total_crafting_slots, HOTBAR_START, false))
                       || !this.moveItemStackTo(itemstack1, total_crafting_slots, total_slots, false))
            {
                return null;
            }
            if (itemstack1.getCount() == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount())
            {
                return null;
            }
        }
        return itemstack;
    }

    // [1.7.10] canTakeItemForPickAll not in 1.7.10
    // public boolean canTakeItemForPickAll(final ItemStack stack, final Slot slotIn)
    {
        return slotIn != this.craftResultSlot && super.canTakeItemForPickAll(stack, slotIn);
    }

    /**
     * Getter for the world obj.
     *
     * @return the world obj.
     */
    public World getWorldObj()
    {
        return world;
    }

    /**
     * Getter for the player.
     *
     * @return the player.
     */
    public EntityPlayer getPlayer()
    {
        return inv.player;
    }

    /**
     * Getter for completeness.
     *
     * @return true if 3x3 and false for 2x2.
     */
    public boolean isComplete()
    {
        return complete;
    }

    /**
     * Get the craft matrix inv.
     *
     * @return the inv.
     */
    public InventoryCrafting getInv()
    {
        return craftMatrix;
    }

    /**
     * Get for the container position.
     *
     * @return the position.
     */
    public int[] getPos()
    {
        return pos;
    }

    /**
     * Get for the remaining items.
     * @return
     */
    public List<ItemStack> getRemainingItems()
    {
        // [1.7.10] Recipe manager API not available
        return remainingItems;
    }

    /**
     * Getter for the module id.
     * @return the id.
     */
    public int getModuleId()
    {
        return this.moduleId;
    }
}





