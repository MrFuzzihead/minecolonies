package com.minecolonies.core.colony.jobs;

import com.minecolonies.api.colony.jobs.IJobWithColonyFlag;
import com.minecolonies.core.util.citizenutils.CitizenItemUtils;
import net.minecraft.util.ResourceLocation;
import com.minecolonies.api.client.render.modeltype.ModModelTypes;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.core.entity.ai.workers.guard.EntityAIKnight;
import com.minecolonies.core.util.AttributeModifierUtils;
// [1.7.10] tags removed
// [1.7.10] world.entity removed
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.NBTTagCompound;
// [1.7.10] net.minecraft.util.DamageSource removed
// [1.7.10] int /* InteractionHand */ removed
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.research.util.ResearchConstants.SHIELD_USAGE;
import static com.minecolonies.api.util.constant.CitizenConstants.GUARD_HEALTH_MOD_LEVEL_NAME;
import static com.minecolonies.api.util.constant.GuardConstants.KNIGHT_HP_BONUS;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_BANNER_PATTERNS;

/**
 * The Knight's job class
 *
 * @author Asherslab
 */
public class JobKnight extends AbstractJobGuard<JobKnight> implements IJobWithColonyFlag
{
    /**
     * Desc of knight job.
     */
    public static final String DESC = "com.minecolonies.coremod.job.knight";

    /**
     * Initialize citizen data.
     *
     * @param entity the citizen data.
     */
    public JobKnight(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public EntityAIKnight generateGuardAI()
    {
        return new EntityAIKnight(this);
    }

    @Override
    public void onLevelUp()
    {
        // Bonus Health for knights(gets reset upon Firing)
        if (getCitizen().getEntity().isPresent())
        {
            final AbstractEntityCitizen citizen = getCitizen().getEntity().get();

            // +1 Heart every 2 World
            final AttributeModifier healthModLevel =
                new AttributeModifier(GUARD_HEALTH_MOD_LEVEL_NAME,
                    getCitizen().getCitizenSkillHandler().getLevel(Skill.Stamina) + KNIGHT_HP_BONUS,
                    AttributeModifier.Operation.ADDITION);
            AttributeModifierUtils.addHealthModifier(citizen, healthModLevel);
        }
    }

    @Override
    public ResourceLocation getModel()
    {
        return ModModelTypes.KNIGHT_GUARD_ID;
    }

    @Override
    public boolean ignoresDamage(@NotNull final net.minecraft.util.DamageSource source)
    {
        if (net.minecraft.util.DamageSource.is(DamageTypeTags.IS_EXPLOSION) && this.getColony().getResearchManager().getResearchEffects().getEffectStrength(SHIELD_USAGE) > 0
            && InventoryUtils.findFirstSlotInItemHandlerWith(this.getCitizen().getInventory(), Items.SHIELD) != -1)
        {
            if (!this.getCitizen().getEntity().isPresent())
            {
                return true;
            }
            final AbstractEntityCitizen worker = this.getCitizen().getEntity().get();
            CitizenItemUtils.setHeldItem(worker, 1 /* InteractionHand.OFF_HAND */, InventoryUtils.findFirstSlotInItemHandlerWith(this.getCitizen().getInventory(), Items.SHIELD));
            worker.startUsingItem(1 /* InteractionHand.OFF_HAND */);

            // Apply the colony Flag to the shield
            ItemStack shieldStack = worker.getInventoryCitizen().getHeldItem(1 /* InteractionHand.OFF_HAND */);
            NBTTagCompound nbt = shieldStack.getOrCreateTagElement("BlockEntityTag");
            nbt.put(TAG_BANNER_PATTERNS, worker.getCitizenColonyHandler().getColonyOrRegister().getColonyFlag());

            return true;
        }
        return super.ignoresDamage(net.minecraft.util.DamageSource);
    }

    @Override
    public void onColonyFlagChanged()
    {
        if (this.getCitizen().getEntity().isPresent())
        {
            final AbstractEntityCitizen worker = this.getCitizen().getEntity().get();
            CitizenItemUtils.setHeldItem(worker, 1 /* InteractionHand.OFF_HAND */, InventoryUtils.findFirstSlotInItemHandlerWith(this.getCitizen().getInventory(), Items.SHIELD));
            worker.startUsingItem(1 /* InteractionHand.OFF_HAND */);
            ItemStack shieldStack = worker.getInventoryCitizen().getHeldItem(1 /* InteractionHand.OFF_HAND */);
            NBTTagCompound nbt = shieldStack.getOrCreateTagElement("BlockEntityTag");
            nbt.put(TAG_BANNER_PATTERNS, worker.getCitizenColonyHandler().getColonyOrRegister().getColonyFlag());
        }
    }
}







