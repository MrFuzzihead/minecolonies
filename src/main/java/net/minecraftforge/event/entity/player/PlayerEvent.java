package net.minecraftforge.event.entity.player;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;

/** [1.7.10 bridge] PlayerEvent - maps to Forge 1.7.10 PlayerEvent and provides entityPlayer field */
public class PlayerEvent extends Event
{
    /** The player involved in this event (matches real FML PlayerEvent.entityPlayer). */
    public final EntityPlayer entityPlayer;

    public PlayerEvent(EntityPlayer p) { this.entityPlayer = p; }

    public static class PlayerChangedDimensionEvent extends PlayerEvent
    {
        public PlayerChangedDimensionEvent(EntityPlayer p) { super(p); }
        public EntityPlayer getEntity() { return entityPlayer; }
    }

    public static class PlayerLoggedInEvent extends PlayerEvent
    {
        public PlayerLoggedInEvent(EntityPlayer p) { super(p); }
        public EntityPlayer getEntity() { return entityPlayer; }
    }

    public static class PlayerLoggedOutEvent extends PlayerEvent
    {
        public PlayerLoggedOutEvent(EntityPlayer p) { super(p); }
        public EntityPlayer getEntity() { return entityPlayer; }
    }
}
