package com.minecolonies.core.colony.events.raid.amazonevent;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.colonyEvents.EventStatus;
import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesRaider;
import com.minecolonies.api.sounds.RaidSounds;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.colony.events.raid.HordeRaidEvent;
import com.minecolonies.core.entity.mobs.raider.amazons.EntityAmazonChiefRaider;
import com.minecolonies.core.entity.mobs.raider.amazons.EntityAmazonSpearmanRaider;
import com.minecolonies.core.entity.mobs.raider.amazons.EntityArcherAmazonRaider;
import com.minecolonies.core.network.messages.client.PlayAudioMessage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;
// [1.7.10] sounds removed
import net.minecraft.entity.Entity;
// [1.7.10] world.entity removed
import net.minecraft.entity.EntityLivingBase;

import static com.minecolonies.api.entity.ModEntities.*;
import static com.minecolonies.api.util.constant.TranslationConstants.RAID_AMAZON;

/**
 * Amazon raid event for the colony, triggers a horde of amazons that spawn and attack the colony.
 */
public class AmazonRaidEvent extends HordeRaidEvent
{
    /**
     * This raids event id, registry entries use res locations as ids.
     */
    public static final ResourceLocation AMAZON_RAID_EVENT_TYPE_ID = new ResourceLocation(Constants.MOD_ID, "amazon_raid");

    /**
     * Cooldown for the music, to not play it too much/not overlap with itself
     */
    private int musicCooldown = 0;

    public AmazonRaidEvent(IColony colony)
    {
        super(colony);
    }

    @Override
    public ResourceLocation getEventTypeID()
    {
        return AMAZON_RAID_EVENT_TYPE_ID;
    }

    @Override
    public void registerEntity(final Entity entity)
    {
        if (!(entity instanceof AbstractEntityMinecoloniesRaider) || !entity.isAlive())
        {
            entity.remove(Entity.RemovalReason.DISCARDED);
            return;
        }

        if (entity instanceof EntityAmazonChiefRaider && boss.keySet().size() < horde.numberOfBosses)
        {
            boss.put(entity, entity.getUUID());
            return;
        }

        if (entity instanceof EntityArcherAmazonRaider && archers.keySet().size() < horde.numberOfArchers)
        {
            archers.put(entity, entity.getUUID());
            return;
        }

        if (entity instanceof EntityAmazonSpearmanRaider && normal.keySet().size() < horde.numberOfRaiders)
        {
            normal.put(entity, entity.getUUID());
            return;
        }

        entity.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    protected void updateRaidBar()
    {
        super.updateRaidBar();
        raidBar.setCreateWorldFog(true);
    }

    @Override
    protected String getDisplayName()
    {
        return String.translatable(RAID_AMAZON);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (--musicCooldown <= 0)
        {
            PlayAudioMessage.sendToAll(getColony(), true, true, new PlayAudioMessage(RaidSounds.AMAZON_RAID, SoundSource.HOSTILE));
            musicCooldown = 20;
        }
    }

    @Override
    public void onEntityDeath(final EntityLivingBase entity)
    {
        super.onEntityDeath(entity);
        if (!(entity instanceof AbstractEntityMinecoloniesRaider))
        {
            return;
        }

        if (entity instanceof EntityAmazonChiefRaider)
        {
            boss.remove(entity);
            horde.numberOfBosses--;
        }

        if (entity instanceof EntityArcherAmazonRaider)
        {
            archers.remove(entity);
            horde.numberOfArchers--;
        }

        if (entity instanceof EntityAmazonSpearmanRaider)
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
    public static AmazonRaidEvent loadFromNBT(final IColony colony, final NBTTagCompound compound)
    {
        AmazonRaidEvent event = new AmazonRaidEvent(colony);
        event.deserializeNBT(compound);
        return event;
    }

    @Override
    public EntityType<?> getNormalRaiderType()
    {
        return AMAZONSPEARMAN;
    }

    @Override
    public EntityType<?> getArcherRaiderType()
    {
        return AMAZON;
    }

    @Override
    public EntityType<?> getBossRaiderType()
    {
        return AMAZONCHIEF;
    }
}





