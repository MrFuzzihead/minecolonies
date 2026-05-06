package com.minecolonies.core.entity.ai.workers.production.herders;
import net.minecraft.entity.passive.EntitySheep;

import com.minecolonies.api.entity.ai.statemachine.AITarget;
import com.minecolonies.api.entity.ai.statemachine.states.IAIState;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.core.Network;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingShepherd;
import com.minecolonies.core.colony.jobs.JobShepherd;
import com.minecolonies.core.util.citizenutils.CitizenItemUtils;
import com.minecolonies.core.network.messages.client.LocalizedParticleEffectMessage;
// [1.7.10] sounds removed
// [1.7.10] int /* InteractionHand */ removed
// [1.7.10] world.entity removed
import net.minecraft.world.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.minecolonies.api.entity.ai.statemachine.states.AIWorkerState.*;
import static com.minecolonies.api.util.constant.Constants.TICKS_SECOND;
import static com.minecolonies.api.util.constant.StatisticsConstants.ITEM_OBTAINED;
import static com.minecolonies.core.colony.buildings.modules.BuildingModules.STATS_MODULE;
// [1.7.10] EntitySheep.field_175512_bO (ITEM_BY_DYE) does not exist; using local fallback
// import static net.minecraft.entity.passive.EntitySheep.field_175512_bO; // [1.7.10] ITEM_BY_DYE field

/**
 * The AI behind the {@link JobShepherd} for Breeding, Killing and Shearing EntitySheep.
 */
public class EntityAIWorkShepherd extends AbstractEntityAIHerder<JobShepherd, BuildingShepherd>
{
    /**
     * Constants used for EntitySheep dying calculations.
     */
    private static final int HUNDRED_PERCENT_CHANCE = 100;

    /**
     * [1.7.10] Replacement for EntitySheep.ITEM_BY_DYE (field_175512_bO).
     * Maps DyeColor (int ordinal) to the wool Item. In 1.7.10, wool is Blocks.wool with meta = dye ordinal.
     */
    private static final java.util.Map<net.minecraft.world.item.DyeColor, net.minecraft.item.Item> ITEM_BY_DYE;
    static {
        ITEM_BY_DYE = new java.util.HashMap<>();
        for (net.minecraft.world.item.DyeColor color : net.minecraft.world.item.DyeColor.values()) {
            ITEM_BY_DYE.put(color, net.minecraft.item.Item.getItemFromBlock(net.minecraft.init.Blocks.wool));
        }
    }

    /**
     * Creates the abstract part of the AI. Always use this constructor!
     *
     * @param job the job to fulfill
     */
    public EntityAIWorkShepherd(@NotNull final JobShepherd job)
    {
        super(job);
        super.registerTargets(
          new AITarget(SHEPHERD_SHEAR, this::shearSheep, TICKS_SECOND)
        );
    }

    @NotNull
    @Override
    public List<EquipmentTypeEntry> getExtraToolsNeeded()
    {
        final List<EquipmentTypeEntry> toolsNeeded = super.getExtraToolsNeeded();
        if (building.getSetting(BuildingShepherd.SHEARING).getValue())
        {
            toolsNeeded.add(ModEquipmentTypes.shears.get());
        }
        return toolsNeeded;
    }

    @Override
    public Class<BuildingShepherd> getExpectedBuildingClass()
    {
        return BuildingShepherd.class;
    }

    @Override
    public IAIState decideWhatToDo()
    {
        final IAIState result = super.decideWhatToDo();

        final EntitySheep shearingSheep = findShearableSheep();

        if (building.getSetting(BuildingShepherd.SHEARING).getValue() && result.equals(START_WORKING) && shearingSheep != null)
        {
            return SHEPHERD_SHEAR;
        }

        worker.setItemInHand(0 /* InteractionHand.MAIN_HAND */, null);

        return result;
    }

    @Override
    public double getButcheringAttackDamage()
    {
        return Math.max(1.0, getSecondarySkillLevel() / 10.0);
    }

    /**
     * @return a shearable {@link EntitySheep} or null.
     */
    @Nullable
    private EntitySheep findShearableSheep()
    {
        return searchForAnimals(a -> a instanceof EntitySheep sheepie && !sheepie.isSheared() && !sheepie.isBaby())
                 .stream().map(a -> (EntitySheep) a).findAny().orElse(null);
    }

    /**
     * Shears a EntitySheep, with a chance of dying it!
     *
     * @return The next {@link IAIState}
     */
    private IAIState shearSheep()
    {

        final EntitySheep EntitySheep = findShearableSheep();

        if (EntitySheep == null)
        {
            return DECIDE;
        }

        if (!equipTool(0 /* InteractionHand.MAIN_HAND */, ModEquipmentTypes.shears.get()))
        {
            return PREPARING;
        }

        if (worker.getMainHandItem() != null)
        {
            if (walkingToAnimal(EntitySheep))
            {
                return getState();
            }

            int enchantmentLevel = worker.getMainHandItem().getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
            enchantmentLevel *= Math.max(1.0, (getPrimarySkillLevel() / 5.0));

            worker.swing(0 /* InteractionHand.MAIN_HAND */);

            final List<ItemStack> items = new ArrayList<>();
            if (!this.world.isClientSide)
            {
                EntitySheep.setSheared(true);
                int qty = 1 + worker.getRandom().nextInt(enchantmentLevel + 1);

                for (int j = 0; j < qty; ++j)
                {
                    items.add(new ItemStack(ITEM_BY_DYE.get(EntitySheep.getColor())));
                }
            }

            EntitySheep.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
            Network.getNetwork().sendToTrackingEntity(new LocalizedParticleEffectMessage(new ItemStack(ITEM_BY_DYE.get(EntitySheep.getColor())), EntitySheep.getOnPos().above()), worker);
            dyeSheepChance(EntitySheep);

            CitizenItemUtils.damageItemInHand(worker, 0 /* InteractionHand.MAIN_HAND */, 1);

            worker.getCitizenExperienceHandler().addExperience(XP_PER_ACTION);
            incrementActionsDoneAndDecSaturation();

            for (final ItemStack item : items)
            {
                building.getModule(STATS_MODULE).incrementBy(ITEM_OBTAINED + ";" + item.getItem().getDescriptionId(), item.getCount());
                InventoryUtils.transferItemStackIntoNextBestSlotInItemHandler(item, (worker.getInventoryCitizen()));
            }
        }

        return DECIDE;
    }

    /**
     * Possibly dyes a EntitySheep based on their Worker Hut World
     *
     * @param EntitySheep the {@link EntitySheep} to possibly dye.
     */
    private void dyeSheepChance(final EntitySheep EntitySheep)
    {
        if (building != null && building.getSetting(BuildingShepherd.DYEING).getValue())
        {
            final int chanceToDye = building.getBuildingLevel();
            final int rand = worker.getRandom().nextInt(HUNDRED_PERCENT_CHANCE);

            if (rand <= chanceToDye)
            {
                final DyeColor[] colors = DyeColor.values();
                final int dyeIndex = worker.getRandom().nextInt(colors.length);
                EntitySheep.setColor(colors[dyeIndex]);
            }
        }
    }
}





