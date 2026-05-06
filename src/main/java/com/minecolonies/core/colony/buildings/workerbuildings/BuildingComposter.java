package com.minecolonies.core.colony.buildings.workerbuildings;

import com.google.common.collect.ImmutableList;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.ItemListModule;
import com.minecolonies.core.colony.buildings.modules.settings.BoolSetting;
import com.minecolonies.core.colony.buildings.modules.settings.IntSetting;
import com.minecolonies.core.colony.buildings.modules.settings.SettingKey;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
// [1.7.10] NbtUtils removed
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import com.minecolonies.api.util.Tuple;
import net.minecraft.world.World;
import net.minecraft.block.Block;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.minecolonies.core.entity.ai.workers.production.agriculture.EntityAIWorkComposter.COMPOSTABLE_LIST;

public class BuildingComposter extends AbstractBuilding
{
    /**
     * Settings key for the composting mode.
     */
    public static final ISettingKey<BoolSetting> PRODUCE_DIRT = new SettingKey<>(BoolSetting.class, new ResourceLocation(com.minecolonies.api.util.constant.Constants.MOD_ID, "producedirt"));

    /**
     * Key for min remainder at warehouse.
     */
    public static final ISettingKey<IntSetting> MIN = new SettingKey<>(IntSetting.class, new ResourceLocation(com.minecolonies.api.util.constant.Constants.MOD_ID, "warehousemin"));

    /**
     * Description of the job for this building
     */
    private static final String COMPOSTER = "composter";

    /**
     * Maximum building World
     */
    private static final int MAX_BUILDING_LEVEL = 5;

    /**
     * NBTBase to store the barrel position.
     */
    private static final String TAG_POS = "pos";

    /**
     * NBTBase to store the barrel list.
     */
    private static final String TAG_BARRELS = "barrels";

    /**
     * List of registered barrels.
     */
    private final List<int[]> barrels = new ArrayList<>();

    /**
     * The constructor of the building.
     *
     * @param c the colony
     * @param l the position
     */
    public BuildingComposter(@NotNull final IColony c, final int[] l)
    {
        super(c, l);
        keepX.put((stack) -> this.getModuleMatching(ItemListModule.class, m -> m.getId().equals(COMPOSTABLE_LIST)).isItemInList(new ItemStorage(stack)), new Tuple<>(Integer.MAX_VALUE, true));
    }

    /**
     * Return a list of barrels assigned to this hut.
     *
     * @return copy of the list
     */
    public List<int[]> getBarrels()
    {
        return ImmutableList.copyOf(barrels);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return COMPOSTER;
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
        if (block == ModBlocks.blockBarrel && !barrels.contains(pos))
        {
            barrels.add(pos);
        }
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);
        final NBTTagList compostBinTagList = compound.getTagList(TAG_BARRELS, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < compostBinTagList.size(); ++i)
        {
            barrels.add(NbtUtils.readBlockPos(compostBinTagList.getCompoundTagAt(i).getCompoundTag(TAG_POS)));
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = super.serializeNBT();
        @NotNull final NBTTagList compostBinTagList = new NBTTagList();
        for (@NotNull final int[] entry : barrels)
        {
            @NotNull final NBTTagCompound compostBinCompound = new NBTTagCompound();
            compostBincompound.setTag(TAG_POS, NbtUtils.writeBlockPos(entry));
            compostBinTagList.add(compostBinCompound);
        }
        compound.setTag(TAG_BARRELS, compostBinTagList);
        return compound;
    }
}




