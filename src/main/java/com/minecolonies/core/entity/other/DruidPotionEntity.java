package com.minecolonies.core.entity.other;

import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.core.colony.jobs.JobDruid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * Druid potion entity — custom splash potion that applies effects based on a selection predicate.
 * [1.7.10] ThrownPotion → EntityPotion; MobEffectInstance → PotionEffect; MobEffect → Potion.
 */
public class DruidPotionEntity extends EntityPotion
{
    public static final double SPLASH_SIZE  = 4.0D;
    public static final double SPLASH_HEIGTH = 2.0D;
    public static final double MAX_DISTANCE = 16.0D;
    public static final int    MIN_DURATION  = 20;

    @Nullable
    private BiPredicate<EntityLivingBase, Potion> entitySelectionPredicate = null;

    /** [1.7.10] Owning citizen stored directly — EntityPotion.thrower holds EntityLivingBase. */
    @Nullable
    private AbstractEntityCitizen ownerCitizen = null;

    public DruidPotionEntity(final World world)
    {
        super(world, null, new ItemStack(net.minecraft.init.Items.splash_potion));
    }

    public DruidPotionEntity(final World world, final AbstractEntityCitizen thrower, final ItemStack potion)
    {
        super(world, thrower, potion);
        this.ownerCitizen = thrower;
    }

    public void setEntitySelectionPredicate(@Nullable final BiPredicate<EntityLivingBase, Potion> entitySelectionPredicate)
    {
        this.entitySelectionPredicate = entitySelectionPredicate;
    }

    /**
     * [1.7.10] Override splash logic from EntityPotion to apply our predicate.
     * EntityPotion.onImpact handles splash; we override onUpdate to intercept.
     * Note: full override of applySplash is done by overriding onImpact below.
     */
    @Override
    protected void func_70665_d(final List<PotionEffect> effects, @Nullable final Entity directHit)
    {
        final AbstractEntityCitizen citizen = ownerCitizen;
        if (citizen != null && citizen.getCitizenData() != null && citizen.getCitizenData().getJob() instanceof JobDruid)
        {
            final AxisAlignedBB aabb = boundingBox.expand(SPLASH_SIZE, SPLASH_HEIGTH, SPLASH_SIZE);
            @SuppressWarnings("unchecked")
            final List<EntityLivingBase> list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);

            for (final EntityLivingBase target : list)
            {
                if (target.isPotionApplicable(new PotionEffect(0, 0)))
                {
                    final double distanceSq = getDistanceSqToEntity(target);
                    if (distanceSq < MAX_DISTANCE)
                    {
                        double d1 = 1.0D - Math.sqrt(distanceSq) / 4.0D;
                        if (target == directHit)
                        {
                            d1 = 1.0D;
                        }

                        for (final PotionEffect effectInstance : effects)
                        {
                            final Potion potion = Potion.potionTypes[effectInstance.getPotionID()];
                            if (potion == null) continue;
                            if (entitySelectionPredicate == null || entitySelectionPredicate.test(target, potion))
                            {
                                if (potion.isInstant())
                                {
                                    potion.affectEntity(this, citizen, target, effectInstance.getAmplifier(), d1);
                                }
                                else
                                {
                                    final int duration = (int) (d1 * effectInstance.getDuration());
                                    if (duration >= MIN_DURATION)
                                    {
                                        target.addPotionEffect(new PotionEffect(effectInstance.getPotionID(), duration, effectInstance.getAmplifier(), effectInstance.getIsAmbient()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else
        {
            super.func_70665_d(effects, directHit);
        }
    }

    /**
     * Throws a potion at the target.
     */
    public static void throwPotionAt(
        final ItemStack potionStack,
        final EntityLivingBase target,
        final AbstractEntityCitizen thrower,
        final World world,
        final float velocity,
        final float inaccuracy,
        final BiPredicate<EntityLivingBase, Potion> entitySelectionPredicate)
    {
        final DruidPotionEntity potionEntity = new DruidPotionEntity(world, thrower, potionStack);
        potionEntity.setEntitySelectionPredicate(entitySelectionPredicate);
        potionEntity.setPosition(thrower.posX, thrower.posY + 1, thrower.posZ);

        world.playSoundAtEntity(thrower, "mob.witch.throw", 1.0F, 0.8F + thrower.getRNG().nextFloat() * 0.4F);

        final double motX = target.motionX;
        final double motZ = target.motionZ;
        double x = target.posX + motX - thrower.posX;
        double y = target.posY + target.getEyeHeight() - 1.1F - thrower.posY;
        double z = target.posZ + motZ - thrower.posZ;
        final double dist = Math.sqrt(x * x + z * z);

        potionEntity.func_70186_c(x, y + dist * 0.2, z, velocity, inaccuracy);
        world.spawnEntityInWorld(potionEntity);
    }
}
