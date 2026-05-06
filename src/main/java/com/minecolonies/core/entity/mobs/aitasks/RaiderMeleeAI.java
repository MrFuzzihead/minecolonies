package com.minecolonies.core.entity.mobs.aitasks;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.entity.ai.combat.threat.IThreatTableEntity;
import com.minecolonies.api.entity.ai.statemachine.states.IState;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.ITickRateStateMachine;
import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.SoundUtils;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.colony.events.raid.RaiderConstants;
import com.minecolonies.core.entity.ai.combat.AttackMoveAI;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import com.minecolonies.core.entity.pathfinding.navigation.EntityNavigationUtils;
import com.minecolonies.core.entity.pathfinding.pathresults.PathResult;
// [1.7.10] Registries removed
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
// [1.7.10] sounds removed
// [1.7.10] int /* InteractionHand */ removed
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import static com.minecolonies.api.entity.mobs.RaiderMobUtils.MOB_ATTACK_DAMAGE;
import static com.minecolonies.core.colony.events.raid.RaiderConstants.*;

/**
 * Raider AI for melee attacking a target
 */
public class RaiderMeleeAI<T extends AbstractEntityMinecoloniesMonster & IThreatTableEntity> extends AttackMoveAI<T>
{
    public RaiderMeleeAI(
      final T owner,
      final ITickRateStateMachine<IState> stateMachine)
    {
        super(owner, stateMachine);
    }

    @Override
    protected void doAttack(final EntityLivingBase target)
    {
        double damageToBeDealt = user.getAttribute(MOB_ATTACK_DAMAGE.get()).getValue();
        if (user.getName().getContents() instanceof TranslatableContents translatableContents)
        {
            target.hurt(target.World.damageSources().source(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, translatableContents.getKey().replace("entity.minecolonies.", ""))), user), (float) damageToBeDealt);
        }
        else
        {
            target.hurt(target.World.damageSources().mobAttack(user), (float) damageToBeDealt);
        }
        user.swing(0 /* InteractionHand.MAIN_HAND */);
        user.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, (float) 1.0D, (float) SoundUtils.getRandomPitch(user.getRandom()));
        target.setLastHurtByMob(user);
    }

    @Override
    protected double getAttackDistance()
    {
        return user.getDifficulty() < EXTENDED_REACH_DIFFICULTY ? MIN_DISTANCE_FOR_ATTACK : MIN_DISTANCE_FOR_ATTACK + EXTENDED_REACH;
    }

    @Override
    protected int getAttackDelay()
    {
        return MELEE_ATTACK_DELAY;
    }

    @Override
    protected PathResult moveInAttackPosition(final EntityLivingBase target)
    {
        EntityNavigationUtils.walkToPos(user,
            target.blockPosition(),
            (int) getAttackDistance(),
            false,
            user.getDifficulty() < ADD_SPEED_DIFFICULTY ? BASE_COMBAT_SPEED : BASE_COMBAT_SPEED * BONUS_SPEED);
        return user.getNavigation().getPathResult();
    }

    @Override
    protected boolean isAttackableTarget(final EntityLivingBase target)
    {
        return (target instanceof EntityCitizen && !target.isInvisible()) || (target instanceof Player && !((Player) target).isCreative() && !target.isSpectator());
    }

    @Override
    protected boolean isWithinPersecutionDistance(final EntityLivingBase target)
    {
        return BlockPosUtil.getDistanceSquared(user.blockPosition(), target.blockPosition()) <= RaiderConstants.MAX_MELEE_RAIDER_PERSECUTION_DISTANCE * RaiderConstants.MAX_MELEE_RAIDER_PERSECUTION_DISTANCE;
    }

    @Override
    protected int getSearchRange()
    {
        return 0;
    }
}





