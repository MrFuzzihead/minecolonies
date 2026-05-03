package com.minecolonies.api.entity.mobs;

import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.colonyEvents.IColonyCampFireRaidEvent;
import com.minecolonies.api.colony.colonyEvents.IColonyEvent;
import com.minecolonies.api.enchants.ModEnchants;
import com.minecolonies.api.entity.CustomGoalSelector;
import com.minecolonies.api.entity.ai.combat.threat.IThreatTableEntity;
import com.minecolonies.api.entity.pathfinding.registry.IPathNavigateRegistry;
import com.minecolonies.api.items.IChiefSwordItem;
import com.minecolonies.api.util.ColonyUtils;
import com.minecolonies.core.entity.pathfinding.navigation.AbstractAdvancedPathNavigate;
import com.minecolonies.core.entity.pathfinding.navigation.PathingStuckHandler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static com.minecolonies.api.util.constant.ColonyManagerConstants.NO_COLONY_ID;
import static com.minecolonies.api.util.constant.NbtTagConstants.*;
import static com.minecolonies.core.colony.events.raid.RaiderConstants.*;

/**
 * Abstract for all raider entities.
 * Ported from 1.21 to 1.7.10.
 */
public abstract class AbstractEntityMinecoloniesRaider extends AbstractEntityMinecoloniesMonster implements IThreatTableEntity
{
    private static final float HP_PERCENT_PER_DMG = 0.03f;
    private static final int   MAX_SCALED_DAMAGE   = 7;
    private static final float MIN_THORNS_DAMAGE   = 30;
    private static final int   THORNS_CHANCE       = 5;
    private static final int   COLONY_SET_RAIDED_CHANCE = 20;
    private static final int   ENV_DAMAGE_COOLDOWN = 30;

    /** Sets the barbarians target colony on spawn. */
    private IColony colony;

    private int  chiefSpeedCooldown = 0;
    private long worldTimeAtSpawn   = 0;
    private int  currentTick        = 0;
    private int  eventID            = 0;
    private boolean isRegistered    = false;

    /** Invulnerability timer for spawning. */
    private int invulTime = 2 * 20;

    private int envDmgCooldown        = 0;
    private boolean tempEnvDamageImmunity = true;

    private int    collisionCounter = 0;
    private double difficulty       = 1.0d;

    /** Last chunk coordinates (x, z). */
    private int lastChunkX = Integer.MIN_VALUE;
    private int lastChunkZ = Integer.MIN_VALUE;

    /**
     * Constructor for abstract raiders.
     *
     * @param world the world.
     */
    public AbstractEntityMinecoloniesRaider(final World world)
    {
        this(world, 1);
    }

    /**
     * Constructor for abstract raiders with texture count.
     *
     * @param world        the world.
     * @param textureCount the texture variant count.
     */
    public AbstractEntityMinecoloniesRaider(final World world, final int textureCount)
    {
        super(world, textureCount);
        RaiderMobUtils.setEquipment(this);
    }

    @NotNull
    public AbstractAdvancedPathNavigate getAdvancedNavigator()
    {
        if (this.newNavigator == null)
        {
            this.newNavigator = IPathNavigateRegistry.getInstance().getNavigateFor(this);
            this.newNavigator.setCanFloat(true);
            newNavigator.setSwimSpeedFactor(getSwimSpeedFactor());
            newNavigator.getPathingOptions().setEnterDoors(true);
            newNavigator.getPathingOptions().withDropCost(1D);
            newNavigator.getPathingOptions().withJumpCost(1D);
            newNavigator.getPathingOptions().setPassDanger(true);
            PathingStuckHandler stuckHandler = PathingStuckHandler.createStuckHandler()
                .withTakeDamageOnStuck(0.4f)
                .withBuildLeafBridges()
                .withChanceToByPassMovingAway(0.20)
                .withPlaceLadders();

            if (MinecoloniesAPIProxy.getInstance().getConfig().getServer().raidersbreakblocks.get())
            {
                stuckHandler.withBlockBreaks();
                stuckHandler.withCompleteStuckBlockBreak(6);
            }

            newNavigator.setStuckHandler(stuckHandler);
        }
        return newNavigator;
    }

    /**
     * Get the specific raider type of this raider.
     *
     * @return the type enum.
     */
    public abstract RaiderType getRaiderType();

