package com.minecolonies.api.entity.mobs;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.entity.CustomGoalSelector;
import com.minecolonies.api.entity.ai.combat.CombatAIStates;
import com.minecolonies.api.entity.ai.combat.threat.IThreatTableEntity;
import com.minecolonies.api.entity.ai.combat.threat.ThreatTable;
import com.minecolonies.api.entity.ai.statemachine.states.IState;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.ITickRateStateMachine;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.TickRateStateMachine;
import com.minecolonies.api.entity.other.AbstractFastMinecoloniesEntity;
import com.minecolonies.api.entity.pathfinding.registry.IPathNavigateRegistry;
import com.minecolonies.api.sounds.RaiderSounds;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.MathUtils;
import com.minecolonies.core.entity.pathfinding.navigation.AbstractAdvancedPathNavigate;
import com.minecolonies.core.entity.pathfinding.navigation.PathingStuckHandler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static com.minecolonies.api.entity.citizen.AbstractEntityCitizen.ENTITY_AI_TICKRATE;
import static com.minecolonies.api.entity.mobs.RaiderMobUtils.MOB_ATTACK_DAMAGE;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_SPAWN_POS;
import static com.minecolonies.core.colony.events.raid.RaiderConstants.*;

/**
 * Abstract for all villain entities.
 * Ported from 1.21 to 1.7.10.
 */
public abstract class AbstractEntityMinecoloniesMonster extends AbstractFastMinecoloniesEntity implements IThreatTableEntity
{
    /**
     * The New PathNavigate navigator.
     */
    protected AbstractAdvancedPathNavigate newNavigator;

    /**
     * Goal selector wrapping this.tasks.
     */
    protected CustomGoalSelector goalSelector;

    /**
     * Target selector wrapping this.targetTasks.
     */
    protected CustomGoalSelector targetSelector;

    /**
     * The invulnerability timer for spawning, to prevent suffocate/grouping damage.
     */
    private int invulTime = 2 * 20;

    /**
     * Texture id of the raiders.
     */
    private int textureId;

    /**
     * Counts entity collisions
     */
    private int collisionCounter = 0;

    /**
     * The collision threshold
     */
    private static final int COLL_THRESHOLD = 50;

    /**
     * The threattable of the EntityCreature
     */
    private ThreatTable threatTable = new ThreatTable<>(this);

    /**
     * Raiders AI statemachine
     */
    private ITickRateStateMachine<IState> ai = new TickRateStateMachine<>(CombatAIStates.NO_TARGET, e -> Log.getLogger().warn(e), ENTITY_AI_TICKRATE);

    /**
     * Initial spawn pos of the entity (stored as int array [x, y, z]).
     */
    private int[] spawnPos = null;

    /**
     * Constructor method for Abstract minecolonies mobs.
     *
     * @param world the world.
     */
    public AbstractEntityMinecoloniesMonster(final World world)
    {
        super(world);
        this.isImmuneToFire = false;
        this.goalSelector = new CustomGoalSelector(this.tasks);
        this.targetSelector = new CustomGoalSelector(this.targetTasks);
        this.experienceValue = BARBARIAN_EXP_DROP;
        IMinecoloniesAPI.getInstance().getMobAIRegistry().applyToMob(this);
        RaiderMobUtils.setEquipment(this);
    }

    /**
     * Constructor method for Abstract minecolonies mobs with texture count.
     *
     * @param world        the world.
     * @param textureCount the texture count.
     */
    public AbstractEntityMinecoloniesMonster(final World world, final int textureCount)
    {
        this(world);
        this.textureId = MathUtils.RANDOM.nextInt(textureCount);
    }

    @Override
    public void applyEntityCollision(final Entity entityIn)
    {
        if (invulTime > 0)
        {
            return;
        }

        if ((collisionCounter += 3) > COLL_THRESHOLD)
        {
            if (collisionCounter > (COLL_THRESHOLD * 3))
            {
                collisionCounter = 0;
            }

            return;
        }

        super.applyEntityCollision(entityIn);
    }

