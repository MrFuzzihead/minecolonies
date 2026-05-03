package com.minecolonies.api.inventory.container;

import com.minecolonies.api.colony.*;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.entity.ai.workers.util.GuardGear;
import com.minecolonies.api.entity.ai.workers.util.GuardGearBuilder;
import com.minecolonies.api.inventory.InventoryCitizen;
import com.minecolonies.api.inventory.ModContainers;
import com.minecolonies.api.util.ItemStackUtils;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraft.item.ItemStack;
// [1.7.10] items shim in com.minecolonies.api.shim
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.minecolonies.api.util.constant.EquipmentLevelConstants.*;
import static com.minecolonies.api.util.constant.GuardConstants.*;
import static com.minecolonies.api.util.constant.InventoryConstants.*;

/**
 * Container for Mie
 */
public class ContainerCitizenInventory extends Container
{
    /**
     * EntityPlayer inventory.
     */
    private final InventoryPlayer playerInventory;

    /**
     * Amount of rows.
     */
    private final int              inventorySize;

    /**
     * Citizen related data.
     */
    private ICitizen citizenData;

    /**
     * Related entity.
     */
    private Optional<? extends Entity> entity = Optional.empty();
    private       String           displayName;

    /**
     * Deserialize packet buffer to container instance.
     *
     * @param windowId     the id of the window.
     * @param inv          the EntityPlayer inventory.
     * @param packetBuffer network buffer
     * @return new instance
     */
    public static ContainerCitizenInventory fromPacketBuffer(final int windowId, final InventoryPlayer inv, final PacketBuffer packetBuffer)
    {
        final int colonyId = packetBuffer.readVarInt();
        final int citizenId = packetBuffer.readVarInt();
        return new ContainerCitizenInventory(windowId, inv, colonyId, citizenId);
    }

