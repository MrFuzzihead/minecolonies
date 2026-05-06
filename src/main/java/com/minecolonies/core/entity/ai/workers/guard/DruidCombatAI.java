package com.minecolonies.core.entity.ai.workers.guard;
import net.minecraft.world.entity.player.Player;

import com.google.common.collect.ImmutableList;
import com.minecolonies.api.colony.guardtype.registry.ModGuardTypes;
import com.minecolonies.api.entity.ai.combat.CombatAIStates;
import com.minecolonies.api.entity.ai.combat.threat.IThreatTableEntity;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.ITickRateStateMachine;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.TickingTransition;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.core.colony.buildings.AbstractBuildingGuards;
import com.minecolonies.core.colony.buildings.modules.settings.GuardTaskSetting;
import com.minecolonies.core.colony.jobs.AbstractJobGuard;
import com.minecolonies.core.colony.jobs.JobDruid;
import com.minecolonies.core.entity.ai.combat.AttackMoveAI;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import com.minecolonies.core.entity.other.DruidPotionEntity;
import com.minecolonies.core.entity.pathfinding.PathfindingUtils;
import com.minecolonies.core.entity.pathfinding.PathingOptions;
import com.minecolonies.core.entity.pathfinding.navigation.EntityNavigationUtils;
import com.minecolonies.core.entity.pathfinding.navigation.MinecoloniesAdvancedPathNavigate;
import com.minecolonies.core.entity.pathfinding.pathjobs.PathJobCanSee;
import com.minecolonies.core.entity.pathfinding.pathjobs.PathJobMoveAwayFromLocation;
import com.minecolonies.core.entity.pathfinding.pathjobs.PathJobMoveToLocation;
import com.minecolonies.core.entity.pathfinding.pathresults.PathResult;
// [1.7.10] int /* InteractionHand */ removed
// [1.7.10] effect removed
// [1.7.10] effect removed
// [1.7.10] effect removed
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.effect.MobEffect;
// [1.7.10] world.entity removed
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

import static com.minecolonies.api.research.util.ResearchConstants.DRUID_USE_POTIONS;
import static com.minecolonies.api.util.constant.GuardConstants.*;
import static com.minecolonies.core.entity.ai.BehaviourStateGroup.GUARD_ABORT_AND_FIGHT;
import static com.minecolonies.core.entity.ai.workers.guard.AbstractEntityAIFight.SPEED_LEVEL_BONUS;

/**
 * Druid combat AI
 */
public class DruidCombatAI extends AttackMoveAI<EntityCitizen>
{
    /**
     * List of potential positive effects.
     */
    private static final ImmutableList<MobEffect> SUPPORT_EFFECTS = ImmutableList.of(MobEffects.DAMAGE_BOOST, MobEffects.SATURATION, MobEffects.HEAL, MobEffects.DAMAGE_RESISTANCE);

    /**
     * List of potential positive effects.
     */
    private static final ImmutableList<MobEffect> ADVERSE_EFFECTS = ImmutableList.of(MobEffects.MOVEMENT_SLOWDOWN, MobEffects.WEAKNESS);

    /**
     * The xp per thrown potion
     */
    private static final double PER_POTION_XP = 0.05D;

    /**
     * The value of the speed which the guard will move.
     */
    private static final double COMBAT_SPEED = 1.0;

    /**
     * Potion velocity.
     */
    public static final float POTION_VELOCITY = 0.5f;

    /**
     * Flee chance
     */
    private static final int FLEE_CHANCE = 3;

    /**
     * The parent combat AI.
     */
    private final AbstractEntityAIGuard<JobDruid, AbstractBuildingGuards> parentAI;

    /**
     * The combat pathing options.
     */
    private final PathingOptions combatPathingOptions;

    /**
     * If the last attack was an instant effect potion.
     */
    private boolean instantEffect;

    public DruidCombatAI(
      final EntityCitizen owner,
      final ITickRateStateMachine stateMachine,
      final AbstractEntityAIGuard parentAI)
    {
        super(owner, stateMachine);

        stateMachine.addTransitionGroup(GUARD_ABORT_AND_FIGHT, new TickingTransition(this::checkForTarget, () -> CombatAIStates.ATTACKING, 5).withName("busy_checkTarget"));
        stateMachine.addTransitionGroup(GUARD_ABORT_AND_FIGHT, new TickingTransition(this::searchNearbyTarget, () -> CombatAIStates.ATTACKING, 80).withName("busy_searchTarget"));

        this.parentAI = parentAI;
        combatPathingOptions = new PathingOptions();
        combatPathingOptions.setEnterDoors(true);
        combatPathingOptions.setEnterGates(true);
        combatPathingOptions.setCanOpenDoors(true);
        combatPathingOptions.setCanSwim(true);
        combatPathingOptions.withOnPathCost(0.8);
        combatPathingOptions.withJumpCost(0.01);
        combatPathingOptions.withDropCost(1.5);
    }

