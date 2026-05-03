package com.minecolonies.api.entity.citizen;

import com.minecolonies.api.colony.ICivilianData;
import com.minecolonies.api.entity.other.AbstractFastMinecoloniesEntity;
import com.minecolonies.api.entity.other.MinecoloniesMinecart;
import com.minecolonies.core.entity.other.SittingEntity;
import com.minecolonies.core.entity.other.cavalry.CavalryHorseEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.sounds.EventType.GREETING;
import static com.minecolonies.api.util.SoundUtils.playSoundAtCitizenWith;
import static com.minecolonies.api.util.constant.Constants.TICKS_SECOND;

/**
 * Base class for civilian entities (citizens, visitors).
 * In 1.7.10 there is no Npc interface — it is just EntityCreature via AbstractFastMinecoloniesEntity.
 */
public abstract class AbstractCivilianEntity extends AbstractFastMinecoloniesEntity
{
    /**
     * Time after which the next player collision is possible
     */
    protected long nextPlayerCollisionTime = 0;

    /**
     * Create a new instance.
     *
     * @param worldIn the world.
     */
    protected AbstractCivilianEntity(final World worldIn)
    {
        super(worldIn);
    }

    /**
     * Setter for the citizen data.
     *
     * @param data the data to set.
     */
    public abstract void setCivilianData(@Nullable ICivilianData data);

    /**
     * Getter for the citizen data.
     *
     * @return civilian data
     */
    public abstract ICivilianData getCivilianData();

    /**
     * Mark the citizen dirty to synch the data with the client.
     *
     * @param time the time interval.
     */
    public abstract void markDirty(final int time);

    /**
     * Getter for the civilian id.
     *
     * @return the id.
     */
    public abstract int getCivilianID();

    /**
     * Setter for the civilian id.
     *
     * @param id the id to set.
     */
    public abstract void setCitizenId(int id);

    @Override
    public void applyEntityCollision(final Entity entityIn)
    {
        if (entityIn instanceof EntityPlayerMP)
        {
            onPlayerCollide((EntityPlayer) entityIn);
        }
        super.applyEntityCollision(entityIn);
    }

    /**
     * On player collision action.
     *
     * @param player the colliding player.
     */
    public void onPlayerCollide(final EntityPlayer player)
    {
        if (worldObj.getTotalWorldTime() > nextPlayerCollisionTime)
        {
            nextPlayerCollisionTime = worldObj.getTotalWorldTime() + TICKS_SECOND * 15;
            getNavigator().clearPathEntity();
            getLookHelper().setLookPositionWithEntity(player, 10.0F, 10.0F);

            playSoundAtCitizenWith(worldObj, (int) posX, (int) posY, (int) posZ, GREETING, getCivilianData());
        }
    }

    /**
     * Queue a sound at the citizen.
     *
     * @param soundName   the sound resource name.
     * @param x           x position.
     * @param y           y position.
     * @param z           z position.
     * @param length      the length of the event.
     * @param repetitions the number of times to play it.
     */
    public abstract void queueSound(@NotNull final String soundName, final int x, final int y, final int z, final int length, final int repetitions);

    /**
     * Queue a sound at the citizen with volume and pitch.
     *
     * @param soundName   the sound resource name.
     * @param x           x position.
     * @param y           y position.
     * @param z           z position.
     * @param length      the length of the event.
     * @param repetitions the number of times to play it.
     * @param volume      the volume.
     * @param pitch       the pitch.
     */
    public abstract void queueSound(@NotNull final String soundName, final int x, final int y, final int z, final int length, final int repetitions, final float volume, final float pitch);

    @Override
    public String toString()
    {
        final ICivilianData data = getCivilianData();
        final String id = data == null ? "none" : "" + data.getId();
        final String colony = data == null ? "none" : "" + data.getColony().getName();
        return "Entity: " + getCommandSenderName() + " Type: [" + getClass().getSimpleName() + "] at pos: (" + (int) posX + "," + (int) posY + "," + (int) posZ + ") civilian id: " + id + " colony: " + colony;
    }

    /**
     * Prevent riding entities except ours.
     *
     * @param entity entity to ride on
     * @return true if successful.
     */
    @Override
    public boolean mountEntity(final Entity entity)
    {
        if (entity instanceof SittingEntity || entity instanceof MinecoloniesMinecart || entity instanceof CavalryHorseEntity)
        {
            return super.mountEntity(entity);
        }
        return false;
    }
}
