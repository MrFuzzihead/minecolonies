package com.minecolonies.core.entity.ai.workers.guard;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.compatibility.tinkers.TinkersToolHelper;
import com.minecolonies.api.entity.ai.combat.CombatAIStates;
import com.minecolonies.api.entity.ai.combat.threat.IThreatTableEntity;
import com.minecolonies.api.entity.ai.statemachine.states.IAIState;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.ITickRateStateMachine;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.TickingTransition;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.api.entity.citizen.VisibleCitizenStatus;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.util.DamageSourceKeys;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.SoundUtils;
import com.minecolonies.api.util.constant.ColonyConstants;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.MineColonies;
import com.minecolonies.core.colony.jobs.AbstractJobGuard;
import com.minecolonies.core.entity.ai.combat.AttackMoveAI;
import com.minecolonies.core.entity.ai.combat.CombatUtils;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import com.minecolonies.core.entity.pathfinding.navigation.EntityNavigationUtils;
import com.minecolonies.core.entity.pathfinding.pathresults.PathResult;
import com.minecolonies.core.util.citizenutils.CitizenItemUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.NBTTagCompound;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
// [1.7.10] sounds removed
import net.minecraft.util.MathHelper;
// [1.7.10] int /* InteractionHand */ removed
// [1.7.10] net.minecraft.util.DamageSource removed
import net.minecraft.entity.EntityLivingBase;
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;
import java.util.Objects;

import static com.minecolonies.api.research.util.ResearchConstants.*;
import static com.minecolonies.api.util.constant.GuardConstants.*;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_BANNER_PATTERNS;
import static com.minecolonies.api.util.constant.StatisticsConstants.MOBS_KILLED;
import static com.minecolonies.api.util.constant.StatisticsConstants.MOB_KILLED;
import static com.minecolonies.core.colony.buildings.modules.BuildingModules.STATS_MODULE;
import static com.minecolonies.core.entity.ai.BehaviourStateGroup.GUARD_ABORT_AND_FIGHT;
import static com.minecolonies.core.entity.ai.workers.guard.AbstractEntityAIFight.SPEED_LEVEL_BONUS;
import static com.minecolonies.core.entity.ai.workers.guard.AbstractEntityAIGuard.PATROL_DEVIATION_RAID_POINT;

/**
 * Knight combat AI
 */
public class KnightCombatAI extends AttackMoveAI<EntityCitizen>
{
    /**
     * Combat icon
     */
    private final static VisibleCitizenStatus KNIGHT_COMBAT =
      new VisibleCitizenStatus(new ResourceLocation(Constants.MOD_ID, "textures/icons/work/knight_combat.png"), "com.minecolonies.gui.visiblestatus.knight_combat");

    /**
     * Knockback chance
     */
    private static final int                   KNOCKBACK_CHANCE = 5;
    private final        AbstractEntityAIGuard parentAI;

    /**
     * Last used time of the aoe ability
     */
    private long lastAoeUseTime = 0;

    /**
     * Cooldown for the Aoe knockback in ticks
     */
    private final int KNOCKBACK_COOLDOWN = 30 * 8;

    /**
     * Minimum time needed to next attack to use the shield
     */
    private final int MIN_TIME_TO_ATTACK = 8;

    /**
     * The value of the speed which the guard will move.
     */
    private static final double COMBAT_SPEED = 1.0;

    public KnightCombatAI(
      final EntityCitizen owner,
      final ITickRateStateMachine stateMachine,
      final AbstractEntityAIGuard parentAI)
    {
        super(owner, stateMachine);

        this.parentAI = parentAI;
        stateMachine.addTransition(new TickingTransition<>(CombatAIStates.ATTACKING, () -> true, this::attackProtect, 8));
        stateMachine.addTransitionGroup(GUARD_ABORT_AND_FIGHT, new TickingTransition(this::checkForTarget, () -> CombatAIStates.ATTACKING, 5).withName("busy_checkTarget"));
        stateMachine.addTransitionGroup(GUARD_ABORT_AND_FIGHT, new TickingTransition(this::searchNearbyTarget, () -> CombatAIStates.ATTACKING, 80).withName("busy_searchTarget"));
    }

    /**
     * Check if the guard can protect himself with a shield And if so, do it.
     *
     * @return The next IAIState.
     */
    protected IAIState attackProtect()
    {
        final int shieldSlot = InventoryUtils.findFirstSlotInItemHandlerWith(user.getInventoryCitizen(), Items.SHIELD);
        if (shieldSlot != -1 && target != null && target.isAlive() && nextAttackTime - user.world.getTotalWorldTime() >= MIN_TIME_TO_ATTACK &&
              user.getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(SHIELD_USAGE) > 0)
        {
            CitizenItemUtils.setHeldItem(user, 1 /* InteractionHand.OFF_HAND */, shieldSlot);
            user.startUsingItem(1 /* InteractionHand.OFF_HAND */);

            // Apply the colony Flag to the shield
            ItemStack shieldStack = user.getInventoryCitizen().getHeldItem(1 /* InteractionHand.OFF_HAND */);
            NBTTagCompound nbt = shieldStack.getOrCreateTagElement("BlockEntityTag");
            if (!Objects.equals(nbt.get(TAG_BANNER_PATTERNS), user.getCitizenColonyHandler().getColonyOrRegister().getColonyFlag()))
            {
                nbt.put(TAG_BANNER_PATTERNS, user.getCitizenColonyHandler().getColonyOrRegister().getColonyFlag());
                user.getInventoryCitizen().markDirty();
            }
            user.lookAt(target, (float) TURN_AROUND, (float) TURN_AROUND);
        }

        return null;
    }

