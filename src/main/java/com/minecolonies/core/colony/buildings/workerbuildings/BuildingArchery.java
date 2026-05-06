package com.minecolonies.core.colony.buildings.workerbuildings;
import net.minecraft.util.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.chunk.LevelChunk;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.NBTUtils;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Building class for the Archery.
 */
public class BuildingArchery extends AbstractBuilding
{
    /**
     * The Schematic name.
     */
    private static final String SCHEMATIC_NAME = "archery";

    /**
     * List of shooting stands in the building.
     */
    private final List<int[]> shootingStands = new ArrayList<>();

    /**
     * List of shooting targets in the building.
     */
    private final List<int[]> shootingTargets = new ArrayList<>();

    /**
     * The abstract constructor of the building.
     *
     * @param c the colony
     * @param l the position
     */
    public BuildingArchery(@NotNull final IColony c, final int[] l)
    {
        super(c, l);
    }


    @Override
    public void registerBlockPosition(@NotNull final Block block, @NotNull final int[] pos, @NotNull final World world)
    {
        if (block == Blocks.TARGET)
        {
            shootingTargets.add(pos);
        }
        else if (block == Blocks.GLOWSTONE)
        {
            shootingStands.add(pos);
        }
        super.registerBlockPosition(block, pos, world);
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);
        shootingTargets.clear();
        shootingStands.clear();

        final NBTTagList targetList = compound.getTagList(TAG_ARCHERY_TARGETS, NBTBase.TAG_COMPOUND);
        shootingTargets.addAll(NBTUtils.streamCompound(targetList).map(targetCompound -> BlockPosUtil.read(targetCompound, TAG_TARGET)).collect(Collectors.toList()));

        final NBTTagList standTagList = compound.getTagList(TAG_ARCHERY_STANDS, NBTBase.TAG_COMPOUND);
        shootingStands.addAll(NBTUtils.streamCompound(standTagList).map(targetCompound -> BlockPosUtil.read(targetCompound, TAG_STAND)).collect(Collectors.toList()));
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = super.serializeNBT();

        final NBTTagList targetList = shootingTargets.stream().map(target -> BlockPosUtil.write(new NBTTagCompound(), TAG_TARGET, target)).collect(NBTUtils.toListNBT());
        compound.setTag(TAG_ARCHERY_TARGETS, targetList);

        final NBTTagList standTagList = shootingStands.stream().map(target -> BlockPosUtil.write(new NBTTagCompound(), TAG_STAND, target)).collect(NBTUtils.toListNBT());
        compound.setTag(TAG_ARCHERY_STANDS, standTagList);

        return compound;
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return SCHEMATIC_NAME;
    }

    /**
     * Get a random position to shoot from.
     *
     * @param random the random obj.
     * @return a random shooting stand position.
     */
    public int[] getRandomShootingStandPosition(final RandomSource random)
    {
        final List<int[]> tagged = getLocationsFromTag(TAG_WORK);
        if (!tagged.isEmpty())
        {
            return tagged.get(random.nextInt(tagged.size()));
        }
        if (!shootingStands.isEmpty())
        {
            return shootingStands.get(random.nextInt(shootingStands.size()));
        }
        return null;
    }

    /**
     * Get a random position to shoot at.
     *
     * @param random the random obj.
     * @return a random shooting target position.
     */
    public int[] getRandomShootingTarget(final RandomSource random)
    {
        if (!shootingTargets.isEmpty())
        {
            return shootingTargets.get(random.nextInt(shootingTargets.size()));
        }
        return null;
    }
}