    @Override
    protected void doAttack(final EntityLivingBase target)
    {
        if (user.distanceToSqr(target) < RANGED_FLEE_SQDIST)
        {
            if (user.getRandom().nextInt(FLEE_CHANCE) == 0 &&
                  !((AbstractBuildingGuards) user.getCitizenData().getWorkBuilding()).getTask().equals(GuardTaskSetting.GUARD))
            {
                EntityNavigationUtils.walkAwayFrom(user, target.blockPosition(), (int) (getAttackDistance() / 2.0), getCombatMovementSpeed());
            }
        }
        else
        {
            user.getNavigation().stop();
        }

        user.swing(0 /* InteractionHand.MAIN_HAND */);

        final int World = user.getCitizenData().getCitizenSkillHandler().getLevel(ModGuardTypes.druid.get().getSecondarySkill());
        final int time = user.getCitizenData().getCitizenSkillHandler().getLevel(ModGuardTypes.druid.get().getPrimarySkill()) * 20;

        final float inaccuracy = 99f / World;
        final MobEffect effect;
        final ItemStack stack = new ItemStack(Items.SPLASH_POTION);
        boolean gotMaterial = false;
        BiPredicate<EntityLivingBase, MobEffect> predicate;
        if (user.getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(DRUID_USE_POTIONS) > 0
              && InventoryUtils.hasItemInItemHandler(user.getInventoryCitizen(), item -> item.getItem() == ModItems.magicpotion))
        {
            gotMaterial = true;
        }
        if (AbstractEntityAIGuard.isAttackableTarget(user, target))
        {
            effect = ADVERSE_EFFECTS.get(user.getRandom().nextInt(gotMaterial ? 2 : 1));
            predicate = (entity, eff) -> AbstractEntityAIGuard.isAttackableTarget(user, entity);
        }
        else
        {
            effect = SUPPORT_EFFECTS.get(user.getRandom().nextInt(gotMaterial ? 4 : 1));
            predicate = (entity, eff) -> !AbstractEntityAIGuard.isAttackableTarget(user, entity);
        }

        PotionUtils.setCustomEffects(stack, Collections.singleton(new MobEffectInstance(effect, time, gotMaterial ? 2 : 0)));
        DruidPotionEntity.throwPotionAt(stack, target, user, user.getCommandSenderWorld(), POTION_VELOCITY, inaccuracy, predicate);

        if (gotMaterial)
        {
            InventoryUtils.removeStackFromItemHandler(user.getCitizenData().getInventory(), new ItemStack(ModItems.magicpotion, 1), 1);
        }

        this.instantEffect = effect.isInstantenous();

        user.setItemInHand(0 /* InteractionHand.MAIN_HAND */, stack);

        user.getThreatTable().removeCurrentTarget();

        user.getCitizenExperienceHandler().addExperience(PER_POTION_XP);
    }

    @Override
    protected int getAttackDelay()
    {
        return this.instantEffect ? super.getAttackDelay() * 2 : super.getAttackDelay();
    }

    @Override
    protected double getAttackDistance()
    {
        int attackDist = BASE_DISTANCE_FOR_POTION_ATTACK;
        // + 1 Blockrange per building World for a total of +5 from building World
        if (user.getCitizenData().getWorkBuilding() != null)
        {
            attackDist += user.getCitizenData().getWorkBuilding().getBuildingLevelEquivalent();
        }

        if (target != null)
        {
            attackDist += user.getY() - target.getY();
        }

        return attackDist;
    }

