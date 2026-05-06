package com.minecolonies.api.entity.ai.combat.threat;

import net.minecraft.entity.EntityLivingBase;

import java.util.Objects;

/**
 * Data entry in the threat table
 */
public class ThreatTableEntry
{
    /**
     * Threat value
     */
    private int threat = 10;

    /**
     * Entity which caused the threat
     */
    private final EntityLivingBase entity;

    /**
     * Time at which it was last seen
     */
    private long lastSeen;

    public ThreatTableEntry(final EntityLivingBase entity)
    {
        this.entity = Objects.requireNonNull(entity);
        this.lastSeen = entity.worldObj.getTotalWorldTime();
    }

    /**
     * Adds threat
     */
    protected void addThreat(final int threat)
    {
        if (threat == 0)
        {
            return;
        }

        this.threat = Math.max(0, this.threat + threat);
        lastSeen = entity.worldObj.getTotalWorldTime(); // [1.7.10]
    }

    /**
     * Set the threat value directly
     *
     * @param threat
     */
    protected void setThreat(final int threat)
    {
        this.threat = threat;
    }

    /**
     * Get the threat value
     *
     * @return
     */
    public int getThreat()
    {
        return threat;
    }

    /**
     * Get the target entity
     *
     * @return target
     */
    public EntityLivingBase getEntity()
    {
        return entity;
    }

    /**
     * Get the worldtime of where it was last seen
     *
     * @return
     */
    public long getLastSeen()
    {
        return lastSeen;
    }

    /**
     * Set the world time of when it was last seen
     *
     * @param gameTime
     */
    public void setLastSeen(final long gameTime)
    {
        this.lastSeen = gameTime;
    }
}


