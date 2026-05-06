package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/** [1.7.10 bridge] PlayerEvent - maps to various Forge 1.7.10 player events */
public class PlayerEvent
{
    public static class PlayerChangedDimensionEvent
    {
        public final EntityPlayer player;
        public PlayerChangedDimensionEvent(EntityPlayer p) { this.player = p; }
        public EntityPlayer getEntity() { return player; }
    }

    public static class PlayerLoggedInEvent
    {
        public final EntityPlayer player;
        public PlayerLoggedInEvent(EntityPlayer p) { this.player = p; }
        public EntityPlayer getEntity() { return player; }
    }

    public static class PlayerLoggedOutEvent
    {
        public final EntityPlayer player;
        public PlayerLoggedOutEvent(EntityPlayer p) { this.player = p; }
        public EntityPlayer getEntity() { return player; }
    }
}

