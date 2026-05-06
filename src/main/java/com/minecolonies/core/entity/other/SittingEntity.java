package com.minecolonies.core.entity.other;
import net.minecraft.util.Direction;

import com.minecolonies.api.entity.ModEntities;
import com.minecolonies.api.entity.other.AbstractFastMinecoloniesEntity;
import com.minecolonies.api.util.EntityUtils;
import com.minecolonies.api.util.LookHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Entity used to sit on, for animation purposes.
 */
public class SittingEntity extends Entity
{
    int maxLifeTime = 100;
    private int[] sittingpos = new int[]{0, 0, 0};

    /** [1.7.10] dimension keyed by int dimensionId */
    private static Map<Integer, Set<int[]>> existingSittingEntities = new HashMap<>();

    public SittingEntity(final World worldIn)
    {
        super(worldIn);
        this.setInvisible(true);
        this.noClip = true;
        this.gravityActive = false;
    }

    public SittingEntity(final World worldIn, final double x, final double y, final double z, final int lifeTime)
    {
        super(worldIn);
        this.setPosition(x, y, z);
        this.setInvisible(true);
        this.noClip = true;
        this.gravityActive = false;
        this.maxLifeTime = lifeTime;
    }

    @Override
    public boolean attackEntityFrom(final DamageSource source, final float amount)
    {
        return false;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @Override
    protected void readEntityFromNBT(final NBTTagCompound compound) {}

    @Override
    protected void writeEntityToNBT(final NBTTagCompound compound) {}

    @Override
    protected void entityInit() {}

    @Override
    public void setDead()
    {
        super.setDead();
        existingSittingEntities.getOrDefault(worldObj.provider.dimensionId, new HashSet<>()).remove(sittingpos);
    }

    @Override
    public void onUpdate()
    {
        if (this.worldObj.isRemote)
        {
            return;
        }

        // [1.7.10] isVehicle() → riddenByEntity != null
        if (this.riddenByEntity == null || maxLifeTime-- < 0)
        {
            if (riddenByEntity != null)
            {
                riddenByEntity.mountEntity(null);
            }
            this.setDead();
        }
    }

    // [1.7.10] addPassenger/removePassenger don't exist; riding handled via mountEntity

    public void setMaxLifeTime(final int maxLifeTime)
    {
        this.maxLifeTime = maxLifeTime;
    }

    public void setSittingPos(final int[] pos)
    {
        sittingpos = pos;
    }

    public static boolean isSittingPosOccupied(final int[] pos, final World world)
    {
        return existingSittingEntities.computeIfAbsent(world.provider.dimensionId, k -> new HashSet<>()).contains(pos);
    }

    /**
     * Makes the given entity sit down onto a new sitting entity.
     */
    public static boolean sitDown(final int[] pos, final EntityCreature entity, final int maxLifeTime)
    {
        if (entity.ridingEntity != null)
        {
            return true;
        }

        if (existingSittingEntities.getOrDefault(entity.worldObj.provider.dimensionId, new HashSet<>()).contains(pos))
        {
            return false;
        }
        existingSittingEntities.computeIfAbsent(entity.worldObj.provider.dimensionId, k -> new HashSet<>()).add(pos);

        final SittingEntity sittingEntity = new SittingEntity(entity.worldObj);

        // [1.7.10] Block collision shape → use 1.0 default seat height; full shape API not available
        double minY = 1.0;
        // TODO: inspect block at pos to find lower collision box (stair, slab) for accurate sit height

        entity.getNavigator().clearPathEntity();
        sittingEntity.setPosition(pos[0] + 0.5, (pos[1] + minY) - entity.height / 2 - 0.1, pos[2] + 0.5);
        sittingEntity.setMaxLifeTime(maxLifeTime);
        sittingEntity.setSittingPos(pos);
        entity.worldObj.spawnEntityInWorld(sittingEntity);
        entity.mountEntity(sittingEntity);

        // [1.7.10] StairBlock facing look-at — not yet ported
        // TODO: check if block is a stair and set look direction

        return true;
    }
}
