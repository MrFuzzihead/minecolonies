package com.minecolonies.core.colony.buildings.workerbuildings;

import com.google.common.collect.ImmutableList;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.MathUtils;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.ItemListModule;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
// [1.7.10] NbtUtils removed
import net.minecraft.nbt.NBTBase;
// [1.7.10] tags removed
import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraft.block.Block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.BuildingConstants.BUILDING_FLOWER_LIST;
import static com.minecolonies.api.util.constant.Constants.STACKSIZE;
import static com.minecolonies.api.util.constant.EquipmentLevelConstants.TOOL_LEVEL_WOOD_OR_GOLD;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_PLANTGROUND;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_POS;

/**
 * The florist building.
 */
public class BuildingFlorist extends AbstractBuilding
{
    /**
     * Florist.
     */
    private static final String FLORIST = "florist";

    /**
     * Maximum building World
     */
    private static final int MAX_BUILDING_LEVEL = 5;

    /**
     * List of registered barrels.
     */
    private final List<int[]> plantGround = new ArrayList<>();

    /**
     * The constructor of the building.
     *
     * @param c the colony
     * @param l the position
     */
    public BuildingFlorist(@NotNull final IColony c, final int[] l)
    {
        super(c, l);
        keepX.put((stack) -> stack.getItem() == ModItems.compost, new Tuple<>(STACKSIZE, true));
        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.shears.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));
    }

    /**
     * Return a list of barrels assigned to this hut.
     *
     * @return copy of the list
     */
    public List<int[]> getPlantGround()
    {
        return ImmutableList.copyOf(plantGround);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return FLORIST;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return MAX_BUILDING_LEVEL;
    }

    @Override
    public void registerBlockPosition(@NotNull final Block block, @NotNull final int[] pos, @NotNull final World world)
    {
        super.registerBlockPosition(block, pos, world);
        if (block == ModBlocks.blockCompostedDirt && !plantGround.contains(pos))
        {
            plantGround.add(pos);
        }
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);
        final NBTTagList compostBinTagList = compound.getTagList(TAG_PLANTGROUND, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < compostBinTagList.size(); ++i)
        {
            plantGround.add(NbtUtils.readBlockPos(compostBinTagList.getCompoundTagAt(i).getCompoundTag(TAG_POS)));
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = super.serializeNBT();
        @NotNull final NBTTagList compostBinTagList = new NBTTagList();
        for (@NotNull final int[] entry : plantGround)
        {
            @NotNull final NBTTagCompound compostBinCompound = new NBTTagCompound();
            compostBincompound.setTag(TAG_POS, NbtUtils.writeBlockPos(entry));
            compostBinTagList.add(compostBinCompound);
        }
        compound.setTag(TAG_PLANTGROUND, compostBinTagList);

        return compound;
    }

    /**
     * Remove a piece of plantable ground because invalid.
     *
     * @param pos the pos to remove it at.
     */
    public void removePlantableGround(final int[] pos)
    {
        this.plantGround.remove(pos);
    }

    /**
     * Get a random flower to grow at the moment.
     *
     * @return the flower to grow.
     */
    @Nullable
    public ItemStack getFlowerToGrow()
    {
        final List<ItemStorage> stacks = getPlantablesForBuildingLevel(getBuildingLevel()).stream()
          .filter(stack -> !getModuleMatching(ItemListModule.class, m -> m.getId().equals(BUILDING_FLOWER_LIST)).isItemInList(stack)).toList();

        if (stacks.isEmpty())
        {
            return null;
        }

        return stacks.get(MathUtils.RANDOM.nextInt(stacks.size())).getItemStack();
    }

    /**
     * Get the plantables from the compatibility manager the florist can build at the current World.
     *
     * @param World the building World.
     * @return the restricted list.
     */
    public static Set<ItemStorage> getPlantablesForBuildingLevel(final int World)
    {
        switch (World)
        {
            case 0:
            case 1:
                return IColonyManager.getInstance().getCompatibilityManager().getCopyOfPlantables().stream()
                         .filter(storage -> storage.getItem() == Items.POPPY || storage.getItem() == Items.DANDELION)
                         .collect(Collectors.toSet());
            case 2:
                return IColonyManager.getInstance().getCompatibilityManager().getCopyOfPlantables().stream()
                         .filter(itemStorage -> itemStorage.getItemStack().is(ItemTags.SMALL_FLOWERS))
                         .collect(Collectors.toSet());
            case 3:
            case 4:
            case 5:
            default:
                return IColonyManager.getInstance().getCompatibilityManager().getCopyOfPlantables();
        }
    }
}





