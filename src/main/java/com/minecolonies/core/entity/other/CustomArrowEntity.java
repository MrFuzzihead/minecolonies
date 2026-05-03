package com.minecolonies.core.entity.other;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Custom arrow entity class which removes itself when on the ground for a bit
 * to avoid lag and does not scale damage with motion.
 */
public class CustomArrowEntity extends EntityArrow
{
    private static final int MAX_LIVE_TIME    = 10 * 20;
    private static final int GROUND_LIVE_TIME = 2 * 20;

    private boolean armorPiercePlayer = false;
    private float   waterInertia      = 0.6f;

    // [1.7.10] EntityHitResult doesn't exist; callback omitted for now
    // private Predicate<EntityHitResult> onHitCallback = null;

    public CustomArrowEntity(final World world)
    {
        super(world);
    }

    public CustomArrowEntity(final World world, final EntityLivingBase shooter)
    {
        super(world, shooter, 2.0f);
    }

    // [1.7.10] doPostHurtEffects → onArrowHit is not standard; override onUpdate for custom behavior

    @Override
    protected float getWaterInertia()
    {
        return waterInertia;
    }

    public void setWaterInertia(final float waterInertia)
    {
        this.waterInertia = waterInertia;
    }

    // [1.7.10] onHitEntity(EntityHitResult) doesn't exist.
    // In 1.7.10, arrow damage logic is in EntityArrow.onUpdate() via attackEntityFrom.
    // armorPiercePlayer and damage capping hooks kept as stubs.

    public void setPlayerArmorPierce()
    {
        armorPiercePlayer = true;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    // [1.7.10] save/load → writeEntityToNBT/readEntityFromNBT on EntityArrow
    @Override
    public void writeEntityToNBT(final NBTTagCompound nbt)
    {
        // Do not save — transient projectile
    }

    @Override
    public void readEntityFromNBT(final NBTTagCompound nbt)
    {
        setDead();
    }

    @Override
    public void onUpdate()
    {
        if (this.ticksExisted > MAX_LIVE_TIME)
        {
            setDead();
            return;
        }

        if (this.inGround && this.timeInGround > GROUND_LIVE_TIME)
        {
            setDead();
            return;
        }

        super.onUpdate();
    }
}
