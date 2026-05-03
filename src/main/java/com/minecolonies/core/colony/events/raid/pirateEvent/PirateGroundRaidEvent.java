package com.minecolonies.core.colony.events.raid.pirateEvent;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.colonyEvents.EventStatus;
import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesRaider;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.colony.events.raid.HordeRaidEvent;
import com.minecolonies.core.entity.mobs.raider.pirates.EntityArcherPirateRaider;
import com.minecolonies.core.entity.mobs.raider.pirates.EntityCaptainPirateRaider;
import com.minecolonies.core.entity.mobs.raider.pirates.EntityPirateRaider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
// [1.7.10] world.entity removed
import net.minecraft.entity.EntityLivingBase;

import static com.minecolonies.api.entity.ModEntities.*;
import static com.minecolonies.api.util.constant.TranslationConstants.RAID_PIRATE;

/**
 * The Pirate raid event, spawns the worst pirates you've ever heard of.
 */
public class PirateGroundRaidEvent extends HordeRaidEvent
{
    /**
     * This raids event id, registry entries use res locations as ids.
     */
    public static final ResourceLocation PIRATE_GROUND_RAID_EVENT_TYPE_ID = new ResourceLocation(Constants.MOD_ID, "pirate_ground_raid");

    public PirateGroundRaidEvent(IColony colony)
    {
        super(colony);
    }

    @Override
    public ResourceLocation getEventTypeID()
    {
        return PIRATE_GROUND_RAID_EVENT_TYPE_ID;
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    protected void updateRaidBar()
    {
        super.updateRaidBar();
        raidBar.setDarkenScreen(true);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
    }

    @Override
    public void registerEntity(final Entity entity)
    {
        if (!(entity instanceof AbstractEntityMinecoloniesRaider) || !entity.isAlive())
        {
            entity.remove(Entity.RemovalReason.DISCARDED);
            return;
        }

        if (entity instanceof EntityCaptainPirateRaider && boss.keySet().size() < horde.numberOfBosses)
        {
            boss.put(entity, entity.getUUID());
            return;
        }

        if (entity instanceof EntityArcherPirateRaider && archers.keySet().size() < horde.numberOfArchers)
        {
            archers.put(entity, entity.getUUID());
            return;
        }

        if (entity instanceof EntityPirateRaider && normal.keySet().size() < horde.numberOfRaiders)
        {
            normal.put(entity, entity.getUUID());
            return;
        }

        entity.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    public void onEntityDeath(final EntityLivingBase entity)
    {
        super.onEntityDeath(entity);
        if (!(entity instanceof AbstractEntityMinecoloniesRaider))
        {
            return;
        }

        if (entity instanceof EntityCaptainPirateRaider)
        {
            boss.remove(entity);
            horde.numberOfBosses--;
        }

        if (entity instanceof EntityArcherPirateRaider)
        {
            archers.remove(entity);
            horde.numberOfArchers--;
        }

        if (entity instanceof EntityPirateRaider)
        {
            normal.remove(entity);
            horde.numberOfRaiders--;
        }

        horde.hordeSize--;

        if (horde.hordeSize == 0)
        {
            status = EventStatus.DONE;
        }

        sendHordeMessage();
    }

    /**
     * Loads the event from the nbt compound.
     *
     * @param colony   colony to load into
     * @param compound NBTcompound with saved values
     * @return the raid event.
     */
    public static PirateGroundRaidEvent loadFromNBT(final IColony colony, final NBTTagCompound compound)
    {
        PirateGroundRaidEvent event = new PirateGroundRaidEvent(colony);
        event.deserializeNBT(compound);
        return event;
    }

    @Override
    public EntityType<?> getNormalRaiderType()
    {
        return PIRATE;
    }

    @Override
    public EntityType<?> getArcherRaiderType()
    {
        return ARCHERPIRATE;
    }

    @Override
    public EntityType<?> getBossRaiderType()
    {
        return CHIEFPIRATE;
    }

    @Override
    protected String getDisplayName()
    {
        return String.translatable(RAID_PIRATE);
    }
}




