package com.minecolonies.core.entity.other;

import com.minecolonies.api.entity.mobs.ICustomAttackSound;
import com.minecolonies.api.items.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Custom arrow entity used for spear throwing.
 * [1.7.10] ThrownTrident does not exist; uses EntityArrow as base.
 */
public class SpearEntity extends EntityArrow implements ICustomAttackSound
{
    private static final int MAX_LIVE_TIME    = 10 * 20;
    private static final int GROUND_LIVE_TIME = 2 * 20;

    public static final int    BASE_DAMAGE       = 4;
    public static final String NBT_WEAPON        = "Weapon";
    public static final String NBT_DEALT_DAMAGE  = "DealtDamage";

    protected ItemStack weapon      = new ItemStack(ModItems.spear);
    private   boolean   dealtDamage = false;

    public SpearEntity(final World world)
    {
        super(world);
    }

    public SpearEntity(final World world, final EntityLivingBase thrower, final ItemStack thrownWeapon)
    {
        super(world, thrower, 2.5f);
        this.weapon = thrownWeapon.copy();
        this.setPosition(thrower.posX, thrower.posY + thrower.getEyeHeight() - 0.1, thrower.posZ);
    }

    @NotNull
    @Override
    public ItemStack func_145748_c_()
    {
        return this.weapon.copy();
    }

    /**
     * [1.7.10] EntityArrow.onUpdate() handles collision.
     * We override to add per-entity-hit logic via attackEntityFrom on target.
     * Full EntityHitResult-based hooks are not available.
     */
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

    @Override
    protected float getWaterInertia()
    {
        return 0.9F;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldRenderInPass(final int pass)
    {
        return true;
    }

    @Override
    public String getAttackSound()
    {
        return "random.bow";  // [1.7.10] placeholder for trident throw sound
    }

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
}
