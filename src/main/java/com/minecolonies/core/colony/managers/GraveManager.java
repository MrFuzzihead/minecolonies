package com.minecolonies.core.colony.managers;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.ldtteam.structurize.util.BlockUtils;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.GraveData;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.managers.interfaces.IGraveManager;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.core.blocks.BlockMinecoloniesGrave;
import com.minecolonies.core.colony.Colony;
import com.minecolonies.core.tileentities.TileEntityGrave;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.IChatComponent;
// [1.7.10] world.entity removed
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.init.Blocks;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.TickRateConstants.MAX_TICKRATE;
import static com.minecolonies.api.research.util.ResearchConstants.GRAVE_DECAY_BONUS;
import static com.minecolonies.api.util.constant.NbtTagConstants.*;
import static com.minecolonies.api.util.constant.TranslationConstants.WARNING_GRAVE_LAVA;
import static com.minecolonies.api.util.constant.TranslationConstants.WARNING_GRAVE_WATER;

public class GraveManager implements IGraveManager
{
    /**
     * List of grave in the colony.
     */
    @NotNull
    private final Map<int[], Boolean> graves = new HashMap<>();

    /**
     * The colony of the manager.
     */
    private final Colony colony;

    /**
     * Creates the GraveManager for a colony.
     *
     * @param colony the colony.
     */
    public GraveManager(final Colony colony)
    {
        this.colony = colony;
    }

    /**
     * Read the graves from NBT.
     *
     * @param compound the compound.
     */
    @Override
    public void read(@NotNull final NBTTagCompound compound)
    {
        graves.clear();
        final NBTTagList gravesTagList = compound.getList(TAG_GRAVE, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < gravesTagList.size(); ++i)
        {
            final NBTTagCompound graveCompound = gravesTagList.getCompound(i);
            if (graveCompound.contains(TAG_POS) && graveCompound.contains(TAG_RESERVED))
            {
                graves.put(BlockPosUtil.read(graveCompound, TAG_POS), graveCompound.getBoolean(TAG_RESERVED));
            }
        }
    }

    /**
     * Write the graves to NBT.
     *
     * @param compound the compound.
     */
    @Override
    public void write(@NotNull final NBTTagCompound compound)
    {
        @NotNull final NBTTagList gravesTagList = new NBTTagList();
        for (@NotNull final int[] blockPos : graves.keySet())
        {
            @NotNull final NBTTagCompound graveCompound = new NBTTagCompound();
            BlockPosUtil.write(graveCompound, TAG_POS, blockPos);
            graveCompound.putBoolean(TAG_RESERVED, graves.get(blockPos));
            gravesTagList.add(graveCompound);
        }
        compound.put(TAG_GRAVE, gravesTagList);
    }

    /**
     * Ticks all grave when this building manager receives a tick.
     *
     * @param colony the colony which is being ticked.
     */
    @Override
    public void onColonyTick(final IColony colony)
    {
        for (final Iterator<int[]> iterator = graves.keySet().iterator(); iterator.hasNext(); )
        {
            final int[] pos = iterator.next();
            if (!WorldUtil.isBlockLoaded(colony.getWorld(), pos))
            {
                continue;
            }

            final BlockEntity graveEntity = colony.getWorld().getBlockEntity(pos);
            if (!(graveEntity instanceof TileEntityGrave))
            {
                iterator.remove();
                colony.markDirty();
                continue;
            }

            if (!((TileEntityGrave) graveEntity).onColonyTick(MAX_TICKRATE))
            {
                iterator.remove();
                colony.markDirty();
            }
        }
    }

    /**
     * Returns a map with all graves within the colony. Key is ID (Coordinates), value is isReserved boolean.
     *
     * @return Map with ID (coordinates) as key, value is isReserved boolean.
     */
    @NotNull
    @Override
    public Map<int[], Boolean> getGraves()
    {
        return graves;
    }

    /**
     * Add a grave from the Colony.
     *
     * @param pos position of the TileEntityGrave to add.
     * @return the grave that was created and added.
     */
    @Override
    public boolean addNewGrave(@NotNull final int[] pos)
    {
        final TileEntityGrave graveEntity = (TileEntityGrave) colony.getWorld().getBlockEntity(pos);
        if (graveEntity == null)
        {
            return false;
        }

        if (graves.containsKey(pos))
        {
            return true;
        }

        graves.put(pos, false);
        colony.markDirty();
        return true;
    }

    /**
     * Remove a TileEntityGrave from the Colony (when it is destroyed).
     *
     * @param pos position of the TileEntityGrave to remove.
     */
    @Override
    public void removeGrave(@NotNull final int[] pos)
    {
        graves.remove(pos);
        colony.markDirty();
    }