    /**
     * Creating the citizen InventoryPlayer container.
     *
     * @param windowId  the window id.
     * @param inv       the inventory.
     * @param colonyId  colony id
     * @param citizenId citizen id
     */
    public ContainerCitizenInventory(final int windowId, final InventoryPlayer inv, final int colonyId, final int citizenId)
    {
        super();
        this.playerInventory = inv;

        final IColony colony;
        if (inv.player.worldObj.isRemote)
        {
            colony = IColonyManager.getInstance().getColonyView(colonyId, inv.player.worldObj.provider.dimensionId);
        }
        else
        {
            colony = IColonyManager.getInstance().getColonyByWorld(colonyId, inv.player.worldObj);
        }

        if (colony == null)
        {
            inventorySize = 0;
            return;
        }

        final InventoryCitizen inventory;
        final int[] workBuilding;

        int workBuildingLevel = 0;
        if (inv.player.worldObj.isRemote)
        {
            final ICitizenDataView data = ((IColonyView) colony).getCitizen(citizenId);
            this.entity = Optional.of(inv.player.worldObj.getEntity(data.getEntityId()));
            this.citizenData = data;
            inventory = data.getInventory();
            this.displayName = data.getName();
            workBuilding = data.getWorkBuilding();
            if (workBuilding != null)
            {
                workBuildingLevel = colony.getCommonBuildingManager().getBuilding(workBuilding).getBuildingLevel();
            }
        }
        else
        {
            final ICitizenData data;
            if (citizenId > 0)
            {
                data = colony.getCitizenManager().getCivilian(citizenId);
            }
            else
            {
                data = colony.getVisitorManager().getCivilian(citizenId);
            }
            this.entity = data.getEntity();
            this.citizenData = data;

            inventory = data.getInventory();
            this.displayName = data.getName();
            workBuilding = data.getWorkBuilding() == null ? null : data.getWorkBuilding().getID();
            if (workBuilding != null)
            {
                workBuildingLevel = data.getWorkBuilding().getBuildingLevel();
            }
        }

        this.inventorySize = inventory.getSlots() / INVENTORY_COLUMNS;
        final int size = inventory.getSlots();

        final int columns = inventorySize <= INVENTORY_BAR_SIZE ? INVENTORY_COLUMNS : ((size / INVENTORY_BAR_SIZE) + 1);
        final int extraOffset = (inventorySize <= INVENTORY_BAR_SIZE ? 0 : 2) + 1;
        final int newOffset = 5;
        int index = 0;


        List<GuardGear> guardGear = switch (workBuildingLevel)
        {
            case 5-> GuardGearBuilder.buildGearForLevel(ARMOR_LEVEL_IRON, ARMOR_LEVEL_MAX, LEATHER_BUILDING_LEVEL_RANGE, DIA_BUILDING_LEVEL_RANGE);
            case 4-> GuardGearBuilder.buildGearForLevel(ARMOR_LEVEL_CHAIN, ARMOR_LEVEL_DIAMOND, LEATHER_BUILDING_LEVEL_RANGE, DIA_BUILDING_LEVEL_RANGE);
            case 3-> GuardGearBuilder.buildGearForLevel(ARMOR_LEVEL_LEATHER, ARMOR_LEVEL_IRON, LEATHER_BUILDING_LEVEL_RANGE, IRON_BUILDING_LEVEL_RANGE);
            case 2-> GuardGearBuilder.buildGearForLevel(ARMOR_LEVEL_LEATHER, ARMOR_LEVEL_CHAIN, LEATHER_BUILDING_LEVEL_RANGE, CHAIN_BUILDING_LEVEL_RANGE);
            case 1-> GuardGearBuilder.buildGearForLevel(ARMOR_LEVEL_LEATHER, ARMOR_LEVEL_GOLD, LEATHER_BUILDING_LEVEL_RANGE, GOLD_BUILDING_LEVEL_RANGE);
            default-> Collections.emptyList();
        };

        for (int j = 0; j < Math.min(this.inventorySize, INVENTORY_BAR_SIZE); ++j)
        {
            for (int k = 0; k < columns; ++k)
            {
                if (index < size)
                {
                    this.addSlot(
                      new SlotItemHandler(inventory, index,
                        INVENTORY_BAR_SIZE + k * PLAYER_INVENTORY_OFFSET_EACH,
                        newOffset + PLAYER_INVENTORY_OFFSET_EACH + j * PLAYER_INVENTORY_OFFSET_EACH)
                      {
                          @Override
                          public void putStack(@NotNull final ItemStack stack)
                          {
                              if (workBuilding != null && !playerInventory.player.World().isClientSide && !ItemStackUtils.isEmpty(stack))
                              {
                                  final IBuilding building = colony.getServerBuildingManager().getBuilding(workBuilding);
                                  final ICitizenData citizenData = colony.getCitizenManager().getCivilian(citizenId);

                                  building.overruleNextOpenRequestOfCitizenWithStack(citizenData, stack);
                              }
                              super.putStack(stack);
                          }
                      });
                    index++;
                }
            }
        }


        index = 3;
        for (int j = 0; j < 4; ++j)
        {
            // [1.7.10] armor slot index: index counts 3..0 (boots=0, leggings=1, chestplate=2, helmet=3)
            final int armorSlot = index;
            this.addSlot(
              new Slot(new net.minecraft.inventory.InventoryBasic("armor", false, 1), 0, INVENTORY_BAR_SIZE + 215,
                23 + j * PLAYER_INVENTORY_OFFSET_EACH)
              {
                  @Override
                  public void putStack(@NotNull final ItemStack stack)
                  {
                      if (workBuilding != null && !playerInventory.player.worldObj.isRemote && !ItemStackUtils.isEmpty(stack))
                      {
                          final IBuilding building = colony.getServerBuildingManager().getBuilding(workBuilding);
                          final ICitizenData citizenData = colony.getCitizenManager().getCivilian(citizenId);

                          building.overruleNextOpenRequestOfCitizenWithStack(citizenData, stack);
                      }
                      super.putStack(stack);
                      inventory.forceArmorStackToSlot(armorSlot, stack);
                  }

                  @Override
                  public ItemStack decrStackSize(final int slot)
                  {
                      inventory.forceClearArmorInSlot(armorSlot, inventory.getArmorInSlot(armorSlot));
                      return super.decrStackSize(slot);
                  }

                  @Override
                  public boolean isItemValid(final ItemStack stack)
                  {
                      // [1.7.10] ArmorItem.getEquipmentSlot() not available; allow all armor in slot
                      for (final GuardGear gear : guardGear)
                      {
                          if (gear.test(stack))
                          {
                              return true;
                          }
                      }
                      return false;
                  }
              });
            index--;
        }

        // EntityPlayer InventoryPlayer slots
        // Note: The slot numbers are within the EntityPlayer InventoryPlayer and may be the same as the field inventory.
        int i;
        for (i = 0; i < INVENTORY_ROWS; i++)
        {
            for (int j = 0; j < INVENTORY_COLUMNS; j++)
            {
                addSlot(new Slot(
                  playerInventory,
                  j + i * INVENTORY_COLUMNS + INVENTORY_COLUMNS,
                  PLAYER_INVENTORY_INITIAL_X_OFFSET + j * PLAYER_INVENTORY_OFFSET_EACH,
                  PLAYER_INVENTORY_INITIAL_Y_OFFSET + newOffset + extraOffset + PLAYER_INVENTORY_OFFSET_EACH * Math.min(this.inventorySize, INVENTORY_BAR_SIZE)
                    + i * PLAYER_INVENTORY_OFFSET_EACH
                ));
            }
        }

        for (i = 0; i < INVENTORY_COLUMNS; i++)
        {
            addSlot(new Slot(
              playerInventory, i,
              PLAYER_INVENTORY_INITIAL_X_OFFSET + i * PLAYER_INVENTORY_OFFSET_EACH,
              PLAYER_INVENTORY_HOTBAR_OFFSET + newOffset + extraOffset + PLAYER_INVENTORY_OFFSET_EACH * Math.min(this.inventorySize,
                INVENTORY_BAR_SIZE)
            ));
        }
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the EntityPlayer InventoryPlayer and the other inventory(s).
     *
     * @param playerIn EntityPlayer that interacted with this {@code Container}.
     * @param index    Index of the {@link Slot}. This index is relative to the list of slots in this {@code Container}, {@link #slots}.
     */
    @NotNull
    @Override
    public ItemStack transferStackInSlot(final EntityPlayer playerIn, final int index)
    {
        final Slot slot = this.slots.get(index);

        if (slot == null || !slot.hasItem())
        {
            return ItemStackUtils.EMPTY;
        }

        final ItemStack stackCopy = slot.getItem().copy();

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
        }

        return stackCopy;
    }

    /**
     * Determines whether supplied EntityPlayer can use this container
     */
    @Override
    public boolean canInteractWith(@NotNull final EntityPlayer playerIn)
    {
        return true;
    }

    /**
     * Getter for the display name.
     *
     * @return the display name.
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * Get the entity of this container.
     * @return the entity.
     */
    public Optional<? extends Entity> getEntity()
    {
        return this.entity;
    }

    /**
     * @return
     */
    public ICitizen getCitizenData()
    {
        return citizenData;
    }
}