    @Override
    public boolean canAttack()
    {
        final int weaponSlot =
          InventoryUtils.getFirstSlotOfItemHandlerContainingEquipment(user.getInventoryCitizen(),
            ModEquipmentTypes.sword.get(),
            0,
            user.getCitizenData().getWorkBuilding().getMaxEquipmentLevel());

        if (weaponSlot != -1)
        {
            CitizenItemUtils.setHeldItem(user, 0 /* InteractionHand.MAIN_HAND */, weaponSlot);
            return true;
        }

        return false;
    }

    @Override
    protected void doAttack(final EntityLivingBase target)
    {
        if (user.distanceTo(target) > 1)
        {
            moveInAttackPosition(target);
        }

        user.swing(0 /* InteractionHand.MAIN_HAND */);
        user.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, (float) BASIC_VOLUME, (float) SoundUtils.getRandomPitch(user.getRandom()));

        final double damageToBeDealt = getAttackDamage();
        net.minecraft.util.DamageSource source = target.World.damageSources().source(DamageSourceKeys.GUARD, user);
        if (MineColonies.getConfig().getServer().pvp_mode.get() && target instanceof Player)
        {
            source = target.World.damageSources().source(DamageSourceKeys.GUARD_PVP, user);
        }

        final int fireLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, user.getItemInHand(0 /* InteractionHand.MAIN_HAND */));
        if (fireLevel > 0)
        {
            target.setSecondsOnFire(fireLevel * 4);
        }

        if (user.world.getTotalWorldTime() - lastAoeUseTime > KNOCKBACK_COOLDOWN)
        {
            doAoeAttack(source, damageToBeDealt);
        }

        target.hurt(source, (float) damageToBeDealt);
        target.setLastHurtByMob(user);

        if (target instanceof EntityCreature && user.getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(KNIGHT_TAUNT) > 0)
        {
            ((EntityCreature) target).setTarget(user);
            if (target instanceof IThreatTableEntity)
            {
                ((IThreatTableEntity) target).getThreatTable().addThreat(user, 5);
            }
        }

        user.stopUsingItem();
        user.getCitizenData().setVisibleStatus(getCombatStatus());
        CitizenItemUtils.damageItemInHand(user, 0 /* InteractionHand.MAIN_HAND */, 1);
    }

    protected VisibleCitizenStatus getCombatStatus()
    {
        return KNIGHT_COMBAT;
    }

    /**
     * Does an aoe attack if researched
     *
     * @param source          normal attack damage source
     * @param damageToBeDealt normal attack damage to be distributed to targets
     */
    private void doAoeAttack(final net.minecraft.util.DamageSource source, final double damageToBeDealt)
    {
        if (user.getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(KNIGHT_WHIRLWIND) > 0
              && user.getRandom().nextInt(KNOCKBACK_CHANCE) == 0)
        {
            List<EntityLivingBase> entities = user.World.getEntitiesOfClass(EntityLivingBase.class, user.getBoundingBox().inflate(2.0D, 0.5D, 2.0D));
            for (EntityLivingBase EntityLivingBase : entities)
            {
                if (EntityLivingBase != user && isEntityValidTarget(EntityLivingBase) && (!(EntityLivingBase instanceof ArmorStand)))
                {
                    EntityLivingBase.knockback(
                      2F,
                      Mth.sin(EntityLivingBase.getYRot() * ((float) Math.PI)),
                      (-Mth.cos(EntityLivingBase.getYRot() * ((float) Math.PI))));
                    EntityLivingBase.hurt(source, (float) (damageToBeDealt / entities.size()));
                }
            }

            user.World.playSound(null,
              user.getX(),
              user.getY(),
              user.getZ(),
              SoundEvents.PLAYER_ATTACK_SWEEP,
              user.getSoundSource(),
              1.0F,
              1.0F);

            double d0 = (double) (-Mth.sin(user.getYRot() * ((float) Math.PI / 180)));
            double d1 = (double) Mth.cos(user.getYRot() * ((float) Math.PI / 180));
            if (user.World instanceof ServerLevel)
            {
                ((ServerLevel) user.World).sendParticles(ParticleTypes.SWEEP_ATTACK,
                  user.getX() + d0,
                  user.getY(0.5D),
                  user.getZ() + d1,
                  2,
                  d0,
                  0.0D,
                  d1,
                  0.0D);
            }

            lastAoeUseTime = user.world.getTotalWorldTime();
        }
    }

    /**
     * Calculates the damage to deal
     *
     * @return attack damage
     */
    protected double getAttackDamage()
    {
        double addDmg = 0;

        final ItemStack heldItem = user.getItemInHand(0 /* InteractionHand.MAIN_HAND */);

        if (ItemStackUtils.doesItemServeAsWeapon(heldItem))
        {
            if (heldItem.getItem() instanceof SwordItem)
            {
                addDmg += ((SwordItem) heldItem.getItem()).getDamage() + BASE_PHYSICAL_DAMAGE;
            }
            else
            {
                addDmg += TinkersToolHelper.getDamage(heldItem);
            }
            addDmg += EnchantmentHelper.getDamageBonus(heldItem, target.getMobType()) / 2.5;
        }

        addDmg += user.getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(MELEE_DAMAGE);

        // TODO: Recheck balancing, do we need this
        if (user.getHealth() <= user.getMaxHealth() * 0.2D)
        {
            addDmg *= 2;
        }

        if (ColonyConstants.rand.nextDouble() > 1 / (1 + user.getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(GUARD_CRIT)))
        {
            addDmg *= 1.5;
            ((ServerLevel) user.World).getChunkSource().broadcastAndSend(user, new ClientboundAnimatePacket(target, 4));
        }

        return addDmg * MineColonies.getConfig().getServer().guardDamageMultiplier.get();
    }

    @Override
    protected double getAttackDistance()
    {
        return MAX_DISTANCE_FOR_ATTACK;
    }

    @Override
    protected int getAttackDelay()
    {
        // TODO: Not sure if we should make knights attack faster, they are intended to not scale in dmg, but health
        final int reload = KNIGHT_ATTACK_DELAY_BASE - user.getCitizenData().getCitizenSkillHandler().getLevel(Skill.Adaptability) / 3;
        return Math.max(reload, KNIGHT_ATTACK_DELAY_MIN);
    }

    @Override
    protected PathResult moveInAttackPosition(final EntityLivingBase target)
    {
        EntityNavigationUtils.walkToPos(user, target.blockPosition(), (int) getAttackDistance(), false, getCombatMovementSpeed());
        return user.getNavigation().getPathResult();
    }

    /**
     * Get combat speed
     *
     * @return movent speed
     */
    protected double getCombatMovementSpeed()
    {
        double levelAdjustment = user.getCitizenData().getCitizenSkillHandler().getLevel(Skill.Adaptability) * SPEED_LEVEL_BONUS;
        levelAdjustment += (user.getCitizenData().getWorkBuilding().getBuildingLevelEquivalent() - 1) * SPEED_LEVEL_BONUS;

        levelAdjustment = Math.min(levelAdjustment, 0.3);
        return COMBAT_SPEED + levelAdjustment;
    }

    @Override
    protected boolean isAttackableTarget(final EntityLivingBase entity)
    {
        return AbstractEntityAIGuard.isAttackableTarget(user, entity);
    }

    @Override
    protected boolean isWithinPersecutionDistance(final EntityLivingBase target)
    {
        return parentAI.isWithinPersecutionDistance(target.blockPosition(), getAttackDistance());
    }

    @Override
    protected boolean skipSearch(final EntityLivingBase entity)
    {
        // Found a sleeping guard nearby
        if (entity instanceof EntityCitizen)
        {
            final EntityCitizen citizen = (EntityCitizen) entity;
            if (citizen.getCitizenJobHandler().getColonyJob() instanceof AbstractJobGuard && ((AbstractJobGuard<?>) citizen.getCitizenJobHandler().getColonyJob()).isAsleep()
                  && user.getSensing().hasLineOfSight(citizen))
            {
                parentAI.setWakeCitizen(citizen);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onTargetChange(final EntityLivingBase newTarget)
    {
        super.onTargetChange(newTarget);
        CombatUtils.notifyGuardsOfTarget(user, target, PATROL_DEVIATION_RAID_POINT);
    }

    @Override
    protected int getSearchRange()
    {
        return 16;
    }

    @Override
    protected void onTargetDied(final EntityLivingBase entity)
    {
        parentAI.incrementActionsDone();
        user.getCitizenExperienceHandler().addExperience(EXP_PER_MOB_DEATH);
        user.getCitizenColonyHandler().getColonyOrRegister().getStatisticsManager().increment(MOBS_KILLED, user.getCitizenColonyHandler().getColonyOrRegister().getDay());
        if (entity.getType().getDescription().getContents() instanceof TranslatableContents translatableContents)
        {
            parentAI.building.getModule(STATS_MODULE).increment(MOB_KILLED + ";" + translatableContents.getKey());
        }
        user.decreaseSaturationForContinuousAction();
    }
}






