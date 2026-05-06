package net.minecraft.server;

import net.minecraft.world.BossEvent;

/**
 * [1.7.10 stub] ServerBossEvent — no equivalent in 1.7.10; boss bars are not supported.
 */
public class ServerBossEvent extends BossEvent
{
    private String name;

    public ServerBossEvent(final String name, final BossBarColor color, final BossBarOverlay overlay)
    {
        this.name = name;
    }

    public void setName(final String name) { this.name = name; }
    public String getName() { return name; }
    public void setVisible(final boolean visible) { }
    public void setProgress(final float progress) { }
    public void addPlayer(final net.minecraft.entity.player.EntityPlayerMP player) { }
    public void removePlayer(final net.minecraft.entity.player.EntityPlayerMP player) { }
    public void removeAllPlayers() { }
}