    @Override
    public void playLivingSound()
    {
        super.playLivingSound();
        final String ambientSound = getAmbientSoundName();
        if (ambientSound != null && worldObj.rand.nextInt(OUT_OF_ONE_HUNDRED) <= ONE)
        {
            this.playSound(ambientSound, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    /**
     * Get the specific raider type of this raider.
     *
     * @return the type enum.
     */
    public abstract RaiderType getRaiderType();

    /**
     * Get the ambient sound name for this EntityCreature.
     *
     * @return sound resource name or null.
     */
    @Nullable
    public String getAmbientSoundName()
    {
        final Object sound = RaiderSounds.raiderSounds.get(getRaiderType()).get(RaiderSounds.RaiderSoundTypes.SAY);
        return sound != null ? sound.toString() : null;
    }

    @Override
    protected String getHurtSound()
    {
        final Object sound = RaiderSounds.raiderSounds.get(getRaiderType()).get(RaiderSounds.RaiderSoundTypes.HURT);
        return sound != null ? sound.toString() : "damage.hit";
    }

    @Override
    protected String getDeathSound()
    {
        final Object sound = RaiderSounds.raiderSounds.get(getRaiderType()).get(RaiderSounds.RaiderSoundTypes.DEATH);
        return sound != null ? sound.toString() : "EntityCreature.player.death";
    }

    @Nullable
    @Override
    protected String getLivingSound()
    {
        return getAmbientSoundName();
    }

    /**
     * Initializes entity stats for a given raidlevel and difficulty
     *
     * @param baseHealth basehealth for this raid/difficulty
     * @param difficulty difficulty
     * @param baseDamage basedamage for this raid/difficulty
     */
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(baseDamage);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(baseHealth);
        this.setHealth(this.getMaxHealth());
        // 1.7.10 has no armor attribute — damage absorption handled differently
    }

    @Override
    public void onLivingUpdate()
    {
        if (!this.isEntityAlive())
        {
            return;
        }

        if (this.spawnPos == null)
        {
            this.spawnPos = new int[] { (int) posX, (int) posY, (int) posZ };
        }

        if (collisionCounter > 0)
        {
            collisionCounter--;
        }

        if (invulTime > 0)
        {
            invulTime--;
        }

        if (worldObj.isRemote)
        {
            super.onLivingUpdate();
            return;
        }

        if (ticksExisted % ENTITY_AI_TICKRATE == 0)
        {
            ai.tick();
        }

        super.onLivingUpdate();
    }

    @Override
    public boolean attackEntityFrom(@NotNull final net.minecraft.util.DamageSource source, final float damage)
    {
        if (net.minecraft.util.DamageSource.getEntity() instanceof AbstractEntityMinecoloniesMonster)
        {
            return false;
        }

        if (net.minecraft.util.DamageSource.getEntity() instanceof EntityLivingBase)
        {
            final EntityLivingBase attacker = (EntityLivingBase) net.minecraft.util.DamageSource.getEntity();
            if (threatTable.getThreatFor(attacker) == -1)
            {
                final AxisAlignedBB area = AxisAlignedBB.getBoundingBox(posX - 10, posY - 2.5, posZ - 10, posX + 10, posY + 2.5, posZ + 10);
                @SuppressWarnings("unchecked")
                final List<AbstractEntityMinecoloniesMonster> nearby = worldObj.getEntitiesWithinAABB(AbstractEntityMinecoloniesMonster.class, area);
                for (final AbstractEntityMinecoloniesMonster monster : nearby)
                {
                    monster.threatTable.addThreat(attacker, 0);
                }
            }
            threatTable.addThreat(attacker, (int) damage);
        }

        return super.attackEntityFrom(net.minecraft.util.DamageSource, damage);
    }

    @Override
    protected boolean canDespawn()
    {
        // Raiders shouldn't despawn
        return false;
    }

    @Override
    public void writeEntityToNBT(final NBTTagCompound compound)
    {
        if (spawnPos != null)
        {
            final NBTTagCompound posTag = new NBTTagCompound();
            posTag.setInteger("x", spawnPos[0]);
            posTag.setInteger("y", spawnPos[1]);
            posTag.setInteger("z", spawnPos[2]);
            compound.setTag(TAG_SPAWN_POS, posTag);
        }
        super.writeEntityToNBT(compound);
    }

    @Override
    public void readEntityFromNBT(final NBTTagCompound compound)
    {
        if (compound.hasKey(TAG_SPAWN_POS))
        {
            final NBTTagCompound posTag = compound.getCompoundTag(TAG_SPAWN_POS);
            this.spawnPos = new int[] { posTag.getInteger("x"), posTag.getInteger("y"), posTag.getInteger("z") };
        }
        super.readEntityFromNBT(compound);
    }

    @Override
    public boolean isPushedByWater()
    {
        return false;
    }

    @Override
    public ThreatTable getThreatTable()
    {
        return threatTable;
    }

    /**
     * Get the AI machine
     *
     * @return ai statemachine
     */
    public ITickRateStateMachine<IState> getAI()
    {
        return ai;
    }

    @Override
    public int getTeamId()
    {
        // All raiders are in the same team
        return -1;
    }

    /**
     * Texture id of the EntityCreature.
     *
     * @return the texture id.
     */
    public int getTextureId()
    {
        return textureId;
    }

    /**
     * Getter for the initial spawn pos of the entity.
     *
     * @return the pos as int[] {x, y, z}.
     */
    public int[] getSpawnPos()
    {
        return this.spawnPos;
    }

    /**
     * Get the EntityCreature difficulty.
     *
     * @return difficulty
     */
    public double getDifficulty()
    {
        return 1;
    }

    /**
     * Get the swim speed factor.
     *
     * @return speed factor
     */
    public abstract double getSwimSpeedFactor();
}