    /**
     * Should the raider despawn.
     *
     * @return true if so.
     */
    private boolean shouldDespawn()
    {
        return worldTimeAtSpawn != 0 && (worldObj.getTotalWorldTime() - worldTimeAtSpawn) >= TICKS_TO_DESPAWN;
    }

    @Override
    public void writeEntityToNBT(final NBTTagCompound compound)
    {
        compound.setLong(TAG_TIME, worldTimeAtSpawn);
        compound.setInteger(TAG_COLONY_ID, this.colony == null ? 0 : colony.getID());
        compound.setInteger(TAG_EVENT_ID, eventID);
        super.writeEntityToNBT(compound);
    }

    @Override
    public void readEntityFromNBT(final NBTTagCompound compound)
    {
        worldTimeAtSpawn = compound.getLong(TAG_TIME);
        eventID = compound.getInteger(TAG_EVENT_ID);
        if (compound.hasKey(TAG_COLONY_ID))
        {
            final int colonyId = compound.getInteger(TAG_COLONY_ID);
            if (colonyId != 0)
            {
                setColony(IColonyManager.getInstance().getColonyByWorld(colonyId, worldObj));
            }
        }

        if (colony == null || eventID == 0)
        {
            this.setDead();
        }

        super.readEntityFromNBT(compound);
    }

