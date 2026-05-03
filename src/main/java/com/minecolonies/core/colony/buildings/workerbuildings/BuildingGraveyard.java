package com.minecolonies.core.colony.buildings.workerbuildings;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.minecolonies.api.blocks.AbstractBlockMinecoloniesNamedGrave;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.core.tileentities.TileEntityGrave;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.Tuple;
import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Direction -> net.minecraft.util.EnumFacing
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.item.Items;
import net.minecraft.world.World;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.minecolonies.api.util.constant.EquipmentLevelConstants.TOOL_LEVEL_WOOD_OR_GOLD;

/**
 * Class which handles the graveyard building.
 */
public class BuildingGraveyard extends AbstractBuilding
{
    /**
     * Descriptive string of the building.
     */
    private static final String GRAVEYARD = "graveyard";

    /**
     * The maximum building World of the hut.
     */
    private static final int MAX_BUILDING_LEVEL = 5;

    /**
     * NBTTag to store the visual grave positions.
     */
    private static final String TAG_VISUAL_GRAVES = "visualgraves";

    /**
     * NBTTag to store the visual grave positions int[].
     */
    private static final String TAG_VISUAL_GRAVES_BLOCKPOS = "visualgravesblockpos";

    /**
     * NBTTag to store the visual grave facing.
     */
    private static final String TAG_VISUAL_GRAVES_FACING = "visualgravesfacing";

    /**
     * The last field NBTBase.
     */
    private static final String TAG_CURRENT_GRAVE = "currentGRAVE";

    /**
     * The grave the undertaker is currently collecting.
     */
    @Nullable
    private int[] currentGrave;

    /**
     * Grave positions
     */
    private Set<Tuple<int[], Direction>> visualGravePositions = new HashSet<>();

    /**
     * Public constructor which instantiates the building.
     *
     * @param c the colony the building is in.
     * @param l the position it has been placed (it's id).
     */
    public BuildingGraveyard(final IColony c, final int[] l)
    {
        super(c, l);
        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.shovel.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new net.minecraft.util.Tuple<>(1, true));
        keepX.put(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING, new net.minecraft.util.Tuple<>(2, true));
    }

    /**
     * Clear the current grave the undertaker is currently working on.
     */
    public void ClearCurrentGrave()
    {
        this.currentGrave = null;
    }

    /**
     * Retrieves a random grave to work on for the undertaker.
     *
     * @return a field to work on.
     */
    @Nullable
    public int[] getGraveToWorkOn()
    {
        if(currentGrave != null)
        {
            if (WorldUtil.isBlockLoaded(colony.getWorld(), currentGrave))
            {
                final BlockEntity tileEntity = getColony().getWorld().getBlockEntity(currentGrave);
                if (tileEntity instanceof TileEntityGrave)
                {
                    return currentGrave;
                }
            }

            colony.getGraveManager().unReserveGrave(currentGrave);
            currentGrave = null;
        }

        currentGrave = colony.getGraveManager().reserveNextFreeGrave();
        return currentGrave;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);

        if (compound.contains(TAG_CURRENT_GRAVE))
        {
            currentGrave = BlockPosUtil.read(compound, TAG_CURRENT_GRAVE);
        }

        visualGravePositions.clear();
        final NBTTagList visualGraveTagList = compound.getList(TAG_VISUAL_GRAVES, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < visualGraveTagList.size(); ++i)
        {
            final NBTTagCompound graveCompound = visualGraveTagList.getCompound(i);
            final int[] graveLocation = BlockPosUtil.read(graveCompound, TAG_VISUAL_GRAVES_BLOCKPOS);
            final Direction graveFacing = Direction.byName(graveCompound.getString(TAG_VISUAL_GRAVES_FACING));
            visualGravePositions.add(new Tuple<>(graveLocation, graveFacing));
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = super.serializeNBT();

        if (currentGrave != null)
        {
            BlockPosUtil.write(compound, TAG_CURRENT_GRAVE, currentGrave);
        }

        @NotNull final NBTTagList visualGraveTagList = new NBTTagList();
        for (@NotNull final Tuple<int[], Direction> vgp : visualGravePositions)
        {
            @NotNull final NBTTagCompound graveCompound = new NBTTagCompound();
            BlockPosUtil.write(graveCompound, TAG_VISUAL_GRAVES_BLOCKPOS, vgp.getA());
            graveCompound.putString(TAG_VISUAL_GRAVES_FACING, vgp.getB().getName());
            visualGraveTagList.add(graveCompound);
        }
        compound.put(TAG_VISUAL_GRAVES, visualGraveTagList);
        return compound;
    }

    /**
     * Get the set of grave positions.
     * @return the set of positions with their directions.
     */
    public Set<Tuple<int[], Direction>> getGravePositions()
    {
        return visualGravePositions;
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return GRAVEYARD;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return MAX_BUILDING_LEVEL;
    }

    @Override
    public void registerBlockPosition(@NotNull final BlockState state, @NotNull final int[] pos, @NotNull final World world)
    {
        super.registerBlockPosition(state, pos, world);
        if (state.getBlock() == ModBlocks.blockNamedGrave)
        {
            visualGravePositions.add(new Tuple<>(pos, state.getValue(AbstractBlockMinecoloniesNamedGrave.FACING)));
        }
    }

    /**
     * Return a random free visual grave position
     */
    public Tuple<int[], Direction> getRandomFreeVisualGravePos()
    {
        if (visualGravePositions.isEmpty())
        {
            return null;
        }

        final List<Tuple<int[], Direction>> availablePos = new ArrayList<Tuple<int[], Direction>>();
        for(final Tuple<int[], Direction> tuple : visualGravePositions)
        {
            if (getColony().getWorld().getBlockState(tuple.getA()).canBeReplaced())
            {
                availablePos.add(tuple);
            }
        }

        if (availablePos.isEmpty())
        {
            return null;
        }

        Collections.shuffle(availablePos);
        return availablePos.get(0);
    }
}





