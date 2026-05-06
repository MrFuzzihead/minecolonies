package com.minecolonies.core.colony.events.raid.barbarianEvent;
import net.minecraft.world.entity.EntityType;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.colonyEvents.EventStatus;
import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesRaider;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.colony.events.raid.HordeRaidEvent;
import com.minecolonies.core.entity.mobs.raider.barbarians.EntityArcherBarbarianRaider;
import com.minecolonies.core.entity.mobs.raider.barbarians.EntityBarbarianRaider;
import com.minecolonies.core.entity.mobs.raider.barbarians.EntityChiefBarbarianRaider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
// [1.7.10] world.entity removed
import net.minecraft.entity.EntityLivingBase;

import static com.minecolonies.api.entity.ModEntities.*;
import static com.minecolonies.api.util.constant.TranslationConstants.RAID_BARBARIAN;

/**
 * Barbarian raid event for the colony, triggers a horde of barbarians which spawn and attack the colony.
 */
public class BarbarianRaidEvent extends HordeRaidEvent
{
    /**
     * This raids event id, registry entries use res locations as ids.
     */
    public static final ResourceLocation BARBARIAN_RAID_EVENT_TYPE_ID = new ResourceLocation(Constants.MOD_ID, "barbarian_raid");

    public BarbarianRaidEvent(IColony colony)
    {
        super(colony);
    }

    @Override
    public ResourceLocation getEventTypeID()
    {
        return BARBARIAN_RAID_EVENT_TYPE_ID;
    }

    @Override
    public void registerEntity(final Entity entity)
    {
        if (!(entity instanceof AbstractEntityMinecoloniesRaider) || !entity.isAlive())
        {
            entity.remove(Entity.RemovalReason.DISCARDED);
            return;
        }

        if (entity instanceof EntityChiefBarbarianRaider && boss.keySet().size() < horde.numberOfBosses)
        {
            boss.put(entity, entity.getUUID());
            return;
        }

        if (entity instanceof EntityArcherBarbarianRaider && archers.keySet().size() < horde.numberOfArchers)
        {
            archers.put(entity, entity.getUUID());
            return;
        }

        if (entity instanceof EntityBarbarianRaider && normal.keySet().size() < horde.numberOfRaiders)
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

        if (entity instanceof EntityChiefBarbarianRaider)
        {
            boss.remove(entity);
            horde.numberOfBosses--;
        }

        if (entity instanceof EntityArcherBarbarianRaider)
        {
            archers.remove(entity);
            horde.numberOfArchers--;
        }

        if (entity instanceof EntityBarbarianRaider)
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
    public static BarbarianRaidEvent loadFromNBT(final IColony colony, final NBTTagCompound compound)
    {
        BarbarianRaidEvent event = new BarbarianRaidEvent(colony);
        event.deserializeNBT(compound);
        return event;
    }

    @Override
    public EntityType<?> getNormalRaiderType()
    {
        return BARBARIAN;
    }

    @Override
    public EntityType<?> getArcherRaiderType()
    {
        return ARCHERBARBARIAN;
    }

    @Override
    public EntityType<?> getBossRaiderType()
    {
        return CHIEFBARBARIAN;
    }

    @Override
    protected String getDisplayName()
    {
        return String.translatable(RAID_BARBARIAN);
    }
}