    @Override
    protected PathResult moveInAttackPosition(final EntityLivingBase target)
    {
        if (BlockPosUtil.getDistanceSquared(target.blockPosition(), user.blockPosition()) <= 4.0)
        {
            final PathJobMoveAwayFromLocation job = new PathJobMoveAwayFromLocation(user.World,
              PathfindingUtils.prepareStart(target),
              target.blockPosition(),
              12,
              (int) user.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
              user);
            final PathResult pathResult = ((MinecoloniesAdvancedPathNavigate) user.getNavigation()).setPathJob(job, null, getCombatMovementSpeed(), true);
            job.setPathingOptions(combatPathingOptions);
            return pathResult;
        }
        else if (BlockPosUtil.getDistance2D(target.blockPosition(), user.blockPosition()) >= 20)
        {
            final PathJobMoveToLocation job = new PathJobMoveToLocation(user.World, PathfindingUtils.prepareStart(user), target.blockPosition(), 200, user);
            final PathResult pathResult = ((MinecoloniesAdvancedPathNavigate) user.getNavigation()).setPathJob(job, null, getCombatMovementSpeed(), true);
            job.setPathingOptions(combatPathingOptions);
            return pathResult;
        }
        final PathJobCanSee job = new PathJobCanSee(user, target, user.World, ((AbstractBuildingGuards) user.getCitizenData().getWorkBuilding()).getGuardPos(user), 40);
        final PathResult pathResult = ((MinecoloniesAdvancedPathNavigate) user.getNavigation()).setPathJob(job, null, getCombatMovementSpeed(), true);
        job.setPathingOptions(combatPathingOptions);
        return pathResult;
    }

    /**
     * Get combat speed
     *
     * @return movent speed
     */
    protected double getCombatMovementSpeed()
    {
        double levelAdjustment = user.getCitizenData().getCitizenSkillHandler().getLevel(Skill.Mana) * SPEED_LEVEL_BONUS;
        levelAdjustment += (user.getCitizenData().getWorkBuilding().getBuildingLevelEquivalent() - 1) * SPEED_LEVEL_BONUS;

        levelAdjustment = Math.min(levelAdjustment, 0.3);
        return COMBAT_SPEED + levelAdjustment;
    }

    @Override
    protected boolean isAttackableTarget(final EntityLivingBase entity)
    {
        return (AbstractEntityAIGuard.isAttackableTarget(user, entity)
                  || (entity instanceof IThreatTableEntity && ((IThreatTableEntity) entity).getThreatTable().getTarget() != null)
                  || (entity instanceof Player && entity.getLastHurtByMobTimestamp() != 0 && entity.tickCount > entity.getLastHurtByMobTimestamp()
                        && entity.tickCount - entity.getLastHurtByMobTimestamp() < 20 * 30))
                 && !wasAffectedByDruid(entity);
    }

    @Override
    protected boolean searchNearbyTarget()
    {
        if (checkForTarget())
        {
            return true;
        }

        final List<EntityLivingBase> entities = user.World.getEntitiesOfClass(EntityLivingBase.class, getSearchArea());

        if (entities.isEmpty())
        {
            return false;
        }

        int targetsUnderEffect = 0;
        boolean foundTarget = false;
        for (final EntityLivingBase entity : entities)
        {
            if (!entity.isAlive())
            {
                continue;
            }

            if (skipSearch(entity))
            {
                return false;
            }

            if (isEntityValidTarget(entity))
            {
                if (user.hasLineOfSight(entity))
                {
                    user.getThreatTable().addThreat(entity, 0);
                    foundTarget = true;
                }
            }
            else if (wasAffectedByDruid(entity))
            {
                targetsUnderEffect++;
            }
        }

        return foundTarget && targetsUnderEffect <= parentAI.building.getBuildingLevelEquivalent() * 2;
    }

    /**
     * Check if an entity has one of the potion effects the druid hands out.
     *
     * @param entity the entity to check for.
     * @return true if so.
     */
    private boolean wasAffectedByDruid(final EntityLivingBase entity)
    {
        return entity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) || entity.hasEffect(MobEffects.SATURATION) || entity.hasEffect(MobEffects.DAMAGE_BOOST)
                 || entity.hasEffect(MobEffects.WEAKNESS) || entity.hasEffect(MobEffects.DAMAGE_RESISTANCE) || entity.hasEffect(MobEffects.HEAL);
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
        if (entity instanceof EntityCitizen && user.getRandom().nextInt(10) < 1)
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
    protected int getYSearchRange()
    {
        if (((AbstractBuildingGuards) user.getCitizenData().getWorkBuilding()).getTask().equals(GuardTaskSetting.GUARD))
        {
            return Y_VISION + 25;
        }

        return Y_VISION;
    }

    @Override
    protected void onTargetDied(final EntityLivingBase entity)
    {
        parentAI.incrementActionsDone();
        user.getCitizenExperienceHandler().addExperience(EXP_PER_MOB_DEATH);
        user.decreaseSaturationForContinuousAction();
    }
}