    /**
     * Reserve a grave
     *
     * @param pos the id of the grave.
     * @return is the grave successfully reserved.
     */
    @Override
    public boolean reserveGrave(@NotNull final int[] pos)
    {
        if (!graves.containsKey(pos) || graves.get(pos))
        {
            return false;
        }

        graves.put(pos, true);
        colony.markDirty();
        return true;
    }

    @Override
    public void unReserveGrave(@NotNull final int[] pos)
    {
        if (graves.containsKey(pos) && graves.get(pos))
        {
            graves.put(pos, false);
            colony.markDirty();
        }
    }

    /**
     * Reserve the next free grave
     *
     * @return the grave successfully reserved or null if none available
     */
    @Override
    public int[] reserveNextFreeGrave()
    {
        for (@NotNull final int[] pos : new ArrayList<>(graves.keySet()))
        {
            if (!WorldUtil.isBlockLoaded(colony.getWorld(), pos))
            {
                continue;
            }

            final BlockEntity graveEntity = colony.getWorld().getBlockEntity(pos);
            if (!(graveEntity instanceof TileEntityGrave))
            {
                graves.remove(pos);
                continue;
            }

            if (reserveGrave(pos))
            {
                return pos;
            }
        }

        return null;
    }

    /**
     * Attempt to create a TileEntityGrave at @pos containing the specific @citizenData
     * <p>
     * On failure: drop all the citizen inventory on the ground.
     *
     * @param world       The world.
     * @param pos         The position where to spawn a grave
     * @param citizenData The citizenData
     */
    @Override
    public int[] createCitizenGrave(final World world, final int[] pos, final ICitizenData citizenData)
    {
        final BlockState here = world.getBlockState(pos);
        if (here.getBlock() == Blocks.LAVA)
        {
            MessageUtils.format(WARNING_GRAVE_LAVA).sendTo(colony).forManagers();
            return null;
        }

        int[] firstValidPosition = null;
        if (here.getBlock() == Blocks.WATER)
        {
            for (int i = 1; i <= 10; i++)
            {
                if (world.getBlockState(pos.above(i)).getBlock() instanceof AirBlock)
                {
                    firstValidPosition = BlockPosUtil.findAround(world, pos, 1, 16,
                      (blockAccess, current) ->
                        blockAccess.getBlockState(current).isAir() &&
                          BlockUtils.isAnySolid(blockAccess.getBlockState(current.below())));
                    break;
                }
            }

            if (firstValidPosition == null)
            {
                MessageUtils.format(WARNING_GRAVE_WATER).sendTo(colony).forManagers();
            }
        }
        else
        {
            firstValidPosition = BlockPosUtil.findAround(world, pos, 10, 10,
              (blockAccess, current) ->
                blockAccess.getBlockState(current).isAir() &&
                  BlockUtils.isAnySolid(blockAccess.getBlockState(current.below())));
        }


        if (firstValidPosition != null)
        {
            world.setBlockAndUpdate(firstValidPosition,
              BlockMinecoloniesGrave.getPlacementState(ModBlocks.blockGrave.defaultBlockState(), firstValidPosition));
            final TileEntityGrave graveEntity = (TileEntityGrave) world.getBlockEntity(firstValidPosition);
            if (!InventoryUtils.transferAllItemHandler(citizenData.getInventory(), graveEntity.getInventory()))
            {
                InventoryUtils.dropItemHandler(citizenData.getInventory(), world, pos.getX(), pos.getY(), pos.getZ());
            }
            // [1.7.10] iterate armor slots 0-3
            for (int slot = 0; slot < 4; slot++)
            {
                final ItemStack stack = citizenData.getInventory().getArmorInSlot(slot);
                if (!InventoryUtils.addItemStackToItemHandler(graveEntity.getInventory(), stack))
                {
                    InventoryUtils.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }


            graveEntity.delayDecayTimer(colony.getResearchManager().getResearchEffects().getEffectStrength(GRAVE_DECAY_BONUS));

            GraveData graveData = new GraveData();
            graveData.setCitizenName(citizenData.getName());
            if (citizenData.getJob() != null)
            {
                final String jobName = String.translatable(citizenData.getJob().getJobRegistryEntry().getTranslationKey().toLowerCase());
                graveData.setCitizenJobName(jobName.getString());
            }
            graveData.setCitizenDataNBT(citizenData.serializeNBT());
            graveEntity.setGraveData(graveData);

            addNewGrave(firstValidPosition);
            return firstValidPosition;
        }
        else
        {
            InventoryUtils.dropItemHandler(citizenData.getInventory(), world, pos.getX(), pos.getY(), pos.getZ());
        }
        return null;
    }
}








