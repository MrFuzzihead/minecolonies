package com.minecolonies.core.colony.buildings.workerbuildings;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.modules.IItemListModule;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.items.ModTags;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.AbstractCraftingBuildingModule;
import com.minecolonies.core.colony.buildings.modules.ItemListModule;
import com.minecolonies.core.colony.buildings.modules.WorkerBuildingModule;
import com.minecolonies.core.colony.buildings.modules.settings.BoolSetting;
import com.minecolonies.core.colony.buildings.modules.settings.DynamicTreesSetting;
import com.minecolonies.core.colony.buildings.modules.settings.SettingKey;
import com.minecolonies.core.colony.buildings.views.AbstractBuildingView;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
// [1.7.10] NbtUtils removed
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
// [1.7.10] Object /* [1.7.10] BonemealableBlock N/A */ removed; use block instanceof check
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_NETHER_TREE_LIST;
import static com.minecolonies.api.util.constant.EquipmentLevelConstants.TOOL_LEVEL_WOOD_OR_GOLD;
import static com.minecolonies.core.entity.ai.workers.production.EntityAIWorkLumberjack.SAPLINGS_LIST;

/**
 * The lumberjacks building.
 */
public class BuildingLumberjack extends AbstractBuilding
{
    /**
     * Replant setting.
     */
    public static final ISettingKey<BoolSetting> REPLANT = new SettingKey<>(BoolSetting.class, new ResourceLocation(com.minecolonies.api.util.constant.Constants.MOD_ID, "replant"));
    public static final ISettingKey<BoolSetting> RESTRICT = new SettingKey<>(BoolSetting.class, new ResourceLocation(com.minecolonies.api.util.constant.Constants.MOD_ID, "restrict"));
    public static final ISettingKey<BoolSetting> DEFOLIATE = new SettingKey<>(BoolSetting.class, new ResourceLocation(com.minecolonies.api.util.constant.Constants.MOD_ID, "defoliate"));
    public static final ISettingKey<DynamicTreesSetting> DYNAMIC_TREES_SIZE = new SettingKey<>(DynamicTreesSetting.class, new ResourceLocation(com.minecolonies.api.util.constant.Constants.MOD_ID, "dynamictreeharvestsize"));

    /**
     * NBT NBTBase for lj restriction start
     */
    private static final String TAG_RESTRICT_START = "startRestrictionPosition";

    /**
     * NBT NBTBase for lj restriction end
     */
    private static final String TAG_RESTRICT_END = "endRestrictionPosition";

    /**
     * The start position of the restricted area.
     */
    private int[] startRestriction = null;

    /**
     * The end position of the restricted area.
     */
    private int[] endRestriction = null;

    /**
     * The maximum upgrade of the building.
     */
    private static final int    MAX_BUILDING_LEVEL = 5;

    /**
     * The job description.
     */
    private static final String LUMBERJACK         = "lumberjack";

    /**
     * A list of all planted nether trees
     */
    private final Set<int[]> netherTrees = new HashSet<>();

    /**
     * Modifier for fungi growing time. Increase to speed up.
     */
    private static final int FUNGI_MODIFIER = 10;

