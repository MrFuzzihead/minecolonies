package com.minecolonies.core.colony.buildings.workerbuildings;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.api.crafting.GenericRecipe;
import com.minecolonies.api.crafting.IGenericRecipe;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.util.NBTUtils;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.AnimalHerdingModule;
import com.minecolonies.core.colony.buildings.modules.settings.BeekeeperCollectionSetting;
import com.minecolonies.core.colony.buildings.modules.settings.SettingKey;
import com.minecolonies.core.colony.buildings.views.AbstractBuildingView;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
// [1.7.10] NbtUtils removed
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
// [1.7.10] tags removed
import com.minecolonies.api.util.Tuple;
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.BuildingConstants.CONST_DEFAULT_MAX_BUILDING_LEVEL;
import static com.minecolonies.api.util.constant.Constants.STACKSIZE;

/**
 * Building of the beekeeper (apiary).
 */
public class BuildingBeekeeper extends AbstractBuilding
{
    /**
     * The beekeeper mode.
     */
    public static final ISettingKey<BeekeeperCollectionSetting> MODE =
      new SettingKey<>(BeekeeperCollectionSetting.class, new ResourceLocation(com.minecolonies.api.util.constant.Constants.MOD_ID, "beekeeper"));

    /**
     * Both setting options.
     */
    public static final String HONEYCOMB = "com.minecolonies.core.apiary.setting.honeycomb";
    public static final String HONEY     = "com.minecolonies.core.apiary.setting.honey";
    public static final String BOTH      = "com.minecolonies.core.apiary.setting.both";

    /**
     * Description of the job executed in the hut.
     */
    private static final String BEEKEEPER = "beekeeper";

    /**
     * List of hives.
     */
    private Set<int[]> hives = new HashSet<>();

    /**
     * The abstract constructor of the building.
     *
     * @param c the colony
     * @param l the position
     */
    public BuildingBeekeeper(@NotNull final IColony c, final int[] l)
    {
        super(c, l);
        keepX.put(stack -> Items.shears == stack.getItem(), new Tuple<>(1, true));
        keepX.put(stack -> Items.glass_bottle == stack.getItem(), new Tuple<>(4, true));
        keepX.put(stack -> stack.is(ItemTags.FLOWERS), new Tuple<>(STACKSIZE,true));
    }

    /**
     * Children must return the name of their structure.
     *
     * @return StructureProxy name.
     */
    @NotNull
    @Override
    public String getSchematicName()
    {
        return BEEKEEPER;
    }

    /**
     * Children must return their max building World.
     *
     * @return Max building World.
     */
    @Override
    public int getMaxBuildingLevel()
    {
        return CONST_DEFAULT_MAX_BUILDING_LEVEL;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);
        NBTUtils.streamCompound(compound.getList(NbtTagConstants.TAG_HIVES, NBTBase.TAG_COMPOUND))
          .map(NbtUtils::readBlockPos)
          .forEach(this.hives::add);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound nbt = super.serializeNBT();
        nbt.put(NbtTagConstants.TAG_HIVES, this.hives.stream().map(NbtUtils::writeBlockPos).collect(NBTUtils.toListNBT()));
        return nbt;
    }

    @Override
    public void serializeToView(@NotNull final PacketBuffer buf, final boolean fullSync)
    {
        super.serializeToView(buf, fullSync);

        buf.writeVarInt(hives.size());
        for (final int[] hive : hives)
        {
            buf.writeBlockPos(hive);
        }
    }

    @Override
    public boolean canEat(final ItemStack stack)
    {
        if (stack.getItem() == null /* [1.7.10] HONEY_BOTTLE does not exist */)
        {
            return false;
        }
        return super.canEat(stack);
    }


    /**
     * Get the hives/nests positions that belong to this beekeper
     *
     * @return te set of positions of hives/nests that belong to this beekeeper
     */
    public Set<int[]> getHives()
    {
        return Collections.unmodifiableSet(new HashSet<>(hives));
    }

    /**
     * Remove a hive/nest position from this beekeper
     *
     * @param pos the position to remove
     */
    public void removeHive(final int[] pos)
    {
        hives.remove(pos);
    }

    /**
     * Add a hive/nest position to this beekeper
     *
     * @param pos the position to add
     */
    public void addHive(final int[] pos)
    {
        hives.add(pos);
    }

    /**
     * Get what materials the beekeeper should harvest.
     *
     * @return honeycomb, honey bottle, or both
     */
    public String getHarvestTypes()
    {
        return getSetting(MODE).getValue();
    }

    /**
     * Get the maximum amount of hives that can belong to this beekeeper
     *
     * @return the number of maximum hives that can belong to this beekeeper
     */
    public int getMaximumHives()
    {
        return (int) Math.pow(2, getBuildingLevel() - 1);
    }

    /**
     * The client side representation of the building.
     */
    public static class View extends AbstractBuildingView
    {
        private Set<int[]> hives;

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

            final int hiveCount = buf.readVarInt();
            this.hives = new HashSet<>();
            for (int i = 0; i < hiveCount; ++i)
            {
                this.hives.add(buf.readBlockPos());
            }
        }

        public Set<int[]> getHives()
        {
            return Collections.unmodifiableSet(new HashSet<>(hives));
        }
    }

    /**
     * Bee herding module
     */
    public static class HerdingModule extends AnimalHerdingModule
    {
        // note that the beekeeper is a bit different from regular herders as they never
        // over-breed and kill the bees (bees don't drop any loot anyway).  currently this
        // doesn't matter but if we extend the AnimalHerdingModule with additional AI
        // functionality then this may need special behaviour.

        public HerdingModule()
        {
            super(ModJobs.beekeeper.get(), a -> a instanceof Bee, new ItemStorage(ItemStack.EMPTY, 1));
        }

        @NotNull
        @Override
        public List<ItemStorage> getBreedingItems()
        {
            if (building != null)
            {
                // todo: if we use this in AI then it should use the item list module settings from the building instead.
            }

            return IColonyManager.getInstance().getCompatibilityManager().getImmutableFlowers().stream()
              .map(flower -> new ItemStorage(flower.getItem(), 2))
              .collect(Collectors.toList());
        }

        @NotNull
        @Override
        public List<IGenericRecipe> getRecipesForDisplayPurposesOnly(@NotNull net.minecraft.entity.passive.EntityAnimal animal)
        {
            // [1.7.10] Bees don't exist in 1.7.10; return empty list
            return new ArrayList<>();
        }
    }
}