    @Override
    public void onLivingUpdate()
    {
        if (!this.isEntityAlive())
        {
            return;
        }

        if (invulTime > 0)
        {
            invulTime--;
        }

        if (collisionCounter > 0)
        {
            collisionCounter--;
        }

        if (envDmgCooldown > 0)
        {
            envDmgCooldown--;
        }

        if (worldObj.isRemote)
        {
            super.onLivingUpdate();
            return;
        }

        if (++currentTick % (rand.nextInt(EVERY_X_TICKS) + 1) == 0)
        {
            if (worldTimeAtSpawn == 0)
            {
                worldTimeAtSpawn = worldObj.getTotalWorldTime();
            }

            final int chunkX = (int) posX >> 4;
            final int chunkZ = (int) posZ >> 4;
            if (chunkX != lastChunkX || chunkZ != lastChunkZ)
            {
                lastChunkX = chunkX;
                lastChunkZ = chunkZ;
                if (rand.nextInt(COLONY_SET_RAIDED_CHANCE) <= 0)
                {
                    onEnterChunk(chunkX, chunkZ);
                }
            }

            if (shouldDespawn())
            {
                this.setDead();
                return;
            }

            if (!isRegistered)
            {
                registerWithColony();
            }

            if (--chiefSpeedCooldown <= 0)
            {
                chiefSpeedCooldown = TIME_TO_COUNTDOWN;

                final net.minecraft.item.ItemStack mainHand = this.getHeldItem();
                if (mainHand != null && mainHand.getItem() instanceof IChiefSwordItem && difficulty > CHIEF_SWORD_SPEED_DIFFICULTY)
                {
                    for (AbstractEntityMinecoloniesRaider entity : RaiderMobUtils.getBarbariansCloseToEntity(this, SPEED_EFFECT_DISTANCE))
                    {
                        if (!entity.isPotionActive(Potion.moveSpeed))
                        {
                            entity.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, SPEED_EFFECT_DURATION, SPEED_EFFECT_MULTIPLIER));
                        }
                    }
                }
            }
        }

        if (isRegistered)
        {
            super.onLivingUpdate();
        }
    }

    /**
     * Event on when a raider entered a new chunk.
     *
     * @param chunkX the chunk x coordinate.
     * @param chunkZ the chunk z coordinate.
     */
    private void onEnterChunk(final int chunkX, final int chunkZ)
    {
        if (colony == null)
        {
            return;
        }
        final Chunk chunk = colony.getWorld().getChunkFromChunkCoords(chunkX, chunkZ);
        final int owningColonyId = ColonyUtils.getOwningColony(chunk);
        if (owningColonyId != NO_COLONY_ID && colony.getID() != owningColonyId)
        {
            final IColony tempColony = IColonyManager.getInstance().getColonyByWorld(owningColonyId, worldObj);
            if (tempColony != null)
            {
                tempColony.getRaiderManager().setPassThroughRaid();
            }
        }
    }

    @Override
    protected void onDeathUpdate()
    {
        super.onDeathUpdate();
        if (!worldObj.isRemote && getColony() != null)
        {
            getColony().getEventManager().onEntityDeath(this, eventID);
        }
    }

    @Override
    public void onEntityUpdate()
    {
        // Unregister from colony when removed.
        if (isDead && !worldObj.isRemote && colony != null && eventID > 0)
        {
            colony.getEventManager().unregisterEntity(this, eventID);
        }
        super.onEntityUpdate();
    }

    @Override
    public boolean attackEntityFrom(@NotNull final net.minecraft.util.DamageSource source, final float damage)
    {
        if (!(net.minecraft.util.DamageSource.getEntity() instanceof EntityLivingBase))
        {
            if (tempEnvDamageImmunity)
            {
                return false;
            }

            if (envDmgCooldown > 0)
            {
                return false;
            }

            final float minimumHealthPct = getMinRemainingHealthForEnvironmentalDamage((float) difficulty);
            final float healthLeftPercent = (getHealth() - damage) / getMaxHealth();
            if (minimumHealthPct > healthLeftPercent)
            {
                return false;
            }

            envDmgCooldown = ENV_DAMAGE_COOLDOWN;
        }
        else if (!worldObj.isRemote)
        {
            if (colony != null)
            {
                final IColonyEvent event = colony.getEventManager().getEventByID(eventID);
                if (event instanceof IColonyCampFireRaidEvent)
                {
                    ((IColonyCampFireRaidEvent) event).setCampFireTime(0);
                }
            }

            final net.minecraft.entity.Entity source = net.minecraft.util.DamageSource.getEntity();
            if (source instanceof EntityPlayer)
            {
                final EntityPlayer player = (EntityPlayer) source;
                if (damage > MIN_THORNS_DAMAGE && rand.nextInt(THORNS_CHANCE) == 0)
                {
                    source.attackEntityFrom(net.minecraft.util.DamageSource.causeThornsDamage(this), damage * 0.5f);
                }

                // TODO: ModEnchants.raiderDamage equivalent for 1.7.10
                final float baseScalingDamage = Math.min(damage, MAX_SCALED_DAMAGE);
                final float totalWithScaled = Math.max(damage, (damage - baseScalingDamage) + baseScalingDamage * HP_PERCENT_PER_DMG * this.getMaxHealth());
                return super.attackEntityFrom(net.minecraft.util.DamageSource, totalWithScaled);
            }
        }

        return super.attackEntityFrom(net.minecraft.util.DamageSource, damage);
    }

    /**
     * Calculates the minimum remaining health percentage for taking environmental damage.
     *
     * @param difficulty the current difficulty.
     * @return minimum health fraction to remain.
     */
    protected float getMinRemainingHealthForEnvironmentalDamage(final float difficulty)
    {
        return Math.min(((difficulty) / 10) + 0.2f, 0.6f);
    }

    /**
     * Getter for the colony.
     *
     * @return the colony the raider is assigned to attack.
     */
    public IColony getColony()
    {
        return colony;
    }

    /**
     * Set the colony to raid.
     *
     * @param colony the colony to set.
     */
    public void setColony(final IColony colony)
    {
        if (colony != null)
        {
            this.colony = colony;
        }
    }

    /**
     * Registers the entity with the colony.
     */
    public void registerWithColony()
    {
        if (colony == null || eventID == 0 || isDead)
        {
            this.setDead();
            return;
        }
        RaiderMobUtils.setMobAttributes(this, getColony());
        colony.getEventManager().registerEntity(this, eventID);
        isRegistered = true;
    }

    public int getEventID()
    {
        return eventID;
    }

    public void setEventID(final int eventID)
    {
        this.eventID = eventID;
    }

    /**
     * Sets the temporary immunity to environmental damage.
     *
     * @param immunity whether immune.
     */
    public void setTempEnvDamageImmunity(final boolean immunity)
    {
        tempEnvDamageImmunity = immunity;
    }

    @Override
    public void initStatsFor(final double baseHealth, final double difficulty, final double baseDamage)
    {
        super.initStatsFor(baseHealth, difficulty, baseDamage);
        this.difficulty = difficulty;
    }

    @Override
    public double getDifficulty()
    {
        return difficulty;
    }

    @Override
    public int getTeamId()
    {
        return -1;
    }
}



