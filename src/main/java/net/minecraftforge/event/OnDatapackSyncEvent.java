package net.minecraftforge.event;

import net.minecraft.entity.player.EntityPlayerMP;

/** [1.7.10 stub] OnDatapackSyncEvent - no 1.7.10 equivalent */
public class OnDatapackSyncEvent
{
    private final EntityPlayerMP player;
    public OnDatapackSyncEvent(EntityPlayerMP p) { this.player = p; }
    /** @return the player to sync to, or null for all players */
    public EntityPlayerMP getPlayer() { return player; }
}

