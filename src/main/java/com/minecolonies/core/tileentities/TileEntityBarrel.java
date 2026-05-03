package com.minecolonies.core.tileentities;

import com.minecolonies.api.blocks.AbstractBlockBarrel;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.crafting.CompostRecipe;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.tileentities.AbstractTileEntityBarrel;
import com.minecolonies.api.tileentities.ITickable;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.WorldUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class TileEntityBarrel extends AbstractTileEntityBarrel implements ITickable
{
    /**
     * True if the barrel has finished composting and the items are ready to harvest.
     */
    private boolean done = false;

    /**
     * The number of items that the barrel contains.
     */
    private int items = 0;

    /**
     * The timer for the composting process.
     */
    private int timer = 0;

    /**
     * The number the timer has to reach to finish composting. Number of Minecraft ticks in 2 whole days.
     */
    private static final int TIMER_END = 24000;

    /**
     * The average of ticks that passes between actually ticking the tileEntity.
     */
    private static final int AVERAGE_TICKS = 20;

    public TileEntityBarrel()
    {
        super();
    }

    @Override
    public void updateEntity()
    {
        // In 1.7.10, TEs tick via updateEntity() — delegate to tick()
        tick();
    }

    /**
     * Update method called per-tick.
     */
    @Override
    public void tick()
    {
        if (!worldObj.isRemote && (worldObj.getTotalWorldTime() % (worldObj.rand.nextInt(AVERAGE_TICKS * 2) + 1) == 0))
        {
            this.updateTick();
        }
    }

    /**
     * Method that does compost ticks or spawns particles if finished.
     */
    public void updateTick()
    {
        if (getItems() == AbstractTileEntityBarrel.MAX_ITEMS)
        {
            doBarrelCompostTick();
        }
        if (this.done)
        {
            // TODO: spawn happy_villager particle equivalent in 1.7.10
            // worldObj.spawnParticle("happyVillager", xCoord + 0.5, yCoord + 1.5, zCoord + 0.5, 0.2, 0, 0.2);
        }
    }

    private void doBarrelCompostTick()
    {
        timer++;
        if (timer >= TIMER_END / AVERAGE_TICKS)
        {
            timer = 0;
            items = 0;
            done = true;
            this.updateBlock();
        }
    }

    /**
     * Method called when a player uses the block. Takes the needed items from the player.
     *
     * @param playerIn  the player.
     * @param itemstack the itemStack in the player's hand.
     * @param hitFace   the side of the barrel the player hit (0-5), or -1 for direct insert.
     * @return if the barrel took any item.
     */
    public boolean useBarrel(final EntityPlayer playerIn, final ItemStack itemstack, final int hitFace)
    {
        if (done)
        {
            final ItemStack compostStack = new ItemStack(ModItems.compost, 6);
            if (hitFace >= 0) // Spawn as EntityItem
            {
                final EntityItem ent = new EntityItem(playerIn.worldObj, xCoord + 0.5, yCoord + 1.75, zCoord + 0.5, compostStack);
                playerIn.worldObj.spawnEntityInWorld(ent);
            }
            else // Insert directly into inventory
            {
                if (!playerIn.inventory.addItemStackToInventory(compostStack))
                {
                    final EntityItem ent = new EntityItem(playerIn.worldObj, xCoord + 0.5, yCoord + 1.75, zCoord + 0.5, compostStack);
                    playerIn.worldObj.spawnEntityInWorld(ent);
                }
            }
            // TODO: play sound equivalent in 1.7.10
            done = false;
            return true;
        }

        final CompostRecipe recipe = findCompostRecipe(itemstack);
        if (recipe == null)
        {
            return false;
        }

        if (items == AbstractTileEntityBarrel.MAX_ITEMS)
        {
            MessageUtils.format("entity.barrel.working").sendTo(playerIn);
            return false;
        }
        else
        {
            this.consumeNeededItems(itemstack, recipe);
            return true;
        }
    }

    private void consumeNeededItems(final ItemStack itemStack, final CompostRecipe recipe)
    {
        final int factor = recipe.getStrength();
        final int availableItems = itemStack.stackSize * factor;
        final int neededItems = AbstractTileEntityBarrel.MAX_ITEMS - items;
        int itemsToRemove = Math.min(neededItems, availableItems);

        this.items += itemsToRemove;
        itemsToRemove /= factor;
        ItemStackUtils.changeSize(itemStack, -itemsToRemove);
    }

    @Nullable
    private static CompostRecipe findCompostRecipe(final ItemStack itemStack)
    {
        return IColonyManager.getInstance().getCompatibilityManager()
          .getCopyOfCompostRecipes().get(itemStack.getItem());
    }

    /**
     * Updates the block appearance between the server and the client.
     */
    public void updateBlock()
    {
        if (worldObj.getBlock(xCoord, yCoord, zCoord) == ModBlocks.blockBarrel)
        {
            AbstractBlockBarrel.changeStateOverFullness(this, worldObj, xCoord, yCoord, zCoord);
            markDirty();
        }
    }

    @Override
    public void writeToNBT(final NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("items", this.items);
        compound.setInteger("timer", this.timer);
        compound.setBoolean("done", this.done);
    }

    @Override
    public void readFromNBT(final NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.items = compound.getInteger("items");
        this.timer = compound.getInteger("timer");
        this.done = compound.getBoolean("done");
    }

    @Override
    public void markDirty()
    {
        if (worldObj != null)
        {
            WorldUtil.markChunkDirty(worldObj, xCoord, yCoord, zCoord);
        }
    }

    @Override
    public int getItems()
    {
        return items;
    }

    @Override
    public boolean isDone()
    {
        return this.done;
    }

    @Override
    public boolean checkIfWorking()
    {
        return this.items == MAX_ITEMS;
    }

    @Override
    public boolean addItem(final ItemStack item)
    {
        final CompostRecipe recipe = findCompostRecipe(item);
        if (recipe != null && this.items < MAX_ITEMS)
        {
            this.consumeNeededItems(item, recipe);
            this.updateBlock();
            return true;
        }
        return false;
    }

    @Override
    public ItemStack retrieveCompost(final double multiplier)
    {
        if (this.done)
        {
            this.done = false;
            this.updateBlock();
            return new ItemStack(ModItems.compost, (int) (6 * multiplier));
        }
        return null; // null = empty in 1.7.10
    }
}