    /**
     * Public constructor of the building, creates an object of the building.
     *
     * @param c the colony.
     * @param l the position.
     */
    public BuildingLumberjack(final IColony c, final int[] l)
    {
        super(c, l);

        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.axe.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));
        keepX.put(itemStack -> ItemStackUtils.hasEquipmentLevel(itemStack, ModEquipmentTypes.shears.get(), TOOL_LEVEL_WOOD_OR_GOLD, getMaxEquipmentLevel()), new Tuple<>(1, true));
    }

    @Override
    public boolean canBeGathered()
    {
        // Normal crafters are only gatherable when they have a task, i.e. while producing stuff.
        // BUT, the lumberjack both gathers and crafts things now, so it should always be gatherable.
        // This unfortunately means that the dman will sometimes "steal" ingredients from the LJ.
        // Fortunately, the dman is smart enough to not instantly gather the ingredients it brought to the LJ.
        // Might be improved in the future. For now, it's a bit annoying, but not too bad imho.
        return true;
    }

    @Override
    public Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> getRequiredItemsAndAmount()
    {
        final Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> toKeep = new HashMap<>(super.getRequiredItemsAndAmount());
        final IItemListModule saplingList = getModuleMatching(ItemListModule.class, m -> m.getId().equals(SAPLINGS_LIST));
        for (final ItemStorage sapling : IColonyManager.getInstance().getCompatibilityManager().getCopyOfSaplings())
        {
            if (!saplingList.isItemInList(sapling))
            {
                toKeep.put(stack -> ItemStackUtils.compareItemStacksIgnoreStackSize(sapling.getItemStack(), stack), new Tuple<>(com.minecolonies.api.util.constant.Constants.STACKSIZE, true));
            }
        }
        return toKeep;
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return LUMBERJACK;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return MAX_BUILDING_LEVEL;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);

        if (compound.hasKey(TAG_RESTRICT_START))
        {
            startRestriction = NbtUtils.readBlockPos(compound.getCompoundTag(TAG_RESTRICT_START));
        }
        else
        {
            startRestriction = null;
        }

        if (compound.hasKey(TAG_RESTRICT_END))
        {
            endRestriction = NbtUtils.readBlockPos(compound.getCompoundTag(TAG_RESTRICT_END));
        }
        else
        {
            endRestriction = null;
        }

        final NBTTagList netherTreeBinTagList = compound.getTagList(TAG_NETHER_TREE_LIST, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < netherTreeBinTagList.size(); i++)
        {
            netherTrees.add(BlockPosUtil.readFromListNBT(netherTreeBinTagList, i));
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = super.serializeNBT();

        if (startRestriction != null)
        {
            compound.setTag(TAG_RESTRICT_START, NbtUtils.writeBlockPos(startRestriction));
        }

        if (endRestriction != null)
        {
            compound.setTag(TAG_RESTRICT_END, NbtUtils.writeBlockPos(endRestriction));
        }

        @NotNull final NBTTagList netherTreeBinCompoundList = new NBTTagList();
        for (@NotNull final int[] pos : netherTrees)
        {
            BlockPosUtil.writeToListNBT(netherTreeBinCompoundList, pos);
        }
        compound.setTag(TAG_NETHER_TREE_LIST, netherTreeBinCompoundList);
        return compound;
    }

    /**
     * Whether or not the LJ should replant saplings.
     *
     * @return true if so.
     */
    public boolean shouldReplant()
    {
        return getSetting(REPLANT).getValue();
    }

    /**
     * Whether or not the LJ should break all the leaves, not just the ones directly in the way.
     *
     * @return true if so.
     */
    public boolean shouldDefoliate()
    {
        return getSetting(DEFOLIATE).getValue();
    }

    /**
     * Whether or not the LJ should be restricted.
     *
     * @return true if it should restrict.
     */
    public boolean shouldRestrict()
    {
        if (getSetting(RESTRICT).getValue())
        {
            if (startRestriction == null || endRestriction == null)
            {
                getSetting(RESTRICT).trigger();
                markDirty();
            }
        }
        return getSetting(RESTRICT).getValue();
    }

    public void setRestrictedArea(final int[] startPosition, final int[] endPosition)
    {
        this.startRestriction = startPosition;
        this.endRestriction = endPosition;

        final boolean areaIsDefined = startPosition != null && endPosition != null;
        if (getSetting(RESTRICT).getValue() != areaIsDefined)
        {
            getSetting(RESTRICT).trigger();
        }
        markDirty();
    }

    public int[] getStartRestriction()
    {
        return this.startRestriction;
    }

    public int[] getEndRestriction()
    {
        return this.endRestriction;
    }

    /**
     * Returns early if no worker is assigned Iterates over the nether tree position list If position is a fungus, grows it depending on worker's World If the block has changed,
     * removes the position from the list and returns early If the position is not a fungus, removes the position from the list
     */
    private void bonemealFungi()
    {
        final WorkerBuildingModule module = getFirstModuleOccurance(WorkerBuildingModule.class);
        final ICitizenData data = getFirstModuleOccurance(WorkerBuildingModule.class).getFirstCitizen();
        if (data == null)
        {
            return;
        }
        final int modifier = Math.max(0, Math.min(FUNGI_MODIFIER, 100));
        for (Iterator<int[]> iterator = netherTrees.iterator(); iterator.hasNext(); )
        {
            final int[] pos = iterator.next();
            final World world = colony.getWorld();
            if (WorldUtil.isBlockLoaded(world, pos))
            {
                final BlockState blockState = world.getBlockState(pos);
                final Block block = blockState.getBlock();
                if (blockState.is(ModTags.mushroomBlocks) || blockState.is(ModTags.fungiBlocks))
                {
                    int threshold = modifier + (int) Math.ceil(data.getCitizenSkillHandler().getLevel(module.getPrimarySkill()) * (1 - ((float) modifier / 100)));
                    final int rand = world.getRandom().nextInt(100);
                    if (rand < threshold)
                    {
                        final Object /* [1.7.10] BonemealableBlock N/A */ growable = (Object /* [1.7.10] BonemealableBlock N/A */) block;
                        // [1.7.10] BonemealableBlock.isValidBonemealTarget/isBonemealSuccess/performBonemeal not available
                        // TODO Phase 7: implement bonemeal growth logic using 1.7.10 Block.updateTick or world event
                    }
                }
                else
                {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Returns a list of the registered nether trees to grow.
     *
     * @return a copy of the list
     */
    public Set<int[]> getNetherTrees()
    {
        return new HashSet<>(netherTrees);
    }

    /**
     * Removes a position from the nether trees
     *
     * @param pos the position
     */
    public void removeNetherTree(int[] pos)
    {
        netherTrees.remove(pos);
    }

    /**
     * Adds a position to the nether trees
     *
     * @param pos the position
     */
    public void addNetherTree(int[] pos)
    {
        netherTrees.add(pos);
    }

    @Override
    public void onColonyTick(@NotNull final IColony colony)
    {
        super.onColonyTick(colony);
        bonemealFungi();
    }

    @Override
    public void serializeToView(@NotNull final PacketBuffer buf, final boolean fullSync)
    {
        super.serializeToView(buf, fullSync);

        buf.writeBoolean(shouldRestrict());
        if (startRestriction != null && endRestriction != null)
        {
            buf.writeBlockPos(startRestriction);
            buf.writeBlockPos(endRestriction);
        }
        else
        {
            buf.writeBlockPos(new int[]{0,0,0});
            buf.writeBlockPos(new int[]{0,0,0});
        }
    }

    /**
     * The client side representation of the building.
     */
    public static class View extends AbstractBuildingView
    {
        private boolean restrict;
        private int[] startRestriction;
        private int[] endRestriction;

        /**
         * Instantiates the view of the building.
         *
         * @param c the colonyView.
         * @param l the location of the block.
         */
        public View(final IColonyView c, final int[] l)
        {
            super(c, l);
        }

        @Override
        public void deserialize(@NotNull PacketBuffer buf)
        {
            super.deserialize(buf);

            this.restrict = buf.readBoolean();
            this.startRestriction = buf.readBlockPos();
            this.endRestriction = buf.readBlockPos();
        }

        public boolean shouldRestrict()
        {
            return this.restrict;
        }

        public int[] getStartRestriction()
        {
            return this.startRestriction;
        }

        public int[] getEndRestriction()
        {
            return this.endRestriction;
        }
    }

    public static class CraftingModule extends AbstractCraftingBuildingModule.Custom
    {
        /**
         * Create a new module.
         *
         * @param jobEntry the entry of the job.
         */
        public CraftingModule(final JobEntry jobEntry)
        {
            super(jobEntry);
        }

        @Override
        public boolean canRecipeBeAdded(@NotNull final IToken<?> token)
        {
            return false;
        }
    }
}





